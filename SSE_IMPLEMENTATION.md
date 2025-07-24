# Server-Sent Events (SSE) Implementation for USTA Team Import

This document describes the implementation of Server-Sent Events (SSE) for the USTA team import API, which allows real-time progress tracking during the import process.

## Overview

The implementation adds SSE support to the existing USTA team import functionality, enabling the frontend to receive real-time updates about the progress of the import process. This improves the user experience by providing immediate feedback on long-running operations.

## API Endpoints

### 1. Initiate Import

```
POST /usta/teams/import
```

This endpoint initiates the import process and returns an import ID that can be used to track the progress.

**Request Body:**
```json
{
  "link": "https://leagues.ustanorcal.com/listplayers.asp?teamid=1234"
}
```

**Response:**
```json
{
  "importId": 123
}
```

### 2. Track Import Progress (SSE)

```
GET /usta/teams/import/{id}/progress
```

This is an SSE endpoint that provides real-time updates on the import progress. The client can establish an EventSource connection to this endpoint to receive progress events.

**Events:**

- `progress`: Sent periodically to update the progress percentage and status message.
  ```
  event: progress
  data: {"step": "fetching_team_data", "progress": 25, "message": "Retrieving team information...", "status": "in_progress"}
  ```

- `debug`: Sent to provide detailed information about the import process, such as player information being processed.
  ```
  event: debug
  data: {"message": "Player:Indick Hazel usta Rating: 2010046918 Saved", "timestamp": 1626912345678}
  ```

- `complete`: Sent when the import is completed successfully.
  ```
  event: complete
  data: {"teamId": 123, "teamName": "Team Name", "message": "Import completed successfully"}
  ```

- `error`: Sent when an error occurs during the import.
  ```
  event: error
  data: {"message": "Error message"}
  ```

### 3. Get Import Status

```
GET /usta/teams/import/{id}
```

This endpoint retrieves the current status of an import process.

**Response:**
```json
{
  "id": 123,
  "step": "fetching_team_data",
  "progress": 25,
  "message": "Retrieving team information...",
  "status": "in_progress",
  "createdAt": "2025-07-23T23:45:00.000Z",
  "updatedAt": "2025-07-23T23:45:10.000Z"
}
```

## Implementation Details

### Backend Components

1. **USTAImportProgress Class**: Simple POJO that holds the progress information for each import process.
2. **USTAImportService**: Service that handles the import process and SSE emitters, using an in-memory approach with ConcurrentHashMap to store progress information.
3. **USTAController**: Controller that exposes the SSE endpoints.

### Frontend Demo

A demo page is available at `/demo/sse` that demonstrates how to use the SSE API from the frontend. The demo includes:

- A form to enter a USTA team link
- A progress bar that updates in real-time
- A log of events received from the server

## How to Use

### Backend Integration

1. Inject the `USTAImportService` into your service or controller:
   ```java
   @Autowired
   private USTAImportService importService;
   ```

2. Start an import process:
   ```java
   Long importId = importService.startImport(teamLink);
   ```

3. Register an SSE emitter for the import:
   ```java
   SseEmitter emitter = importService.registerEmitter(importId);
   ```

### Frontend Integration

1. Start the import process:
   ```javascript
   fetch('/usta/teams/import', {
       method: 'POST',
       headers: {
           'Content-Type': 'application/json'
       },
       body: JSON.stringify({
           link: teamLink
       })
   })
   .then(response => response.json())
   .then(data => {
       const importId = data.importId;
       // Connect to SSE endpoint
       const eventSource = new EventSource(`/usta/teams/import/${importId}/progress`);
       // Handle events
       // ...
   });
   ```

2. Handle SSE events:
   ```javascript
   eventSource.addEventListener('progress', function(event) {
       const data = JSON.parse(event.data);
       // Update UI with progress information
   });
   
   eventSource.addEventListener('debug', function(event) {
       const data = JSON.parse(event.data);
       // Display detailed debug information
   });
   
   eventSource.addEventListener('complete', function(event) {
       const data = JSON.parse(event.data);
       // Handle completion
       eventSource.close();
   });
   
   eventSource.addEventListener('error', function(event) {
       const data = JSON.parse(event.data);
       // Handle error
       eventSource.close();
   });
   ```

## Error Handling

The implementation includes comprehensive error handling:

1. If an error occurs during the import process, an error event is sent to the client.
2. If the SSE connection is lost, the client can reconnect and continue receiving updates.
3. If the import process fails, the error is logged and the import status is updated to "error".

## Performance Considerations

1. The SSE connection is kept open until the import process is completed or an error occurs.
2. The implementation uses a thread pool to handle multiple concurrent imports.
3. Both SSE emitters and progress information are stored in ConcurrentHashMaps to ensure thread safety.
4. The import process is executed asynchronously to avoid blocking the main thread.
5. Using an in-memory approach instead of a database reduces overhead and improves performance for short-lived progress tracking.
6. Debug messages are sent frequently to keep the connection alive and prevent socket timeout exceptions during long-running imports.

## Demo Page

A demo page is available at `/demo/sse` that demonstrates how to use the SSE API from the frontend. The demo includes:

- A form to enter a USTA team link
- A progress bar that updates in real-time
- A log of events received from the server

To access the demo page, navigate to:
```
http://localhost:8080/demo/sse
