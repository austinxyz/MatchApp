package com.utr.match.usta;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.PlayerRepository;
import com.utr.match.entity.USTATeam;
import com.utr.match.entity.USTATeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class USTATeamImportorTest {

    @Autowired
    USTATeamImportor importor;

    @Autowired
    USTATeamRepository teamRepository;

    final String teamURL = "https://www.ustanorcal.com/TeamInfo.asp?id=96400";
    final String teamName = "MOUNTAIN VIEW TC/RENGSTORFF PK 40AM4.0A";
    final String flightURL = "https://www.ustanorcal.com/standings.asp?a=usta-nc-nc-sb&l=17834:2624&r=L";

    final String scoreCardURL = "https://www.ustanorcal.com/scorecard.asp?id=750078&l=17608:2606";
    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void importUSTAFlight() {
        importor.importUSTAFlight(flightURL);
    }
    @Test
    void importUSTATeam() {
        importor.importUSTATeam(teamURL);
    }

    @Test
    void updateUTRId() {
        importor.updateTeamPlayerUTRID(teamName);
    }

    @Test
    void updateTeamUTR() {
        USTATeam team = teamRepository.findByName(teamName);
        importor.updateTeamUTRInfo(team);
    }


    @Test
    void importScoreCard() {
        importor.importScoreCard(scoreCardURL, 6L);
    }


    @Test
    void importTeamMatchs() {
        USTATeam team = teamRepository.findByName(teamName);
        importor.refreshTeamMatcheScores(team);
    }

    @Test
    void updateTeamPlayersDR() {
        USTATeam team = teamRepository.findByName(teamName);
        importor.updateTeamPlayersDR(team);
    }

    @Test
    void updatePlayerUTRInfo() {
        PlayerEntity player = playerRepository.findByUtrId("2929342");

        importor.updatePlayerUTRInfo(player, true);
    }
}