# UTR API Specification

This document provides detailed information about the UTR API endpoints available in the MatchApp application. These endpoints allow you to access and manipulate Universal Tennis Rating (UTR) data, including events, leagues, teams, and player ratings.

## Base URL

All UTR API endpoints are prefixed with `/utr`.

## Cross-Origin Resource Sharing (CORS)

All endpoints support CORS with `@CrossOrigin(origins = "*")`, allowing requests from any origin.

## API Endpoints

### Get Events

```
GET /utr/events
```

**Purpose:** Retrieve a list of UTR events based on their status.

**Request Parameters:**
- `status` (query parameter, optional): Filter events by status. Default is `active`.
  - `active`: Return only active events.
  - Any other value: Return all events.

**Response:**
- `200 OK`: Returns a list of `EventEntity` objects.
- `404 Not Found`: If no events match the criteria.

### Get League

```
GET /utr/leagues/{id}
```

**Purpose:** Retrieve detailed information about a specific UTR league.

**Request Parameters:**
- `id` (path variable): The unique identifier of the league.

**Response:**
- `200 OK`: Returns a `League` object containing league details.
- `404 Not Found`: If no league with the specified ID exists.

### Get Team

```
GET /utr/teams/{id}
```

**Purpose:** Retrieve detailed information about a specific UTR team.

**Request Parameters:**
- `id` (path variable): The unique identifier of the team.

**Response:**
- `200 OK`: Returns a `Team` object containing team details.
- `404 Not Found`: If no team with the specified ID exists.

### Get Candidate Team

```
GET /utr/candidateTeams/{id}
```

**Purpose:** Retrieve a candidate team by ID.

**Request Parameters:**
- `id` (path variable): The unique identifier of the candidate team.

**Response:**
- `200 OK`: Returns a `CandidateTeam` object containing team details.
- `404 Not Found`: If no candidate team with the specified ID exists.

### Update Candidates UTR

```
GET /utr/candidateTeams/{id}/utrs
```

**Purpose:** Update UTR values for players in a candidate team.

**Request Parameters:**
- `id` (path variable): The unique identifier of the division containing the candidate team.
- `action` (query parameter): The action to perform:
  - `refreshValue`: Refresh UTR values for all candidates in the team.

**Response:**
- `200 OK`: Returns the updated `CandidateTeam` object.
- `404 Not Found`: If the division doesn't exist or the action is invalid.

### Export Division to Excel

```
GET /utr/exportExcel/divisions/{divisionId}
```

**Purpose:** Export division candidate information to an Excel file.

**Request Parameters:**
- `divisionId` (path variable): The unique identifier of the division.

**Response:**
- Returns a `ModelAndView` object that triggers an Excel file download containing the candidate team information.

### Export Team to Excel

```
GET /utr/exportExcel/team/{teamId}
```

**Purpose:** Export team information to an Excel file.

**Request Parameters:**
- `teamId` (path variable): The unique identifier of the team.

**Response:**
- Returns a `ModelAndView` object that triggers an Excel file download containing the team information.

### Add Candidate to Division

```
PUT /utr/divisions/{id}/candidate/{utrid}
```

**Purpose:** Add a candidate player to a division.

**Request Parameters:**
- `id` (path variable): The unique identifier of the division.
- `utrid` (path variable): The UTR ID of the player to add as a candidate.

**Response:**
- `200 OK`: Returns the updated `DivisionEntity` object.
- `404 Not Found`: If the division doesn't exist.

## Data Models

The API uses the following primary data models:

- `EventEntity`: Represents a UTR event with its details.
- `League`: Represents a UTR league containing divisions and teams.
- `Team`: Represents a UTR team with its players.
- `CandidateTeam`: Represents a team of candidate players for a division.
- `DivisionEntity`: Represents a division within a league.
- `UTRTeamEntity`: Represents the database entity for a UTR team.

## Error Handling

All endpoints follow a consistent error handling pattern:

- `200 OK`: Returned when the request is successful.
- `404 Not Found`: Returned when the requested resource doesn't exist.

Some endpoints may return additional status codes for specific error conditions.
