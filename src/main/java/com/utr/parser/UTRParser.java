package com.utr.parser;

import com.utr.model.Club;
import com.utr.model.Event;
import com.utr.model.Player;
import com.utr.model.PlayerResult;
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
    private static final Logger logger = LoggerFactory.getLogger(UTRParser.class);
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNZW1iZXJJZCI6IjIwODkxNCIsImVtYWlsIjoiemhvdXpob25neWkuc2hAZ21haWwuY29tIiwiVmVyc2lvbiI6IjEiLCJEZXZpY2VMb2dpbklkIjoiMTQwODU4MjIiLCJuYmYiOjE2NzgyMzc5MjEsImV4cCI6MTY4MDgyOTkyMSwiaWF0IjoxNjc4MjM3OTIxfQ.jJcDXHA2Oc09pkk2ujCAn3a7WEnBeJIo6kHRAl3lmVA";
    Map<String, PlayerResult> playerResults;
    Map<String, Player> players;
    Map<String, Event> events;
    Map<String, Club> clubs;
    private final Map<String, LocalDate> fetchedTimes;

    public UTRParser() {
        playerResults = new HashMap<>();
        events = new HashMap<>();
        clubs = new HashMap<>();
        players = new HashMap<>();
        fetchedTimes = new HashMap<>();
    }

    public List<Event> getClubEvents(String clubId) {
        EventParser eventParser = new EventParser();
        return eventParser.buildEvents(getClubEventsJson(clubId));
    }

    public Club getClub(String clubId) {
        ClubParser clubParser = new ClubParser();
        Club club = clubParser.buildClub(getClubJson(clubId));
        club.getEvents().addAll(getClubEvents(clubId));
        return club;
    }

    private String getClubJson(String clubId) {
        String getCallURL
                = String.format(CLUB_URL, clubId);

        return restGetCall(getCallURL);
    }

    private String getClubEventsJson(String clubId) {
        String getCallURL
                = String.format(CLUB_EVENTS, clubId);

        return restGetCall(getCallURL);
    }


    public List<Player> searchPlayers(String query, int totalNumber) {
        SearchPlayerParser parser = new SearchPlayerParser();
        return parser.buildPlayers(getSearchPlayersJson(query, totalNumber));
    }

    private String getSearchPlayersJson(String query, int totalNumber) {
        String getCallURL
                = PLAYER_SEARCH + query + "&top=" + totalNumber;

        return restGetCall(getCallURL);
    }


    public Event parseEvent(String eventId) {
        EventParser eventParser = new EventParser();
        return eventParser.buildEvent(getEventJson(eventId), eventId);
    }

    public PlayerResult parsePlayerResult(String playerId) {
        return parsePlayerResult(playerId, true);
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
        PlayerResult result = resultParser.parseResult(getResultJson(playerId, latest), false);
        if (result.getLossesNumber() + result.getWinsNumber() == 0) {
            return 0.0f;
        }
        return (float) result.getWinsNumber() / (float) (result.getLossesNumber() + result.getWinsNumber());
    }

    private String getResultJson(String playerId, boolean latest) {

        String getCallURL
                = PLAYER_RESULT + playerId + "/results";

        if (latest) {
            getCallURL = getCallURL + "?year=last";
        }

        return restGetCall(getCallURL);

    }

    private Player parsePlayer(String playerId) {
        PlayerParser resultParser = new PlayerParser(playerId);
        Player player = resultParser.parseResult(getPlayerJson(playerId));

        return player;
    }

    private String getPlayerJson(String playerId) {

        String getCallURL
                = String.format(PLAYER_PROFILE, playerId);

        return restGetCall(getCallURL);

    }

    private String getEventJson(String eventId) {

        String getCallURL
                = EVENTS_URL + eventId;

        return restGetCall(getCallURL);

    }

    @Retryable(value = HttpServerErrorException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 5000)
    )
    public String restGetCall(String getCallURL) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String accessToken = TOKEN;
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            String requestJson = "{}";
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            ResponseEntity<String> response = restTemplate.exchange(getCallURL, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (RestClientException ex) {
            logger.debug(ex.toString());
            logger.debug("Call REST API failed: " + getCallURL);
        }
        return "";
    }

    public Player getPlayer(String utrId) {
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
            player = parsePlayer(utrId);
            if (player == null) {
                return player;
            }
            players.put(player.getId(), player);
            fetchedTimes.put(utrId, LocalDate.now());
        }
        return player;
    }

    private boolean differentDay(LocalDate fetchDate) {
        LocalDate date = LocalDate.now();
        return Duration.between(date.atTime(0, 0), fetchDate.atTime(0, 0)).toDays() < 0;
    }

    public PlayerResult parsePlayerResult(String utrId, boolean latest) {

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
        PlayerResult result = resultParser.parseResult(getResultJson(utrId, latest), true);
        playerResults.put(key, result);

        if (result.getPlayer() != null) {
            Player player = refreshUTR(result.getPlayer());

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
}
