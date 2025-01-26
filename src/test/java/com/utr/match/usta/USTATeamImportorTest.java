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
    USTAMatchImportor matchImportor;

    @Autowired
    USTATeamRepository teamRepository;


    final String teamURL = "https://leagues.ustanorcal.com/teaminfo.asp?id=96444";
    final String teamName = "VALLEY CHURCH 18MX8.0A";

    String divisionName = "2024 Mixed 18 & Over 8.0";
    final String flightURL = "https://leagues.ustanorcal.com/standings.asp?a=usta-nc-nc-eb&l=18480:2706&r=L";

    final String scoreCardURL = "https://leagues.ustanorcal.com/scorecard.asp?id=753886&l=17624:2624";
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
        matchImportor.importScoreCard(scoreCardURL, division);
    }


    @Test
    void importTeamMatchs() {
        USTATeamEntity team = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        USTADivision division = divisionRepository.findByName(divisionName);
        matchImportor.refreshMatchesScores(new USTATeam(team), division);
    }

    @Test
    void updateTeamPlayersDR() {
        USTATeamEntity teamEntity = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        USTATeam team = new USTATeam(teamEntity);
        importor.updateTeamPlayersDR(team);
    }

    @Test
    void updatePlayerUTRInfo() {
        PlayerEntity player = playerRepository.findByUtrId("257354").get(0);

        importor.updatePlayerUTRInfo(player, true, true);
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

    @Test
    void updateTeamUTRInfo() {
        USTATeamEntity teamEntity = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        USTATeam team = new USTATeam(teamEntity);
        importor.updateTeamUTRInfo(team, true, false);
    }
}