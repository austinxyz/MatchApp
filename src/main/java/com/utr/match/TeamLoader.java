package com.utr.match;

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
                    team = new Team(pStrings[3]);
                }

                if(!pStrings[0].startsWith("-")) {
                    team.addPlayer(pStrings[0], pStrings[1], pStrings[2]);
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

}
