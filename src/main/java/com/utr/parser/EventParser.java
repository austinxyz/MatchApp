package com.utr.parser;

import com.utr.model.Division;
import com.utr.model.Event;
import com.utr.model.Player;
import org.springframework.boot.json.JsonParserFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventParser {

    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public EventParser() {
    }

    public Event buildEvent(String eventJsonString, String eventId) {
        Event event = new Event(eventId);

        Map<String, Object> eventJson = JsonParserFactory.getJsonParser().parseMap(eventJsonString);

        event.setName((String)eventJson.get("name"));

        List divisions = (List)eventJson.get("eventDivisions");

        for (Object devision : divisions) {
            Map<String, Object> divJson = (Map<String, Object>)devision;
            event.addDivision(parseDivision(divJson));
        }

        List players = (List)eventJson.get("registeredPlayers");

        for (Object regPlayer: players) {
            Map<String, Object> playerJson = (Map<String, Object>)regPlayer;
            List playerDivs = (List) playerJson.get("registeredDivisions");
            List<Division> playerTeams = parsePlayerDivisions(playerDivs, event);

            Player player = parsePlayer(playerJson);
            for (Division div: playerTeams) {
                div.getPlayers().add(player);
            }
        }

        return event;
    }


    private Player parsePlayer(Map<String, Object> playerJson) {
        String lastName = (String)playerJson.get("lastName");
        String firstName = (String)playerJson.get("firstName");
        String gender = (String)playerJson.get("gender");
        String utr = playerJson.get("doublesUtrDisplay").toString();
        String id = playerJson.get("id").toString();

        Player player = new Player(firstName, lastName, gender, utr);
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

    private List<Division> parsePlayerDivisions(List playerDivs, Event event) {
        List<Division> divisions = new ArrayList<>();
        for (Object div: playerDivs) {
            Map<String, Object> divJson = (Map<String, Object>)div;
            String divId = divJson.get("divisionId").toString();
            divisions.add(event.getDivision(divId));
        }

        return divisions;
    }

    private Division parseDivision(Map<String, Object> divisionJson) {
        Division division = new Division(divisionJson.get("id").toString());
        division.setName((String)divisionJson.get("name"));
        return division;
    }

    public List<Event> buildEvents(String clubEventsJson) {
        List eventsJson = JsonParserFactory.getJsonParser().parseList(clubEventsJson);

        List<Event> events = new ArrayList<>();

        for (Object eventJsonObject: eventsJson) {
            Map<String, Object> eventJson = (Map<String, Object>)eventJsonObject;
            String eventId = ((Integer)(eventJson.get("id"))).toString();
            Event event = new Event(eventId);
            event.setName((String)eventJson.get("name"));

            Map<String, Object> eventScheduleJson = (Map<String, Object>)eventJson.get("eventSchedule");

            LocalDateTime date = LocalDateTime.parse((String)eventScheduleJson.get("eventStartUtc"), formatter);

            event.setStartDate(date);
            event.setDuration(eventScheduleJson.get("eventDatesLong").toString());

            events.add(event);
        }
        events.sort((o1, o2) -> o2.getStartDate().compareTo(o1.getStartDate()));

        return events;
    }
}
