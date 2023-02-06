package com.utr.parser;

import com.utr.match.usta.USTATeamImportor;
import com.utr.model.Club;
import com.utr.model.Event;
import com.utr.model.Player;
import com.utr.model.PlayerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class UTRParser {

    private static final Logger logger = LoggerFactory.getLogger(UTRParser.class);
    public UTRParser() {
    }

    public static final String EVENTS_URL = "https://app.universaltennis.com/api/v1/tms/events/";

    public static final String PLAYER_RESULT = "https://app.universaltennis.com/api/v1/player/";

    public static final String PLAYER_PROFILE = "https://app.universaltennis.com/api/v1/player/%s/profile";

    public static final String PLAYER_SEARCH = "https://app.universaltennis.com/api/v2/search/players?query=";

    public static final String CLUB_EVENTS = "https://app.universaltennis.com/api/v1/club/%s/events";

    public static final String CLUB_URL = "https://app.universaltennis.com/api/v1/club/%s";

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNZW1iZXJJZCI6IjIwODkxNCIsImVtYWlsIjoiemhvdXpob25neWkuc2hAZ21haWwuY29tIiwiVmVyc2lvbiI6IjEiLCJEZXZpY2VMb2dpbklkIjoiMTM0NTUzMjQiLCJuYmYiOjE2NzQwMjcxMzYsImV4cCI6MTY3NjYxOTEzNiwiaWF0IjoxNjc0MDI3MTM2fQ.HVz-VDtenXz5Jx1HnTwS8ve_sAdcDOhYVITYkhry49c";

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
        PlayerResultParser resultParser = new PlayerResultParser(playerId);
        return resultParser.parseResult(getResultJson(playerId, true));
    }

    public PlayerResult parsePlayerResult(String playerId, boolean latest) {
        PlayerResultParser resultParser = new PlayerResultParser(playerId);
        return resultParser.parseResult(getResultJson(playerId, latest));
    }

    private String getResultJson(String playerId, boolean latest) {

        String getCallURL
                = PLAYER_RESULT + playerId + "/results";

        if (latest) {
            getCallURL = getCallURL + "?year=last";
        }

        return restGetCall(getCallURL);

    }

    public Player parsePlayer(String playerId) {
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

    @Retryable (value = HttpServerErrorException.class,
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
}
