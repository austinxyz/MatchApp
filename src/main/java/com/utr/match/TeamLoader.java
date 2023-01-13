package com.utr.match;

import com.utr.match.entity.EventDBLoader;
import com.utr.match.model.Line;
import com.utr.match.model.PlayerPair;
import com.utr.match.model.Team;
import com.utr.model.*;
import com.utr.parser.UTRParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("singleton")
public class TeamLoader {

    public static final String DEFAULT_EVENT_ID = "123233";
    public static final String DEFAULT_CLUB_ID = "3156";
    Map<String, Event> events;
    Map<String, Club> clubs;

    Map<String, PlayerResult> playerResults;

    Map<String, Player> players;

    @Autowired
    UTRParser parser;

    @Autowired
    EventDBLoader loader;

    private TeamLoader() {

        playerResults = new HashMap<>();
        events = new HashMap<>();
        clubs = new HashMap<>();
        players = new HashMap<>();

    }

    public Player getPlayer(String utrId) {
        Player player = null;
        if (players.containsKey(utrId)) {
            player = players.get(utrId);
        } else {
            player = parser.parsePlayer(utrId);
            players.put(player.getId(), player);
        }
        return player;
    }

    public List<Player> queryPlayer(String query, int top) {
        if (isPlayerId(query)) {
            Player player = getPlayer(query);
            List<Player> players = new ArrayList<>();
            players.add(player);
            return players;
        }
        return parser.searchPlayers(query, top);
    }

    private boolean isPlayerId(String query) {

        if (query == null || query.length() < 1) {
            return false;
        }

        char c = query.charAt(0);

        return c >= '1' && c <= '9';

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

            if (eventId.equals(DEFAULT_EVENT_ID)) {
                loader.updateEvent(event);
            }
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

        Player player = null;
        if (!players.containsKey(playerId)) {
            player = result.getPlayer();
            players.put(player.getId(), player);
        } else {
            player = players.get(playerId);
        }
        player.setSuccessRate(
                (float)result.getWinsNumber()/(float)(result.getLossesNumber()+result.getWinsNumber()));

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
}
