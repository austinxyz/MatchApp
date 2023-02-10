package com.utr.match.usta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class USTAServiceTest {

    @Autowired
    USTAService service;

    @Test
    void getPlayerScores() {
        System.out.println(service.getPlayerScores("20"));

    }

}