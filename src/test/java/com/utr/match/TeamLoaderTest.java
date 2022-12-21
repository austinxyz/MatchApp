package com.utr.match;

import com.utr.match.model.Team;
import com.utr.model.Division;
import com.utr.model.Player;
import com.utr.parser.UTRParser;
import org.junit.jupiter.api.Test;

import java.util.List;

class TeamLoaderTest {

    @Test
    void initTeam() {
        Team team = TeamLoader.getInstance().initTeam("ZJU-BYD");

        System.out.println(team);
    }

    @Test
    void getTeams() {
        List<Division> teams = TeamLoader.getInstance().getDivisions();

        for (Division team : teams) {
            System.out.println(team);
        }
    }
    @Test
    void searchPlayer() {

        List<Player> results = TeamLoader.getInstance().queryPlayer("1316122", 5);

        System.out.println(results);
    }

    @Test
    void searchPlayer2() {

        List<Player> results = TeamLoader.getInstance().queryPlayer("yanzhao xu", 5);

        System.out.println(results);
    }
}