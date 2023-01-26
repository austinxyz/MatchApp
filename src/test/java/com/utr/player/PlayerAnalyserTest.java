package com.utr.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerAnalyserTest {

    @Test
    void compareSingle() {
        PlayerAnalyser analyser = PlayerAnalyser.getInstance();
        System.out.println(analyser.compareSingle("1316122", "3190677"));
    }

    @Test
    void simpleTest() {
        System.out.println("18+".compareTo("40+"));
        System.out.println("18+".compareTo("55+"));
        System.out.println("40+".compareTo("40+"));
    }
}