package com.utr.match.usta;

import com.utr.match.entity.USTATeam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class USTATeamImportorTest {

    @Autowired
    USTATeamImportor importor;

    final String teamURL = "https://www.ustanorcal.com/TeamInfo.asp?id=96400";
    final String teamName = "BAY CLUB SANTA CLARA 40AM3.5A";
    final String flightURL = "https://www.ustanorcal.com/standings.asp?a=usta-nc-nc-mp&l=17608:2606&r=L";

    final String scoreCardURL = "https://www.ustanorcal.com/scorecard.asp?id=753752&l=17644:2623";

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
        importor.updateTeamUTRInfo(teamName);
    }


    @Test
    void importScoreCard() {
        importor.importScoreCard(scoreCardURL, 3L);
    }

}