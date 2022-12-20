package com.utr.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerAnalyserTest {

    @Test
    void compareSingle() {
        PlayerAnalyser analyser = PlayerAnalyser.getInstance();
        System.out.println(analyser.compareSingle("1316122", "3190677"));
    }
}