package com.utr.parser;

import com.utr.model.Event;
import com.utr.model.PlayerResult;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class UTRParser {

    public static final String EVENTS_URL = "https://app.universaltennis.com/api/v1/tms/events/";
    public static final String PLAYER_RESULT = "https://app.universaltennis.com/api/v1/player/";
    public static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNZW1iZXJJZCI6IjIwODkxNCIsImVtYWlsIjoiemhvdXpob25neWkuc2hAZ21haWwuY29tIiwiVmVyc2lvbiI6IjEiLCJEZXZpY2VMb2dpbklkIjoiMTI4NjAyNjMiLCJuYmYiOjE2Njg3MjQyMDAsImV4cCI6MTY3MTMxNjIwMCwiaWF0IjoxNjY4NzI0MjAwfQ.HxVRVfhpbSNqnVX1v_ZWTud1Nx0OVgG4KUnz67Ne1aU";

    public Event parseEvent(String eventId) {
        EventParser eventParser = new EventParser(eventId);
        return eventParser.buildEvent(getEventJson(eventId));
    }

    public PlayerResult parsePlayerResult(String playerId) {
        PlayerResultParser resultParser = new PlayerResultParser(playerId);
        return resultParser.parseResult(getResultJson(playerId));
    }

    private String getResultJson(String playerId) {

        String getCallURL
                = PLAYER_RESULT + playerId + "/results";

        return restGetCall(getCallURL);

    }

    private String getEventJson(String eventId) {

        String getCallURL
                = EVENTS_URL + eventId;

        return restGetCall(getCallURL);

    }

    private static String restGetCall(String getCallURL) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String accessToken = TOKEN;
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestJson = "{}";
        HttpEntity<String> entity = new HttpEntity <> (requestJson, headers);
        ResponseEntity<String> response = restTemplate.exchange(getCallURL, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }
}
