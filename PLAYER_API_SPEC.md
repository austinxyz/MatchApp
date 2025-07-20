# Player API Specification

This document provides detailed information about the Player API endpoints available in the MatchApp application. These endpoints allow you to access and manipulate player data, including searching, retrieving, and updating player information.

## Base URL

All Player API endpoints are prefixed with `/players`.

## Cross-Origin Resource Sharing (CORS)

All endpoints support CORS with `@CrossOrigin(origins = "*")`, allowing requests from any origin.

## API Endpoints

### Get Player by ID

```
GET /players/{id}
```

**Purpose:** Retrieve detailed information about a specific player.

**Request Parameters:**
- `id` (path variable): The unique identifier of the player.
- `action` (query parameter): The action to perform:
  - `updateUTRId`: Update the player's UTR ID.
  - `updateDR`: Update the player's dynamic rating.

**Response:**
- `200 OK`: Returns a `PlayerEntity` object containing player details.
- `404 Not Found`: If no player with the specified ID exists.

### Get Player Teams

```
GET /players/{id}/teams
```

**Purpose:** Retrieve all teams associated with a specific player.

**Request Parameters:**
- `id` (path variable): The unique identifier of the player.

**Response:**
- `200 OK`: Returns a list of `USTATeamMemberPO` objects representing the player's team memberships.
- `404 Not Found`: If no teams are found for the player or the player doesn't exist.

### Search Players by Name

```
GET /players/search
```

**Purpose:** Search for players based on a name query.

**Request Parameters:**
- `name` (query parameter): The name to search for.

**Response:**
- `200 OK`: Returns a list of `PlayerEntity` objects matching the search criteria.
- `404 Not Found`: If no players match the search criteria.

### Search Players by UTR

```
GET /players/searchUTR
```

**Purpose:** Search for players based on UTR and other criteria.

**Request Parameters:**
- `USTARating` (query parameter): The USTA rating to search for.
- `utrLimit` (query parameter, optional): The upper limit for UTR values. Default is `16.0`.
- `utr` (query parameter, optional): The UTR value to search for. Default is `0.0`.
- `type` (query parameter, optional): The type of UTR (singles or doubles). Default is `double`.
- `gender` (query parameter, optional): The gender to filter by. Default is `M`.
- `ageRange` (query parameter): The age range to filter by.
- `ratedOnly` (query parameter, optional): Whether to include only rated players. Default is `false`.
- `start` (query parameter, optional): The starting index for pagination. Default is `0`.
- `size` (query parameter, optional): The number of results to return. Default is `10`.
- `asc` (query parameter, optional): Whether to sort in ascending order. Default is `false`.
- `bayArea` (query parameter, optional): Whether to filter for Bay Area players. Default is `false`.

**Response:**
- `200 OK`: Returns a list of `PlayerEntity` objects matching the search criteria.
- `404 Not Found`: If no players match the search criteria.

### Get UTR Statistics

```
GET /players/statUTR
```

**Purpose:** Get statistical information about UTR values for players matching certain criteria.

**Request Parameters:**
- `USTARating` (query parameter): The USTA rating to filter by.
- `ratedOnly` (query parameter, optional): Whether to include only rated players. Default is `false`.
- `ignoreZeroUTR` (query parameter, optional): Whether to ignore players with zero UTR. Default is `false`.
- `type` (query parameter, optional): The type of UTR (singles or doubles). Default is `double`.
- `gender` (query parameter, optional): The gender to filter by. Default is `M`.
- `ageRange` (query parameter): The age range to filter by.

**Response:**
- `200 OK`: Returns a map of statistical data about UTR values.
- `404 Not Found`: If no data is available for the specified criteria.

### Create Player

```
POST /players/
```

**Purpose:** Create a new player.

**Request Body:**
- `PlayerEntity` object containing the player's information.

**Response:**
- `200 OK`: Returns the newly created `PlayerEntity` object.

### Get Player by UTR ID

```
GET /players/utr/{id}
```

**Purpose:** Retrieve or update a player by their UTR ID.

**Request Parameters:**
- `id` (path variable): The UTR ID of the player.
- `action` (query parameter, optional): The action to perform:
  - `search` (default): Search for the player.
  - `refreshUTRValue`: Refresh the player's UTR value from the UTR API.
  - `updateUTRValue`: Update the player's UTR value manually.
- `dutr` (query parameter, optional): The doubles UTR value to set (used with `updateUTRValue`). Default is `0.0`.
- `sutr` (query parameter, optional): The singles UTR value to set (used with `updateUTRValue`). Default is `0.0`.

**Response:**
- `200 OK`: Returns the `PlayerEntity` object.
- `404 Not Found`: If no player with the specified UTR ID exists or the action is invalid.

### Update Player

```
PUT /players/{id}
```

**Purpose:** Update an existing player's information.

**Request Parameters:**
- `id` (path variable): The unique identifier of the player.

**Request Body:**
- `PlayerEntity` object containing the updated player information.

**Response:**
- `200 OK`: Returns the updated `PlayerEntity` object.
- `404 Not Found`: If the player doesn't exist.

### Get Player by USTA NorCal ID

```
GET /players/usta/{norcalId}
```

**Purpose:** Retrieve a player by their USTA NorCal ID.

**Request Parameters:**
- `norcalId` (path variable): The USTA NorCal ID of the player.
- `action` (query parameter, optional): The action to perform. Currently only supports `search` (default).

**Response:**
- `200 OK`: Returns the `PlayerEntity` object.
- `404 Not Found`: If no player with the specified NorCal ID exists or the action is invalid.

## Data Models

The API uses the following primary data models:

- `PlayerEntity`: Represents a player with their UTR and USTA information.
- `USTATeamMemberPO`: Plain object representation of a player's team membership.

## Error Handling

All endpoints follow a consistent error handling pattern:

- `200 OK`: Returned when the request is successful.
- `404 Not Found`: Returned when the requested resource doesn't exist.

Some endpoints may return additional status codes for specific error conditions.
