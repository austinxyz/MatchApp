# ZiJing API Specification

This document provides detailed information about the ZiJing API endpoints available in the MatchApp application. These endpoints are primarily focused on player and team analysis, lineup optimization, and tournament-specific functionality.

## Base URL

ZiJing API endpoints do not have a common prefix and are directly accessible at the root level.

## Cross-Origin Resource Sharing (CORS)

All endpoints support CORS with `@CrossOrigin(origins = "*")`, allowing requests from any origin.

## API Endpoints

### Single Player Analysis

```
GET /analysis/single/player1/{player1}/player2/{player2}
```

**Purpose:** Analyze and compare two individual players.

**Request Parameters:**
- `player1` (path variable): The unique identifier of the first player.
- `player2` (path variable): The unique identifier of the second player.

**Response:**
- `200 OK`: Returns a `SingleAnalysisResult` object containing the comparison results.
- `404 Not Found`: If either player doesn't exist.

### Get Club Information

```
GET /club/{clubId}
```

**Purpose:** Retrieve detailed information about a specific club.

**Request Parameters:**
- `clubId` (path variable): The unique identifier of the club.

**Response:**
- `200 OK`: Returns a `Club` object containing club details.
- `404 Not Found`: If no club with the specified ID exists.

### Search Players

```
GET /search/players
```

**Purpose:** Search for players based on a query string.

**Request Parameters:**
- `query` (query parameter): The search query string.
- `top` (query parameter, optional): The maximum number of results to return. Default is `5`.

**Response:**
- `200 OK`: Returns a list of `Player` objects matching the search criteria.

### Get Event Information

```
GET /event/{eventId}
```

**Purpose:** Retrieve detailed information about a specific event.

**Request Parameters:**
- `eventId` (path variable): The unique identifier of the event.

**Response:**
- `200 OK`: Returns an `Event` object containing event details.
- `404 Not Found`: If no event with the specified ID exists.

### Get Event Team

```
GET /event/{eventId}/team/{teamId}
```

**Purpose:** Retrieve a team participating in a specific event.

**Request Parameters:**
- `eventId` (path variable): The unique identifier of the event.
- `teamId` (path variable): The unique identifier of the team.

**Response:**
- `200 OK`: Returns a `Team` object containing team details.
- `404 Not Found`: If the team doesn't exist in the event or has no players.

### Get All Teams

```
GET /teams
```

**Purpose:** Retrieve all divisions with their teams.

**Response:**
- `200 OK`: Returns a list of `Division` objects containing team information.
- `404 Not Found`: If no teams are found.

### Get Team by Name

```
GET /team
```

**Purpose:** Retrieve a team by its name.

**Request Parameters:**
- `team` (query parameter, optional): The name of the team. Default is `ZJU`.

**Response:**
- `200 OK`: Returns a `Team` object containing team details.
- `404 Not Found`: If the team doesn't exist or has no players.

### Get Player Result

```
GET /playerresult
```

**Purpose:** Retrieve match results for a specific player.

**Request Parameters:**
- `id` (query parameter): The unique identifier of the player.
- `year` (query parameter, optional): The year to retrieve results for. Default is `latest`.

**Response:**
- `200 OK`: Returns a `PlayerResult` object containing the player's match results.
- `404 Not Found`: If the player doesn't exist or has no results.

### Generate Lineup

```
GET /lineup
```

**Purpose:** Generate optimal lineups for a team using a specified strategy.

**Request Parameters:**
- `team` (query parameter, optional): The name of the team. Default is `ZJU`.
- `strategy` (query parameter, optional): The strategy number to use for lineup generation. Default is `0`.

**Response:**
- `200 OK`: Returns a list of `Lineup` objects representing possible lineups.
- `404 Not Found`: If no valid lineups can be generated.

### Generate Fixed Lineup

```
GET /fixedlineup
```

**Purpose:** Generate lineups for a team with fixed player pairs for specific lines.

**Request Parameters:**
- `team` (query parameter, optional): The name of the team. Default is `ZJU-BYD`.
- `d1` (query parameter, optional): Fixed player pairs for D1 line. Default is empty.
- `d2` (query parameter, optional): Fixed player pairs for D2 line. Default is empty.
- `d3` (query parameter, optional): Fixed player pairs for D3 line. Default is empty.
- `md` (query parameter, optional): Fixed player pairs for MD line. Default is empty.
- `wd` (query parameter, optional): Fixed player pairs for WD line. Default is empty.
- `grantUTR` (query parameter, optional): Whether to use granted UTR values. Default is `true`.

**Response:**
- `200 OK`: Returns a list of `Lineup` objects representing possible lineups with the fixed pairs.
- `404 Not Found`: If no valid lineups can be generated.

## Data Models

The API uses the following primary data models:

- `SingleAnalysisResult`: Contains the results of comparing two players.
- `Club`: Represents a tennis club with its details.
- `Player`: Represents a player with their UTR and other information.
- `Event`: Represents a tennis event or tournament.
- `Team`: Represents a team with its players.
- `Division`: Represents a division containing teams.
- `PlayerResult`: Contains match results for a player.
- `Lineup`: Represents a possible lineup for a team.

## Error Handling

All endpoints follow a consistent error handling pattern:

- `200 OK`: Returned when the request is successful.
- `404 Not Found`: Returned when the requested resource doesn't exist.

Some endpoints may return additional status codes for specific error conditions.

## Notes

- The ZiJing API appears to be specialized for tournament and team management, particularly for events like the "ZiJing Cup".
- Many endpoints have default values that reference specific teams (e.g., "ZJU" or "ZJU-BYD"), suggesting this API was initially designed for specific tournaments or organizations.
- The lineup generation functionality uses different strategies for optimizing team performance based on player UTR ratings.
