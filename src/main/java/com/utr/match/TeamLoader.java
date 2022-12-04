package com.utr.match;

import com.utr.match.model.Line;
import com.utr.match.model.PlayerPair;
import com.utr.match.model.Team;
import com.utr.model.Division;
import com.utr.model.Event;
import com.utr.model.Player;
import com.utr.parser.UTRParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class TeamLoader {

    public static final String EVENT_ID = "123233";
    Event event;

    private TeamLoader() {
        UTRParser parser = new UTRParser();
        event = parser.parseEvent(EVENT_ID);
        loadDataFromFile(event);
    }

    public static TeamLoader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private void loadDataFromFile(Event event) {
        for (Division div : event.getDivisions()) {
            updateTeamFromFile(div);
        }
    }

    private void updateTeamFromFile(Division div) {

        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader("input/" + EVENT_ID + "/" +
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

    public List<Division> getDivisions() {
        return event.getDivisions();
    }

    public Team initTeam(String teamName) {

        Division div = event.getDivisionByName(teamName);

        Team team = createTeam(div);

        for (Player player : div.getPlayers()) {
            createPlayer(team, player);
        }

        return team;
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
