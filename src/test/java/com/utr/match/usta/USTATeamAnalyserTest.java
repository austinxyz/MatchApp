package com.utr.match.usta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class USTATeamAnalyserTest {

    @Autowired
    USTATeamAnalyser analyser;


    @Test
    void compareTeam() {

        analyser.compareTeam("1", "41");
    }
}