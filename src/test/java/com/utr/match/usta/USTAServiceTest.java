package com.utr.match.usta;

import com.utr.match.usta.po.USTALeaguePO;
import com.utr.match.usta.po.USTATeamPO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class USTAServiceTest {

    @Autowired
    USTAService service;

    @Test
    void getPlayerScores() {
        System.out.println(service.getPlayerScores("20"));

    }

    @Test
    void getLeaguesFromUSTASite() {
        for (USTALeaguePO league:service.getLeaguesFromUSTASite()) {
            System.out.println(league);
        }
    }

    @Test
    void getTeamsFromUSTASite() {
        String divLink = "https://www.ustanorcal.com/listteams.asp?leagueid=2605";
        String divName = "2023 Adult 40 & Over Mens 3.5";
        for (USTATeamPO team: service.getTeamsFromUSTASite("2605")) {
            System.out.println(team);
        }
    }
}