package com.utr.match;

import com.utr.match.model.Line;
import com.utr.match.model.PlayerPair;
import com.utr.match.model.Team;
import com.utr.model.*;
import com.utr.parser.UTRParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamLoader {

    public static final String DEFAULT_EVENT_ID = "123233";
    public static final String DEFAULT_CLUB_ID = "3156";
    Map<String, Event> events;
    Map<String, Club> clubs;

    Map<String, PlayerResult> playerResults;
    UTRParser parser;

    private TeamLoader() {
        parser = new UTRParser();
        Event event = parser.parseEvent(DEFAULT_EVENT_ID);
        loadDataFromFile(event);
        playerResults = new HashMap<>();
        events = new HashMap<>();
        events.put(DEFAULT_EVENT_ID, event);
        clubs = new HashMap<>();
        //Club club = parser.getClub(DEFAULT_CLUB_ID);
        //clubs.put(DEFAULT_CLUB_ID, club);
    }

    public static TeamLoader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private void loadDataFromFile(Event event) {
        for (Division div : event.getDivisions()) {
            updateTeamFromFile(div, event.getId());
        }
    }

    private void updateTeamFromFile(Division div, String eventId) {

        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader("input/" + eventId + "/" +
                    div.getName() + ".txt"));
            String line = reader.readLine();
            while (line != null) {
                String[] pStrings = line.split(",");
                if (div.getDisplayName() == null) {
                    div.setDisplayName(pStrings[3]);
                }

                if (!pStrings[0].startsWith("-")) {
                    Player player = div.getPlayer(pStrings[0]);
                    if (player != null) {
                        player.setUTR(pStrings[2]);
                    }
                }

                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        div.getPlayers().sort((o1, o2) -> Float.compare(o2.getUTR(), o1.getUTR()));
    }

    public List<Player> queryPlayer(String query, int top) {
        if (isPlayerId(query)) {
            PlayerResult result = parser.parsePlayerResult(query);
            List<Player> players = new ArrayList<>();
            players.add(result.getPlayer());
            return players;
        }
        return parser.searchPlayers(query, top);
    }

    private boolean isPlayerId(String query) {

        if (query == null || query.length() <1) {
            return false;
        }

        char c = query.charAt(0);

        return c>= '1' && c<='9';

    }

    public List<Division> getDivisions() {
        return getDivisions(DEFAULT_EVENT_ID);
    }

    public Club getClub(String clubId) {
        return getOrFetchClub(clubId);
    }

    public Club getClub() {
        return getOrFetchClub(DEFAULT_CLUB_ID);
    }

    public List<Division> getDivisions(String eventId) {
        Event event = getOrFetchEvent(eventId);

        return event.getDivisions();
    }

    public Event getEvent(String eventId) {
        return getOrFetchEvent(eventId);
    }

    private Event getOrFetchEvent(String eventId) {
        Event event;

        if (events.containsKey(eventId)) {
            event = events.get(eventId);
        } else {
            event = parser.parseEvent(eventId);
            events.put(eventId, event);
        }
        return event;
    }

    private Club getOrFetchClub(String clubId) {
        Club club;

        if (clubs.containsKey(clubId)) {
            club = clubs.get(clubId);
        } else {
            club = parser.getClub(clubId);
            clubs.put(clubId, club);
        }
        return club;
    }
    public Team initTeam(String teamName) {
        Event event = getOrFetchEvent(DEFAULT_EVENT_ID);

        Division div = event.getDivisionByName(teamName);

        Team team = createTeam(div);

        for (Player player : div.getPlayers()) {
            createPlayer(team, player);
        }

        return team;
    }

    public Team initTeam(String teamId, String eventId) {

        Event event = getOrFetchEvent(eventId);

        Division div = event.getDivision(teamId);

        if (div == null) {
            return null;
        }

        Team team = createTeam(div);

        for (Player player : div.getPlayers()) {
            createPlayer(team, player);
        }

        return team;
    }

    public PlayerResult searchPlayerResult(String playerId) {
        if (playerResults.containsKey(playerId)) {
            return playerResults.get(playerId);
        }

        PlayerResult result = parser.parsePlayerResult(playerId);
        playerResults.put(playerId, result);
        return result;
    }

    private void createPlayer(Team team, Player teamPlayer) {

        for (Player player : team.getPlayers()) {
            PlayerPair pair = new PlayerPair(player, teamPlayer);
            for (Line line : team.getLines().values()) {
                line.addMatchedPair(pair);
            }
        }
        team.getPlayers().add(teamPlayer);
    }

    private Team createTeam(Division div) {
        Team team = new Team(div.getName());
        team.setDisplayName(div.getDisplayName());
        team.setTeamId(div.getId());
        team.getLines().put("D3", new Line("D3", (float) 11.0, 0));
        team.getLines().put("MD", new Line("MD", (float) 10.5, 1));
        team.getLines().put("D2", new Line("D2", (float) 12.0, 0));
        team.getLines().put("D1", new Line("D1", (float) 13.0, 0));
        team.getLines().put("WD", new Line("WD", (float) 9.5, 2));
        return team;
    }

    private static class SingletonHolder {
        private static final TeamLoader INSTANCE = new TeamLoader();
    }
}
