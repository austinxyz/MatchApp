package com.utr.match.usta;

import com.utr.match.entity.*;
import com.utr.match.usta.po.USTALeaguePO;
import com.utr.match.usta.po.USTATeamPO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class USTAServiceTest {

    @Autowired
    USTAService service;
    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private USTADivisionRepository ustaDivisionRepository;

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

    @Test
    void getTeam() {
        service.getTeam("213", true);
    }

    @Test
    void createCandidateTeam() {
        USTADivision div = ustaDivisionRepository.findByName("2023 Mixed 40 & Over 7.0");
        service.createCandidateTeam("RoyalFlush 7.0M", div);
    }

    @Test
    void addCandidate() {
        USTACandidateTeam team = service.getCandidateTeam("2"); // 7.0 mixed team.
        team = service.addCandidate(team, "2138695");

    }
}