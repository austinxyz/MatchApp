package com.utr.match.usta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class USTATeamAnalyserTest {

    @Autowired
    USTATeamAnalyser analyser;


    @Test
    void compareTeam() {

        USTATeamAnalysisResult result = analyser.compareTeam("1", "41");
        result.getTeam2();
    }
}