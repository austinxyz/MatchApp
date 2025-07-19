package com.utr.match;

import com.utr.UTRApiConfig;
import com.utr.parser.UTRParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for UTR API management.
 * Provides endpoints for checking token status and getting update instructions.
 */
@RestController
@RequestMapping("/api/utr")
public class UTRApiController {
    private static final Logger logger = LoggerFactory.getLogger(UTRApiController.class);
    
    @Autowired
    private UTRParser utrParser;
    
    @Autowired
    private UTRApiConfig utrApiConfig;
    
    /**
     * Check if the UTR API token is valid.
     * 
     * @return A response with token status and update instructions if needed
     */
    @GetMapping("/token/status")
    public ResponseEntity<Map<String, Object>> checkTokenStatus() {
        Map<String, Object> response = new HashMap<>();
        
        // Use a known player ID to test the token
        String testPlayerId = "2547696"; // This is the same ID used in AppInitBean
        boolean isExpired = utrParser.isTokenExpired(testPlayerId);
        
        response.put("tokenValid", !isExpired);
        
        if (isExpired) {
            logger.warn("UTR API token is expired or invalid");
            response.put("message", "UTR API token is expired or invalid");
            response.put("updateInstructions", utrParser.getTokenUpdateInstructions());
        } else {
            logger.info("UTR API token is valid");
            response.put("message", "UTR API token is valid");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get instructions for updating the UTR API token.
     * 
     * @return A response with update instructions
     */
    @GetMapping("/token/update-instructions")
    public ResponseEntity<Map<String, Object>> getTokenUpdateInstructions() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("updateInstructions", utrParser.getTokenUpdateInstructions());
        
        return ResponseEntity.ok(response);
    }
}
