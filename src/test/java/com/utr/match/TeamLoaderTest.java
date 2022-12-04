package com.utr.match;

import com.utr.match.model.Team;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeamLoaderTest {

    @Test
    void initTeam() {
        Team team = TeamLoader.getInstance().initTeam("ZJU-BYD");

        System.out.println(team);
    }

    @Test
    void getTeams() {
        List<Team> teams = TeamLoader.getInstance().getTeams();

        for (Team team: teams) {
            System.out.println(team);
        }
    }
}