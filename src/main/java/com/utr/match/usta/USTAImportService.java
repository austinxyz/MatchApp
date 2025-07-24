package com.utr.match.usta;

import com.utr.match.entity.USTAImportProgress;
import com.utr.match.entity.USTATeamEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Service
public class USTAImportService {

    private static final Logger logger = LoggerFactory.getLogger(USTAImportService.class);
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, USTAImportProgress> progressMap = new ConcurrentHashMap<>();
    private final Map<Long, Consumer<String>> debugCallbacks = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Autowired
    private USTATeamImportor importor;

    /**
     * Start a new import process and return the import ID
     * @param teamLink The link to the team to import
     * @return The ID of the import process
     */
    public Long startImport(String teamLink) {
        Long importId = idCounter.getAndIncrement();
        USTAImportProgress progress = new USTAImportProgress("initializing", 0, "Starting import process...");
        progress.setId(importId);
        progress.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        progress.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        progressMap.put(importId, progress);
        
        // Start the import process asynchronously
        executor.submit(() -> {
            try {
                processImport(importId, teamLink);
            } catch (Exception e) {
                logger.error("Error during import process", e);
                updateProgress(importId, "error", 0, "Error during import: " + e.getMessage());
                completeWithError(importId, e.getMessage());
            }
        });
        
        return importId;
    }

    /**
     * Process the import in steps, updating progress along the way
     * @param importId The ID of the import process
     * @param teamLink The link to the team to import
     */
    private void processImport(Long importId, String teamLink) {
        try {
            // Register a debug callback for this import
            Consumer<String> debugCallback = (message) -> sendDebugEvent(importId, message);
            debugCallbacks.put(importId, debugCallback);
            
            // Set the debug callback on the importor
            importor.setDebugCallback(debugCallback);
            
            // Step 1: Fetch team data (25%)
            updateProgress(importId, "fetching_team_data", 10, "Retrieving team information...");
            
            // Step 2: Process player information (50%)
            updateProgress(importId, "processing_player_information", 25, "Processing player information...");
            sendDebugEvent(importId, "Starting to create team and add players from link: " + teamLink);
            USTATeamEntity teamEntity = importor.createTeamAndAddPlayers(teamLink);
            
            if (teamEntity == null) {
                throw new RuntimeException("Failed to create team from link: " + teamLink);
            }
            
            // Step 3: Update player USTA numbers (75%)
            updateProgress(importId, "updating_player_information", 50, "Updating player USTA information...");
            sendDebugEvent(importId, "Starting to update player USTA numbers");
            teamEntity = importor.updatePlayerUSTANumber(teamEntity);
            
            if (teamEntity == null) {
                throw new RuntimeException("Failed to update player USTA numbers");
            }
            
            // Step 4: Complete import (100%)
            updateProgress(importId, "completing_import", 75, "Finalizing import process...");
            
            // Complete the import
            completeWithSuccess(importId, teamEntity);
            
        } catch (Exception e) {
            logger.error("Error during import", e);
            completeWithError(importId, e.getMessage());
        } finally {
            // Remove the debug callback
            debugCallbacks.remove(importId);
            // Clear the debug callback on the importor
            importor.setDebugCallback(null);
        }
    }

    /**
     * Update the progress of an import
     * @param importId The ID of the import process
     * @param step The current step
     * @param progress The progress percentage (0-100)
     * @param message A message describing the current status
     */
    private void updateProgress(Long importId, String step, int progress, String message) {
        USTAImportProgress importProgress = progressMap.get(importId);
        if (importProgress == null) {
            throw new RuntimeException("Import not found");
        }
        
        importProgress.setStep(step);
        importProgress.setProgress(progress);
        importProgress.setMessage(message);
        importProgress.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Send SSE event
        sendProgressEvent(importId, importProgress);
    }

    /**
     * Mark an import as completed successfully
     * @param importId The ID of the import process
     * @param teamEntity The imported team entity
     */
    private void completeWithSuccess(Long importId, USTATeamEntity teamEntity) {
        USTAImportProgress importProgress = progressMap.get(importId);
        if (importProgress == null) {
            throw new RuntimeException("Import not found");
        }
        
        importProgress.markAsCompleted();
        importProgress.setMessage("Import completed successfully");
        importProgress.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Send completion event
        sendCompletionEvent(importId, teamEntity);
        
        // Close the emitter
        closeEmitter(importId);
    }

    /**
     * Mark an import as failed with an error
     * @param importId The ID of the import process
     * @param errorMessage The error message
     */
    private void completeWithError(Long importId, String errorMessage) {
        USTAImportProgress importProgress = progressMap.get(importId);
        if (importProgress == null) {
            throw new RuntimeException("Import not found");
        }
        
        importProgress.markAsError(errorMessage);
        importProgress.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Send error event
        sendErrorEvent(importId, errorMessage);
        
        // Close the emitter
        closeEmitter(importId);
    }

    /**
     * Register a new SSE emitter for an import
     * @param importId The ID of the import process
     * @return The SSE emitter
     */
    public SseEmitter registerEmitter(Long importId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        // Set completion callbacks
        emitter.onCompletion(() -> emitters.remove(importId));
        emitter.onTimeout(() -> emitters.remove(importId));
        emitter.onError(e -> {
            logger.error("Error in SSE emitter", e);
            emitters.remove(importId);
        });
        
        // Store the emitter
        emitters.put(importId, emitter);
        
        // Send initial event
        USTAImportProgress progress = progressMap.get(importId);
        if (progress == null) {
            throw new RuntimeException("Import not found");
        }
        
        try {
            emitter.send(SseEmitter.event()
                    .name("progress")
                    .data(Map.of(
                            "step", progress.getStep(),
                            "progress", progress.getProgress(),
                            "message", progress.getMessage(),
                            "status", progress.getStatus()
                    )));
        } catch (IOException e) {
            logger.error("Error sending initial SSE event", e);
            emitter.completeWithError(e);
        }
        
        return emitter;
    }

    /**
     * Send a progress event to the SSE emitter
     * @param importId The ID of the import process
     * @param progress The progress information
     */
    private void sendProgressEvent(Long importId, USTAImportProgress progress) {
        SseEmitter emitter = emitters.get(importId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("progress")
                        .data(Map.of(
                                "step", progress.getStep(),
                                "progress", progress.getProgress(),
                                "message", progress.getMessage(),
                                "status", progress.getStatus()
                        )));
            } catch (IOException e) {
                logger.error("Error sending progress SSE event", e);
                emitter.completeWithError(e);
                emitters.remove(importId);
            }
        }
    }

    /**
     * Send a completion event to the SSE emitter
     * @param importId The ID of the import process
     * @param teamEntity The imported team entity
     */
    private void sendCompletionEvent(Long importId, USTATeamEntity teamEntity) {
        SseEmitter emitter = emitters.get(importId);
        if (emitter != null) {
            try {
                // Ensure teamEntity is not null before accessing its properties
                if (teamEntity != null) {
                    emitter.send(SseEmitter.event()
                            .name("complete")
                            .data(Map.of(
                                    "teamId", teamEntity.getId(),
                                    "teamName", teamEntity.getName(),
                                    "message", "Import completed successfully"
                            )));
                } else {
                    // If teamEntity is null, send a generic completion message
                    emitter.send(SseEmitter.event()
                            .name("complete")
                            .data(Map.of(
                                    "message", "Import completed successfully, but team details are not available"
                            )));
                }
            } catch (IOException e) {
                logger.error("Error sending completion SSE event", e);
                emitter.completeWithError(e);
                emitters.remove(importId);
            }
        }
    }

    /**
     * Send an error event to the SSE emitter
     * @param importId The ID of the import process
     * @param errorMessage The error message
     */
    private void sendErrorEvent(Long importId, String errorMessage) {
        SseEmitter emitter = emitters.get(importId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data(Map.of(
                                "message", errorMessage
                        )));
            } catch (IOException e) {
                logger.error("Error sending error SSE event", e);
                emitter.completeWithError(e);
                emitters.remove(importId);
            }
        }
    }
    
    /**
     * Send a debug event to the SSE emitter
     * @param importId The ID of the import process
     * @param message The debug message
     */
    private void sendDebugEvent(Long importId, String message) {
        SseEmitter emitter = emitters.get(importId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("debug")
                        .data(Map.of(
                                "message", message,
                                "timestamp", System.currentTimeMillis()
                        )));
            } catch (IOException e) {
                logger.error("Error sending debug SSE event", e);
                // Don't complete with error for debug messages
                // Just log the error and continue
            }
        }
    }

    /**
     * Close an SSE emitter
     * @param importId The ID of the import process
     */
    private void closeEmitter(Long importId) {
        SseEmitter emitter = emitters.get(importId);
        if (emitter != null) {
            emitter.complete();
            emitters.remove(importId);
        }
    }

    /**
     * Get the progress of an import
     * @param importId The ID of the import process
     * @return The progress information
     */
    public USTAImportProgress getProgress(Long importId) {
        USTAImportProgress progress = progressMap.get(importId);
        if (progress == null) {
            throw new RuntimeException("Import not found");
        }
        return progress;
    }
}
