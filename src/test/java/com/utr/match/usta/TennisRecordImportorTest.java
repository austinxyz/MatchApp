package com.utr.match.usta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TennisRecordImportorTest {

    public static final String TEAM_NAME = "Tri-Level 18+ NEW ENGLAND M 0.0";
    @Autowired
    TennisRecordImportor importor;

    @Autowired
    USTATeamImportor teamImportor;

    @Autowired
    USTAService service;

    @Test
    void importUSTATeam() {

        importor.importUSTATeam("https://www.tennisrecord.com/adult/teamprofile.aspx?teamname=NorCal%2fShih%2fTri-LevelM4.5A&year=2022",
                TEAM_NAME);
    }

    @Test
    void importUSTATeamFromText() {
        String playerList = "Mark Blaisdell\t4.5 | Jerry Knickerbocker\t4 | Brian Brower\t3.5 |" +
                "Tomas Gonzalez\t4.5 | Geoffrey West\t4 | " +
                "Andrew Day\t4.5 | Michael Rubin\t3.5";

        importor.importUSTATeamFromText(playerList,
                TEAM_NAME);
    }

    @Test
    void parseTeamUTRID() {
        List<USTATeam> teams =
                service.searchTeam(TEAM_NAME);

        if (teams.size() > 0) {
            USTATeam team = teams.get(0);

            teamImportor.updateTeamPlayersUTRID(team);
        }


    }
}