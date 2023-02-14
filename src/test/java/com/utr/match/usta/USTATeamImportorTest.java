package com.utr.match.usta;

import com.utr.match.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class USTATeamImportorTest {

    @Autowired
    USTATeamImportor importor;

    @Autowired
    USTATeamRepository teamRepository;


    final String teamURL = "https://www.ustanorcal.com/teaminfo.asp?id=92360";
    final String teamName = "PALO ALTO TC/RINCONADA PK 18MX8.0D";

    String divisionName = "2023 Mixed 18 & Over 8.0";
    final String flightURL = "https://www.ustanorcal.com/standings.asp?a=usta-nc-nc-lp&l=17728:2625&r=L";

    final String scoreCardURL = "https://www.ustanorcal.com/scorecard.asp?id=753886&l=17624:2624";
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private USTADivisionRepository divisionRepository;

    @Test
    void importUSTATeam() {
        importor.importUSTATeam(teamURL);
    }

    @Test
    void updateAllUTRId() {

        List<USTATeamEntity> teams = teamRepository.findByDivision_IdOrderByUstaFlightAsc(2L);

        for (USTATeamEntity teamEntity: teams) {
            //System.out.println(team.getName());
            //importor.updateTeamPlayersUTRID(team);
            USTATeam team = new USTATeam(teamEntity);
            importor.updateTeamUTRInfo(team);
        }
    }


    @Test
    void updateUTRId() {

        importor.updateTeamPlayerUTRID(teamName, divisionName);
    }

    @Test
    void updateTeamUTR() {
        USTATeamEntity teamEntity = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        USTATeam team = new USTATeam(teamEntity);
        importor.updateTeamUTRInfo(team);
    }

    @Test
    void updateTeamUSTAInfo() {
        USTATeamEntity teamEntity = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        USTATeam team = new USTATeam(teamEntity);
        importor.updatePlayerUSTANumber(teamEntity);
    }

    @Test
    void importUSTAFlight() {
        importor.importUSTAFlight(flightURL);
    }
    @Test
    void updateFlightUTRID() {
        List<USTATeamEntity> teams = teamRepository.findByUstaFlight_Id(28L);

        for (USTATeamEntity teamEntity: teams) {
            USTATeam team = new USTATeam(teamEntity);
            importor.updateTeamPlayersUTRID(team);
        }
    }

    @Test
    void updateFlightUTRInfo() {
        List<USTATeamEntity> teams = teamRepository.findByUstaFlight_Id(28L);

        for (USTATeamEntity teamEntity: teams) {
            USTATeam team = new USTATeam(teamEntity);
            importor.updateTeamUTRInfo(team);
        }
    }
    @Test
    void importScoreCard() {
        USTADivision division = divisionRepository.findByName(divisionName);
        importor.importScoreCard(scoreCardURL, 6L, division);
    }


    @Test
    void importTeamMatchs() {
        USTATeamEntity team = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        USTADivision division = divisionRepository.findByName(divisionName);
        importor.refreshTeamMatchesScores(team, division);
    }

    @Test
    void updateTeamPlayersDR() {
        USTATeamEntity teamEntity = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        USTATeam team = new USTATeam(teamEntity);
        importor.updateTeamPlayersDR(team);
    }

    @Test
    void updatePlayerUTRInfo() {
        PlayerEntity player = playerRepository.findByUtrId("257354");
        USTATeamMember member = new USTATeamMember(player);

        importor.updatePlayerUTRInfo(member, true);
    }

    @Test
    void findTeamsByFlight() {
        long begin = System.currentTimeMillis();
        List<USTATeamEntity> teams = teamRepository.findByUstaFlight_Id(2L);
        System.out.println("it takes " + (System.currentTimeMillis() - begin)/1000 + " second");
    }

    @Test
    void updateAllTeamsDR() {

        for (USTATeamEntity teamEntity : teamRepository.findByDivision_IdOrderByUstaFlightAsc(5L)) {
            USTATeam team = new USTATeam(teamEntity);
            importor.updateTeamPlayersDR(team);
        }
    }
}