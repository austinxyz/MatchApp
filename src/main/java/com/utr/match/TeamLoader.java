package com.utr.match;

import com.utr.match.model.Line;
import com.utr.match.model.Player;
import com.utr.match.model.PlayerPair;
import com.utr.match.model.Team;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TeamLoader {

    public Team initTeam(String teamName) {

        BufferedReader reader;
        Team team = null;

        try {
            reader = new BufferedReader(new FileReader("input/" +
                    teamName + ".txt"));
            String line = reader.readLine();
            while (line != null) {
                String[] pStrings = line.split(",");
                if (team == null) {
                    team = createTeam(pStrings[3]);
                }

                if(!pStrings[0].startsWith("-")) {
                    createPlayer(team, pStrings[0], pStrings[1], pStrings[2]);
                }
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return team;
    }

    private void createPlayer(Team team, String name, String gender, String UTR) {
        Player newPlayer = new Player(name, gender, UTR);
        for (Player player: team.getPlayers() ) {
            PlayerPair pair = new PlayerPair(player, newPlayer);
            for (Line line:team.getLines()) {
                line.addMatchedPair(pair);
            }
        }
        team.getPlayers().add(newPlayer);
    }
    private Team createTeam(String teamName) {
        Team team = new Team(teamName);
        team.getLines().add(new Line("D3", (float)11.0, 0));
        team.getLines().add(new Line("MD", (float)10.5,  1));
        team.getLines().add(new Line("D2", (float)12.0,  0));
        team.getLines().add(new Line("D1", (float)13.0,  0));
        team.getLines().add(new Line("WD", (float)9.5,  2));
        return team;
    }
}
