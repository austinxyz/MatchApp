package com.utr.match;

import com.utr.match.model.Team;
import com.utr.model.Division;
import com.utr.model.Player;
import com.utr.parser.UTRParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
class TeamLoaderTest {

    @Autowired
    TeamLoader loader;

    @Transactional
    @Test
    void initTeam() {
        Team team = loader.initTeam("ZJU-BYD");

        System.out.println(team);
    }

    @Test
    void getTeams() {
        List<Division> teams = loader.getDivisions();

        for (Division team : teams) {
            System.out.println(team);
        }
    }
    @Test
    void searchPlayer() {

        List<Player> results = loader.queryPlayer("3412468", 5);

        System.out.println(results);
    }

    @Test
    void searchPlayer2() {

        List<Player> results = loader.queryPlayer("yanzhao xu", 5);

        System.out.println(results);
    }
}