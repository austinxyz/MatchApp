# USTA API Specification

This document provides detailed information about the USTA API endpoints available in the MatchApp application. These endpoints allow you to access and manipulate USTA tennis data, including teams, players, divisions, leagues, matches, and more.

## Base URL

All USTA API endpoints are prefixed with `/usta`.

## Cross-Origin Resource Sharing (CORS)

All endpoints support CORS with `@CrossOrigin(origins = "*")`, allowing requests from any origin.

## API Endpoints

### Teams

#### Get Team by ID

```
GET /usta/teams/{id}
```

**Purpose:** Retrieve detailed information about a specific USTA team.

**Request Parameters:**
- `id` (path variable): The unique identifier of the team.
- `matches` (query parameter, optional): Boolean flag to include match information. Default is `false`.

**Response:**
- `200 OK`: Returns a `USTATeam` object containing team details.
- `404 Not Found`: If no team with the specified ID exists.

#### Search Teams

```
GET /usta/search/teams
```

**Purpose:** Search for USTA teams based on a query string.

**Request Parameters:**
- `query` (query parameter): The search query string.

**Response:**
- `200 OK`: Returns a list of `USTATeam` objects matching the search criteria.
- `404 Not Found`: If no teams match the search criteria.

#### Get Teams by Division

```
GET /usta/divisions/{divId}/teams
```

**Purpose:** Retrieve all teams in a specific division.

**Request Parameters:**
- `divId` (path variable): The unique identifier of the division.

**Response:**
- `200 OK`: Returns a list of `USTATeam` objects in the specified division.
- `404 Not Found`: If no teams are found in the division or the division doesn't exist.

#### Get Teams from USTA Site

```
GET /usta/site/divisions/{id}/teams
```

**Purpose:** Fetch teams directly from the USTA website for a specific division.

**Request Parameters:**
- `id` (path variable): The unique identifier of the division.

**Response:**
- `200 OK`: Returns a list of `USTATeamPO` objects from the USTA site.
- `404 Not Found`: If no teams are found or the division doesn't exist.

#### Create Team

```
POST /usta/teams
```

**Purpose:** Create a new USTA team by importing data from the USTA website.

**Request Body:**
- `USTATeamPO` object containing at minimum the team's link on the USTA website.

**Response:**
- `200 OK`: Returns the newly created `USTATeam` object.

#### Update Players UTR ID

```
GET /usta/teams/{id}/utrs
```

**Purpose:** Update UTR IDs or values for players in a team.

**Request Parameters:**
- `id` (path variable): The unique identifier of the team.
- `action` (query parameter): The action to perform:
  - `refreshID`: Update UTR IDs for team players.
  - `refreshValue`: Update UTR values for team players.

**Response:**
- `200 OK`: Returns the updated `USTATeam` object.
- `404 Not Found`: If the team doesn't exist or the action is invalid.

#### Update Team Players

```
GET /usta/teams/{id}/players
```

**Purpose:** Refresh the player roster for a team.

**Request Parameters:**
- `id` (path variable): The unique identifier of the team.
- `action` (query parameter): Must be `refresh` to update the player list.

**Response:**
- `200 OK`: Returns the updated `USTATeam` object with refreshed player information.
- `404 Not Found`: If the team doesn't exist or the action is invalid.

#### Update Players Dynamic Rating

```
GET /usta/teams/{id}/drs
```

**Purpose:** Update dynamic ratings for players in a team.

**Request Parameters:**
- `id` (path variable): The unique identifier of the team.
- `action` (query parameter): Must be `refresh` to update the dynamic ratings.

**Response:**
- `200 OK`: Returns the updated `USTATeam` object with refreshed dynamic ratings.
- `404 Not Found`: If the team doesn't exist or the action is invalid.

#### Get Team Match Scores

```
GET /usta/teams/{id}/matches
```

**Purpose:** Retrieve match scores for a specific team.

**Request Parameters:**
- `id` (path variable): The unique identifier of the team.
- `action` (query parameter, optional): The action to perform:
  - `fetch` (default): Fetch existing match scores.
  - `updateScore`: Refresh match scores from the USTA website.

**Response:**
- `200 OK`: Returns a list of `USTAMatch` objects.
- `404 Not Found`: If the team doesn't exist or the action is invalid.

#### Get Team Line Statistics

```
GET /usta/teams/{id}/lineStat
```

**Purpose:** Retrieve line statistics for a specific team.

**Request Parameters:**
- `id` (path variable): The unique identifier of the team.

**Response:**
- `200 OK`: Returns a `USTATeam` object with line statistics.
- `404 Not Found`: If the team doesn't exist.

#### Export Team to Excel

```
GET /usta/exportExcel/team/{teamId}
```

**Purpose:** Export team information to an Excel file.

**Request Parameters:**
- `teamId` (path variable): The unique identifier of the team.

**Response:**
- Returns a `ModelAndView` object that triggers an Excel file download.

### Players

#### Get Player UTR Information

```
GET /usta/players/{id}/utrs
```

**Purpose:** Retrieve or update UTR information for a specific player.

**Request Parameters:**
- `id` (path variable): The unique identifier of the player.
- `action` (query parameter, optional): The action to perform:
  - `fetch` (default): Fetch existing UTR information.
  - `refreshUTRId`: Update the player's UTR ID.
  - `refreshUTRValue`: Update the player's UTR value.
  - `refreshDR`: Update the player's dynamic rating.

**Response:**
- `200 OK`: Returns the `PlayerEntity` object with UTR information.
- `404 Not Found`: If the player doesn't exist or the action is invalid.

#### Get Player Scores

```
GET /usta/players/{id}/scores
```

**Purpose:** Retrieve match scores for a specific player.

**Request Parameters:**
- `id` (path variable): The unique identifier of the player.

**Response:**
- `200 OK`: Returns a list of `USTAMatchLinePO` objects containing the player's match scores.
- `404 Not Found`: If the player doesn't exist or has no scores.

### Divisions

#### Get Divisions by League

```
GET /usta/leagues/{id}/divisions
```

**Purpose:** Retrieve all divisions in a specific league.

**Request Parameters:**
- `id` (path variable): The unique identifier of the league.

**Response:**
- `200 OK`: Returns a list of `USTADivision` objects.
- `404 Not Found`: If no divisions are found or the league doesn't exist.

#### Get Divisions by Year

```
GET /usta/{year}/divisions
```

**Purpose:** Retrieve all divisions for a specific year.

**Request Parameters:**
- `year` (path variable): The year to retrieve divisions for.

**Response:**
- `200 OK`: Returns a list of `USTADivision` objects.
- `404 Not Found`: If no divisions are found for the specified year.

#### Get Open Divisions

```
GET /usta/open/divisions
```

**Purpose:** Retrieve all currently open divisions.

**Response:**
- `200 OK`: Returns a list of `USTADivision` objects for open divisions.
- `404 Not Found`: If no open divisions are found.

#### Import Division from USTA Site

```
POST /usta/site/divisions/
```

**Purpose:** Import a division from the USTA website.

**Request Body:**
- `USTADivisionPO` object containing at minimum the USTA league ID and league name.

**Response:**
- `200 OK`: Returns the imported `USTADivisionPO` object.
- `404 Not Found`: If the division cannot be imported.

### Flights

#### Get Flights by Division

```
GET /usta/divisions/{divId}/flights
```

**Purpose:** Retrieve all flights in a specific division.

**Request Parameters:**
- `divId` (path variable): The unique identifier of the division.

**Response:**
- `200 OK`: Returns a list of `USTAFlight` objects.
- `404 Not Found`: If no flights are found or the division doesn't exist.

#### Get Teams by Flight

```
GET /usta/flights/{flightId}/teams
```

**Purpose:** Retrieve all teams in a specific flight.

**Request Parameters:**
- `flightId` (path variable): The unique identifier of the flight.

**Response:**
- `200 OK`: Returns a list of `USTATeam` objects.
- `404 Not Found`: If no teams are found or the flight doesn't exist.

#### Import Flight Teams from USTA Site

```
POST /usta/site/flight/teams
```

**Purpose:** Import teams for a flight from the USTA website.

**Request Body:**
- `USTAFlightPO` object containing at minimum the flight ID and link.

**Response:**
- `200 OK`: Returns a list of imported `USTATeamPO` objects.
- `404 Not Found`: If the teams cannot be imported.

### Leagues

#### Get Leagues by Year

```
GET /usta/{year}/leagues
```

**Purpose:** Retrieve all leagues for a specific year.

**Request Parameters:**
- `year` (path variable): The year to retrieve leagues for.

**Response:**
- `200 OK`: Returns a list of `USTALeague` objects.
- `404 Not Found`: If no leagues are found for the specified year.

#### Get Open Leagues

```
GET /usta/open/leagues
```

**Purpose:** Retrieve all currently open leagues.

**Response:**
- `200 OK`: Returns a list of `USTALeague` objects for open leagues.
- `404 Not Found`: If no open leagues are found.

#### Get Leagues from USTA Site

```
GET /usta/current/leagues
```

**Purpose:** Fetch leagues directly from the USTA website.

**Response:**
- `200 OK`: Returns a list of `USTALeaguePO` objects from the USTA site.
- `404 Not Found`: If no leagues are found.

#### Create League

```
POST /usta/leagues
```

**Purpose:** Create a new USTA league.

**Request Body:**
- `USTALeague` object containing:
  - `name` (required): The name of the league.
  - `year` (required): The year of the league.
  - `status` (optional): The status of the league. Default is "Open".

**Example Request Body:**
```json
{
  "name": "2025 USTA NorCal Adult 18 & Over",
  "year": "2025",
  "status": "Open"
}
```

**Response:**
- `200 OK`: Returns the created `USTALeague` object.
- `400 Bad Request`: If the request body is missing required fields.

### Match Scores

#### Update Line Score Information

```
PUT /usta/score/{id}
```

**Purpose:** Update information for a specific match line score.

**Request Parameters:**
- `id` (path variable): The unique identifier of the match line.

**Request Body:**
- `USTAMatchLine` object containing the updated score information.

**Response:**
- `200 OK`: Returns the updated `USTAMatchLine` object.
- `404 Not Found`: If the match line doesn't exist.

### Team Analysis

#### Compare Teams

```
GET /usta/analysis/team/team1/{teamId1}/team2/{teamId2}
```

**Purpose:** Analyze and compare two teams.

**Request Parameters:**
- `teamId1` (path variable): The unique identifier of the first team.
- `teamId2` (path variable): The unique identifier of the second team.

**Response:**
- `200 OK`: Returns a `USTATeamAnalysisResult` object with the comparison results.
- `404 Not Found`: If either team doesn't exist.

#### Export Team Analysis to Excel

```
GET /usta/exportExcel/team/team1/{teamId1}/team2/{teamId2}
```

**Purpose:** Export team comparison analysis to an Excel file.

**Request Parameters:**
- `teamId1` (path variable): The unique identifier of the first team.
- `teamId2` (path variable): The unique identifier of the second team.

**Response:**
- Returns a `ModelAndView` object that triggers an Excel file download.

### Candidate Teams

#### Get Candidate Teams by Division

```
GET /usta/divisions/{divId}/candidateTeams
```

**Purpose:** Retrieve all candidate teams in a specific division.

**Request Parameters:**
- `divId` (path variable): The unique identifier of the division.

**Response:**
- `200 OK`: Returns a list of `USTACandidateTeam` objects.
- `404 Not Found`: If no candidate teams are found or the division doesn't exist.

#### Get Candidate Team by ID

```
GET /usta/candidateTeams/{id}
```

**Purpose:** Retrieve a specific candidate team.

**Request Parameters:**
- `id` (path variable): The unique identifier of the candidate team.

**Response:**
- `200 OK`: Returns a `USTACandidateTeam` object.
- `404 Not Found`: If the candidate team doesn't exist.

#### Update Candidate Team UTR Values

```
GET /usta/candidateTeams/{id}/utrs
```

**Purpose:** Update UTR values for players in a candidate team.

**Request Parameters:**
- `id` (path variable): The unique identifier of the candidate team.
- `action` (query parameter): Must be `refreshValue` to update UTR values.

**Response:**
- `200 OK`: Returns the updated `USTACandidateTeam` object.
- `404 Not Found`: If the candidate team doesn't exist or the action is invalid.

#### Export Candidate Team to Excel

```
GET /usta/exportExcel/candidateTeam/{id}
```

**Purpose:** Export candidate team information to an Excel file.

**Request Parameters:**
- `id` (path variable): The unique identifier of the candidate team.

**Response:**
- Returns a `ModelAndView` object that triggers an Excel file download.

#### Add Candidate to Team

```
PUT /usta/candidateTeam/{id}/candidate/{utrid}
```

**Purpose:** Add a candidate player to a candidate team.

**Request Parameters:**
- `id` (path variable): The unique identifier of the candidate team.
- `utrid` (path variable): The UTR ID of the player to add.

**Response:**
- `200 OK`: Returns the updated `USTACandidateTeam` object.
- `404 Not Found`: If the candidate team doesn't exist.

## Data Models

The API uses the following primary data models:

- `USTATeam`: Represents a USTA team with its players and match information.
- `USTATeamPO`: Plain object representation of a USTA team, typically used for data transfer.
- `USTADivision`: Represents a USTA division containing flights and teams.
- `USTADivisionPO`: Plain object representation of a USTA division.
- `USTAFlight`: Represents a flight within a division.
- `USTAFlightPO`: Plain object representation of a USTA flight.
- `USTALeague`: Represents a USTA league containing divisions.
- `USTALeaguePO`: Plain object representation of a USTA league.
- `USTAMatch`: Represents a match between two teams.
- `USTAMatchLine`: Represents a specific line (court) within a match.
- `USTAMatchLinePO`: Plain object representation of a match line.
- `PlayerEntity`: Represents a player with their UTR and USTA information.
- `USTACandidateTeam`: Represents a team of candidate players.
- `USTATeamAnalysisResult`: Contains the results of comparing two teams.

## Error Handling

All endpoints follow a consistent error handling pattern:

- `200 OK`: Returned when the request is successful.
- `404 Not Found`: Returned when the requested resource doesn't exist.

Some endpoints may return additional status codes for specific error conditions.
