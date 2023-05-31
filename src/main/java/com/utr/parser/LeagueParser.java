package com.utr.parser;

import com.utr.model.*;

import java.util.List;
import java.util.Map;

public class LeagueParser extends UTRJSONHandler {
    public League buildLeague(String leagueJsonString) {
        Map<String, Object> leagueJson = parseJsonMap(leagueJsonString);

        League league = new League();

        if (leagueJson == null) {
            return league;
        }

        String id = leagueJson.get("id").toString();
        String name = leagueJson.get("name").toString();

        league.setId(id);
        league.setName(name);
        return league;
    }

    public League buildTeams(League league, String teamsJsonString) {

        Map<String, Object> teamsObj = parseJsonMap(teamsJsonString);

        List teams = (List)teamsObj.get("teams");

        for (Object team : teams) {
            Map<String, Object> teamJson = (Map<String, Object>)team;
            league.getTeams().add(parseTeam(teamJson));
        }

        return league;
    }

    public Team buildTeamPlayers(Team team, String teamJsonString) {
        Map<String, Object> teamObj = parseJsonMap(teamJsonString);

        List members = (List)teamObj.get("teamMembers");

        for (Object member : members) {
            Map<String, Object> memberJson = (Map<String, Object>)member;
            team.getPlayers().add(parsePlayer(memberJson));
        }

        return team;
    }

    private Player parsePlayer(Map<String, Object> playerJson) {
        String id = playerJson.get("playerId").toString();
        String gender = playerJson.get("gender").toString();

        String lastName = (String)playerJson.get("lastName");
        String firstName = (String)playerJson.get("firstName");

        String UTR = (String) playerJson.get("utrDoublesDisplay");

        Player player = new Player(firstName, lastName, gender, UTR);

        player.setId(id);
        Object utrDoubles = playerJson.get("utrDoubles");
        if (utrDoubles!=null) {
            player.setdUTR((Double) utrDoubles);
        }
        Object utrSingles = playerJson.get("utrSingles");
        if (utrSingles!=null) {
            player.setsUTR((Double) utrSingles);
        }
        Map<String, Object> location = (Map<String, Object>) playerJson.get("location");

        if (location != null) {
            player.setLocation((String) location.get("formattedAddress"));
        }
        return player;
    }

    private Team parseTeam(Map<String, Object> teamJson) {
        Team team = new Team();
        if (teamJson == null) {
            return team;
        }

        String id = teamJson.get("id").toString();
        String name = teamJson.get("name").toString();

        team.setId(id);
        team.setName(name);

        List captains = (List)teamJson.get("captains");

        for (Object cap: captains) {
            Map<String, Object> playerJson = (Map<String, Object>)cap;
            Player player = parseCaptain(playerJson);
            team.getCaptains().add(player);
        }
        return team;
    }

    private Player parseCaptain(Map<String, Object> playerJson) {
        String id = playerJson.get("playerId").toString();
        String gender = playerJson.get("gender").toString();

        String lastName = (String)playerJson.get("lastName");
        String firstName = (String)playerJson.get("firstName");

        Player player = new Player(firstName, lastName, gender, "0.0");
        player.setId(id);

        return player;
    }


}
