package com.utr.match;

import com.utr.match.entity.*;
import com.utr.match.model.Line;
import com.utr.match.model.PlayerPair;
import com.utr.match.model.Team;
import com.utr.model.*;
import com.utr.parser.UTRParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("singleton")
public class TeamLoader {

    public static final String DEFAULT_EVENT_ID = "196830";
    public static final String DEFAULT_CLUB_ID = "3156";
    Map<String, Event> events;
    Map<String, Club> clubs;

    @Autowired
    UTRParser parser;

    @Autowired
    EventDBLoader loader;

    @Autowired
    UTRTeamRepository utrTeamRepository;

    private TeamLoader() {

        events = new HashMap<>();
        clubs = new HashMap<>();

    }

    public Player getPlayer(String utrId, boolean withToken) {

        return parser.getPlayer(utrId, withToken);

    }

    public List<Player> queryPlayer(String query, int top, boolean withToken) {
        if (isPlayerId(query)) {
            Player player = getPlayer(query, withToken);
            List<Player> players = new ArrayList<>();
            players.add(player);
            return players;
        }
        return parser.searchPlayers(query, top, withToken);
    }

    private boolean isPlayerId(String query) {

        if (query == null || query.length() < 1) {
            return false;
        }

        char c = query.charAt(0);

        return c >= '1' && c <= '9';

    }

    public List<Division> getDivisions() {
        return getDivisions(DEFAULT_EVENT_ID, true);
    }

    public Club getClub(String clubId, boolean withToken) {
        return getOrFetchClub(clubId, withToken);
    }

    public Club getClub() {
        return getOrFetchClub(DEFAULT_CLUB_ID, true);
    }

    public List<Division> getDivisions(String eventId, boolean withToken) {
        Event event = getOrFetchEvent(eventId, withToken);

        return event.getDivisions();
    }

    public Event getEvent(String eventId, boolean withToken) {
        return getOrFetchEvent(eventId, withToken);
    }

    private Event getOrFetchEvent(String eventId, boolean withToken) {
        Event event;

        if (events.containsKey(eventId)) {
            event = events.get(eventId);
        } else {
            event = fetchEvent(eventId, withToken);
        }
        return event;
    }

    private Event fetchEvent(String eventId, boolean withToken) {
        Event event;
        event = parser.parseEvent(eventId, withToken);

        if (eventId.equals(DEFAULT_EVENT_ID)) {
            loader.updateEvent(event);
        }
        events.put(eventId, event);
        return event;
    }

    private Club getOrFetchClub(String clubId, boolean withToken) {
        Club club;

        if (clubs.containsKey(clubId)) {
            club = clubs.get(clubId);
        } else {
            club = parser.getClub(clubId, withToken);
            clubs.put(clubId, club);
        }
        return club;
    }

    public Team initTeam(String teamName) {

        Event event = getOrFetchEvent(DEFAULT_EVENT_ID, true);

        Division div = event.getDivisionByName(teamName);

        return getTeam(div, DEFAULT_EVENT_ID);
    }

    private Player toPlayer(UTRTeamMember member, Division div) {
        Player player = div.getPlayerByUTRId(member.getUtrId());
        if (player == null) {
            return player;
        }
        if (member.getMatchUTR() >= 0.1d) {
            player.setUTR(String.valueOf(member.getMatchUTR()));
        } else {
            //player.setUTR(String.valueOf(member.getDUTR()));
        }
        player.setId(member.getUtrId());
        player.setUstaRating(member.getUSTARating());
        player.setSuccessRate(member.getSuccessRate());
        player.setWholeSuccessRate(member.getWholeSuccessRate());
        return player;
    }

    public Team initTeam(String teamId, String eventId, boolean withToken) {

        Event event = getOrFetchEvent(eventId, true);

        Division div = event.getDivision(teamId);

        return getTeam(div, eventId);
    }

    private Team getTeam(Division div, String eventId) {
        if (div == null) {
            return null;
        }

        UTRTeamEntity teamEntity = utrTeamRepository.findByUtrTeamId(div.getId());

        if (teamEntity == null) {
            return null;
        }

        if (teamEntity.getPlayers().size() != div.getPlayers().size()) {
            System.out.println("team " + teamEntity.getName() + " has " + teamEntity.getPlayers().size() + " not same players in utr :" + div.getPlayers().size());
            Event event = fetchEvent(eventId, true);
            div = event.getDivision(div.getId());

            if (div == null) {
                return null;
            }
        }

        Team team = createTeam(teamEntity);

        for (UTRTeamMember member : teamEntity.getPlayers()) {
            Player player = toPlayer(member, div);

            if (player == null) {
                continue;
            }

            createPlayer(team, player);
        }

        team.getWD().resetMatchPairs(20);

        team.caculateTeamUTR();

        return team;
    }

    public PlayerResult searchPlayerResult(String playerId, boolean latest, boolean withToken) {

        PlayerResult result = parser.parsePlayerResult(playerId,latest, withToken);

        if (result == null) {
            return null;
        }

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
        team.getLines().put("WD", new Line("WD", (float) 9.5, 2, 4.5f));
        return team;
    }

    private Team createTeam(UTRTeamEntity teamEntity) {
        Team team = new Team(teamEntity.getName());
        team.setTeamId(teamEntity.getUtrTeamId());
        team.getLines().put("D3", new Line("D3", (float) 11.0, 0));
        team.getLines().put("MD", new Line("MD", (float) 10.5, 1));
        team.getLines().put("D2", new Line("D2", (float) 12.0, 0));
        team.getLines().put("D1", new Line("D1", (float) 13.0, 0));
        team.getLines().put("WD", new Line("WD", (float) 9.5, 2, 4.5f));
        return team;
    }
}
