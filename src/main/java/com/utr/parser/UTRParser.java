package com.utr.parser;

import com.utr.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("singleton")
public class UTRParser {

    public static final String EVENTS_URL = "https://app.universaltennis.com/api/v1/tms/events/";
    public static final String PLAYER_RESULT = "https://app.universaltennis.com/api/v1/player/";
    public static final String PLAYER_PROFILE = "https://app.universaltennis.com/api/v1/player/%s/profile";
    public static final String PLAYER_SEARCH = "https://app.universaltennis.com/api/v2/search/players?query=";
    public static final String CLUB_EVENTS = "https://app.universaltennis.com/api/v1/club/%s/events";
    public static final String CLUB_URL = "https://app.universaltennis.com/api/v1/club/%s";
    public static final String LEAGUE_TEAM_URL = "https://leagues-api.universaltennis.com/v1/leagues/teams/%s/members";
    public static final String LEAGUE_TEAMS_URL = "https://leagues-api.universaltennis.com/v1/leagues/sessions/%s/teams";
    public static final String LEAGUE_SESSION_URL = "https://leagues-api.universaltennis.com/v1/leagues/sessions/%s";
    public static final String LEAGUE_CONFERENCE_URL = "https://leagues-api.universaltennis.com/v1/leagues/conferences/%s";
    public static final String LEAGUE_URL = "https://leagues-api.universaltennis.com/v1/leagues/%s/summary";
    private static final Logger logger = LoggerFactory.getLogger(UTRParser.class);
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNZW1iZXJJZCI6IjE4MTUxOCIsImVtYWlsIjoiYXVzdGluLnh5ekBnbWFpbC5jb20iLCJWZXJzaW9uIjoiMSIsIkRldmljZUxvZ2luSWQiOiIxNjI2MjAxMCIsIm5iZiI6MTY5MjgwOTAzMCwiZXhwIjoxNjk1NDAxMDI5LCJpYXQiOjE2OTI4MDkwMzB9.AciEVUprmbOdQVNsaBjW-_vR4ssWR2WcfC9rzzG854s";
    Map<String, PlayerResult> playerResults;
    Map<String, Player> players;
    Map<String, Event> events;
    Map<String, Club> clubs;

    Map<String, League> leagues;

    private final Map<String, LocalDate> fetchedTimes;

    public UTRParser() {
        playerResults = new HashMap<>();
        events = new HashMap<>();
        clubs = new HashMap<>();
        players = new HashMap<>();
        fetchedTimes = new HashMap<>();
        leagues = new HashMap<>();
    }

    public List<Event> getClubEvents(String clubId, boolean withToken) {
        EventParser eventParser = new EventParser();
        return eventParser.buildEvents(getClubEventsJson(clubId, withToken));
    }

    public Club getClub(String clubId, boolean withToken) {
        ClubParser clubParser = new ClubParser();
        Club club = clubParser.buildClub(getClubJson(clubId, withToken));
        club.getEvents().addAll(getClubEvents(clubId, withToken));
        return club;
    }

    private String getClubJson(String clubId, boolean withToken) {
        String getCallURL
                = String.format(CLUB_URL, clubId);

        return restGetCall(getCallURL, withToken);
    }

    private String getClubEventsJson(String clubId, boolean withToken) {
        String getCallURL
                = String.format(CLUB_EVENTS, clubId);

        return restGetCall(getCallURL, withToken);
    }


    public List<Player> searchPlayers(String query, int totalNumber, boolean withToken) {
        SearchPlayerParser parser = new SearchPlayerParser();
        return parser.buildPlayers(getSearchPlayersJson(query, totalNumber, withToken));
    }

    private String getSearchPlayersJson(String query, int totalNumber, boolean withToken) {
        String getCallURL
                = PLAYER_SEARCH + query + "&top=" + totalNumber;

        return restGetCall(getCallURL, withToken);
    }


    public Event parseEvent(String eventId, boolean withToken) {
        EventParser eventParser = new EventParser();
        return eventParser.buildEvent(getEventJson(eventId, withToken), eventId);
    }

    public PlayerResult parsePlayerResult(String playerId, boolean withToken) {
        return parsePlayerResult(playerId, true, withToken );
    }

    private Player refreshUTR(Player newPlayer) {
        if (newPlayer == null) {
            return null;
        }

        Player player = null;
        if (!players.containsKey(newPlayer.getId())) {
            players.put(newPlayer.getId(), newPlayer);
            player = newPlayer;
        } else {
            player = players.get(newPlayer.getId());
            player.setsUTR(newPlayer.getsUTR());
            player.setdUTR(newPlayer.getdUTR());
        }

        player.setUtrFetchedTime(new Timestamp(System.currentTimeMillis()));

        return player;
    }

    public float getWinPercent(String playerId, boolean latest) {
        PlayerResultParser resultParser = new PlayerResultParser(playerId);
        PlayerResult result = resultParser.parseResult(getResultJson(playerId, latest, true), false);
        if (result.getLossesNumber() + result.getWinsNumber() == 0) {
            return 0.0f;
        }
        return (float) result.getWinsNumber() / (float) (result.getLossesNumber() + result.getWinsNumber());
    }

    public League parseLeague(String leagueId) {
        LeagueParser parser = new LeagueParser();
        League league = parser.buildLeague(getLeagueJson(leagueId));

        for (Conference conf : league.getConferences()) {
            parser.buildSessions(conf, getConfJson(conf.getId()));
            for (Session session: conf.getSessions()) {
                parser.buildTeams(session, getLeagueTeamsJson(session.getId()));
            }
        }
        return league;
    }

    private String getSessionJson(String id) {
        String getCallURL
                = String.format(LEAGUE_SESSION_URL, id);

        return restGetCall(getCallURL, true);
    }

    private String getConfJson(String id) {
        String getCallURL
                = String.format(LEAGUE_CONFERENCE_URL, id);

        return restGetCall(getCallURL, true);
    }

    public UTRTeam parseTeamMembers(League league, String teamId) {
        LeagueParser parser = new LeagueParser();
        UTRTeam team = league.getTeam(teamId);
        if (team!= null) {
            parser.buildTeamPlayers(team, getTeamMemberJson(teamId));
        }
        return team;
    }

    private String getTeamMemberJson(String teamId) {
        String getCallURL
                = String.format(LEAGUE_TEAM_URL, teamId);

        return restGetCall(getCallURL, true);
    }

    private String getLeagueTeamsJson(String sessionId) {
        String getCallURL
                = String.format(LEAGUE_TEAMS_URL, sessionId);

        return restGetCall(getCallURL, true);
    }

    private String getLeagueJson(String leagueId) {
        String getCallURL
                = String.format(LEAGUE_URL, leagueId);

        return restGetCall(getCallURL, true);
    }

    private String getResultJson(String playerId, boolean latest, boolean withToken) {

        String getCallURL
                = PLAYER_RESULT + playerId + "/results";

        if (latest) {
            getCallURL = getCallURL + "?year=last";
        }

        return restGetCall(getCallURL, withToken);

    }

    private Player parsePlayer(String playerId, boolean withToken) {
        PlayerParser resultParser = new PlayerParser(playerId);
        Player player = resultParser.parseResult(getPlayerJson(playerId, withToken));

        return player;
    }

    private String getPlayerJson(String playerId, boolean withToken) {

        String getCallURL
                = String.format(PLAYER_PROFILE, playerId);

        return restGetCall(getCallURL, withToken);

    }

    private String getEventJson(String eventId, boolean withToken) {

        String getCallURL
                = EVENTS_URL + eventId;

        return restGetCall(getCallURL, withToken);

    }

    @Retryable(value = HttpServerErrorException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 5000)
    )
    public String restGetCall(String getCallURL, boolean withToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String accessToken = withToken? TOKEN: "";
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            String requestJson = "{}";
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            ResponseEntity<String> response = restTemplate.exchange(getCallURL, HttpMethod.GET, entity, String.class);
            if (withToken) {
                logger.debug("Call Rest API by Token: " + getCallURL);
            }
            return response.getBody();
        } catch (RestClientException ex) {
            logger.debug(ex.toString());
            logger.debug("Call REST API failed: " + getCallURL);
        }
        return "";
    }

    public Player getPlayer(String utrId, boolean withToken) {
        Player player = null;

        if (fetchedTimes.containsKey(utrId)) {
            LocalDate fetchTime = fetchedTimes.get(utrId);

            if (differentDay(fetchTime)) {
                players.remove(utrId);
            }
        }

        if (players.containsKey(utrId)) {
            player = players.get(utrId);
        } else {
            player = parsePlayer(utrId, withToken);
            if (player == null) {
                return player;
            }
            if (withToken) {
                players.put(player.getId(), player);
                fetchedTimes.put(utrId, LocalDate.now());
            }
        }
        return player;
    }

    private boolean differentDay(LocalDate fetchDate) {
        LocalDate date = LocalDate.now();
        return Duration.between(date.atTime(0, 0), fetchDate.atTime(0, 0)).toDays() < 0;
    }

    public PlayerResult parsePlayerResult(String utrId, boolean latest, boolean withToken) {

        if (utrId == null || utrId.equals("")) {
            return null;
        }
        String key = utrId + (latest ? "T" : "F");

        if (fetchedTimes.containsKey(key)) {
            LocalDate fetchTime = fetchedTimes.get(key);

            if (differentDay(fetchTime)) {
                playerResults.remove(key);
            }
        }

        if (playerResults.containsKey(key)) {
            return playerResults.get(key);
        }

        PlayerResultParser resultParser = new PlayerResultParser(utrId);
        PlayerResult result = resultParser.parseResult(getResultJson(utrId, latest, withToken), true);
        if (withToken) {
            playerResults.put(key, result);
        }

        if (result.getPlayer() != null) {
            Player player = result.getPlayer();
            if (withToken) {
                player = refreshUTR(result.getPlayer());
            }

            if ((result.getWinsNumber() + result.getLossesNumber()) > 0) {
                float successRate = (float) result.getWinsNumber() / (float) (result.getLossesNumber() + result.getWinsNumber());
                if (latest) {
                    player.setSuccessRate(successRate);
                } else {
                    player.setWholeSuccessRate(successRate);
                }
            }
        }

        return result;
    }

    public League getLeague(String id) {

        if (leagues.containsKey(id)) {
            return leagues.get(id);
        }

        League league = parseLeague(id);
        if (league!=null) {
            leagues.put(id, league);
        }

        return league;
    }
}
