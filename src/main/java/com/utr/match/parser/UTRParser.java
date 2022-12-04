package com.utr.match.parser;

import com.utr.match.model.Event;
import com.utr.match.model.Player;
import com.utr.match.model.Team;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UTRParser {

    public static final String EVENTS_URL = "https://app.universaltennis.com/api/v1/tms/events/";
    public static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNZW1iZXJJZCI6IjIwODkxNCIsImVtYWlsIjoiemhvdXpob25neWkuc2hAZ21haWwuY29tIiwiVmVyc2lvbiI6IjEiLCJEZXZpY2VMb2dpbklkIjoiMTI4NjAyNjMiLCJuYmYiOjE2Njg3MjQyMDAsImV4cCI6MTY3MTMxNjIwMCwiaWF0IjoxNjY4NzI0MjAwfQ.HxVRVfhpbSNqnVX1v_ZWTud1Nx0OVgG4KUnz67Ne1aU";

    public Event parseEvent(String eventId) {
        Event event = new Event(eventId);

        getEventJson(eventId, event);

        return event;
    }

    private void getEventJson(String eventId, Event event) {
        String response = getEvent(eventId);

        Map<String, Object> eventJson = JsonParserFactory.getJsonParser().parseMap(response);

        event.setName((String)eventJson.get("name"));

        List divisions = (List)eventJson.get("eventDivisions");

        for (Object devision : divisions) {
            Map<String, Object> teamJson = (Map<String, Object>)devision;
            event.addTeam(parseTeam(teamJson));
        }

        List players = (List)eventJson.get("registeredPlayers");

        for (Object regPlayer: players) {
            Map<String, Object> playerJson = (Map<String, Object>)regPlayer;
            List playerDivs = (List) playerJson.get("registeredDivisions");
            List<Team> playerTeams = parsePlayerTeams(playerDivs, event);

            Player player = parsePlayer(playerJson);
            for (Team team: playerTeams) {
                team.getPlayers().add(player);
            }
        }

    }

    private String getEvent(String eventId) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = EVENTS_URL + eventId;
        HttpHeaders headers = new HttpHeaders();
        String accessToken = TOKEN;
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestJson = "{}";
        HttpEntity<String> entity = new HttpEntity <> (requestJson, headers);
        ResponseEntity<String> response = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    private Player parsePlayer(Map<String, Object> playerJson) {
        String name = (String)playerJson.get("lastName")
                +" " + (String)playerJson.get("firstName");
        String gender = (String)playerJson.get("gender");
        String utr = playerJson.get("myUtrDoubles").toString();
        String id = playerJson.get("id").toString();

        Player player = new Player(name, gender, utr);
        player.setId(id);
        player.setsUTR(getDoubleValue(playerJson, "singlesUtrDisplay"));
        player.setdUTR(getDoubleValue(playerJson, "doublesUtrDisplay"));
        player.setsUTRStatus((String)playerJson.get("ratingStatusSingles"));
        player.setdUTRStatus((String)playerJson.get("ratingStatusDoubles"));
        return player;
    }

    private double getDoubleValue(Map<String, Object> json, String name) {
        Object v = json.get(name);
        if (v==null) {
            return 0.0d;
        }
        return Double.parseDouble((String)v);
    }

    private List<Team> parsePlayerTeams(List playerDivs, Event event) {
        List<Team> teams = new ArrayList<>();
        for (Object div: playerDivs) {
            Map<String, Object> divJson = (Map<String, Object>)div;
            String divId = divJson.get("divisionId").toString();
            teams.add(event.getTeam(divId));
        }

        return teams;
    }

    private Team parseTeam(Map<String, Object> teamJson) {
        Team team = new Team((String)teamJson.get("name"));
        team.setTeamId(teamJson.get("id").toString());
        return team;
    }
}
