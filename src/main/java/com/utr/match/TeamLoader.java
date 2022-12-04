package com.utr.match;

import com.utr.match.model.*;
import com.utr.match.parser.UTRParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class TeamLoader {

    Event event;
    private TeamLoader() {
        UTRParser parser = new UTRParser();
        event = parser.parseEvent("123233");
        loadDataFromFile(event);
    }

    private void loadDataFromFile(Event event) {
        for (Team team: event.getTeams()) {
            updateTeamFromFile(team);
        }
    }

    private void updateTeamFromFile(Team team) {

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("input/" +
                    team.getName() + ".txt"));
            String line = reader.readLine();
            while (line != null) {
                String[] pStrings = line.split(",");
                if (team.getDisplayName() == null) {
                    team.setDisplayName(pStrings[3]);
                }

                if(!pStrings[0].startsWith("-")) {
                    Player player = team.getPlayer(pStrings[0]);
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

        team.getPlayers().sort((o1, o2) -> Float.compare(o2.getUTR(), o1.getUTR()));
    }

    private static class SingletonHolder {
        private static final TeamLoader INSTANCE = new TeamLoader();
    }

    public static TeamLoader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public List<Team> getTeams() {
        return event.getTeams();
    }

    public Team initTeam(String teamName) {

        Team team = event.getTeamByName(teamName);

        Team newTeam = createTeam(team);

        for(Player player: team.getPlayers()) {
            createPlayer(newTeam, player);
        }

        return newTeam;
    }

    private void createPlayer(Team newTeam, Player teamPlayer) {

        Player newPlayer = new Player(teamPlayer.getName(), teamPlayer.getGender(), String.valueOf(teamPlayer.getUTR()));

        newPlayer.setsUTRStatus(teamPlayer.getsUTRStatus());
        newPlayer.setdUTRStatus(teamPlayer.getdUTRStatus());
        newPlayer.setsUTR(teamPlayer.getsUTR());
        newPlayer.setdUTR(teamPlayer.getdUTR());

        for (Player player: newTeam.getPlayers() ) {
            PlayerPair pair = new PlayerPair(player, newPlayer);
            for (Line line:newTeam.getLines().values()) {
                line.addMatchedPair(pair);
            }
        }
        newTeam.getPlayers().add(newPlayer);
    }

    private Team createTeam(Team oteam) {
        Team team = new Team(oteam.getName());
        team.setDisplayName(oteam.getDisplayName());
        team.setTeamId(oteam.getTeamId());
        team.getLines().put("D3", new Line("D3", (float)11.0, 0));
        team.getLines().put("MD", new Line("MD", (float)10.5,  1));
        team.getLines().put("D2", new Line("D2", (float)12.0,  0));
        team.getLines().put("D1", new Line("D1", (float)13.0,  0));
        team.getLines().put("WD", new Line("WD", (float)9.5,  2));
        return team;
    }
}
