package com.utr.match.entity;

import com.utr.match.entity.USTALeague;
import com.utr.match.entity.USTALeagueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class USTALeagueRepositoryTest {


    @Autowired
    private USTALeagueRepository leagueRepository;

    @Test
    void createLeague() {

        USTALeague league = new USTALeague("2025 Mixed 18 & Over", "2025");

        leagueRepository.save(league);

    }


}