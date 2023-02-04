package com.utr.match.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class USTATeamScoreCardRepositoryTest {

    @Autowired
    USTATeamScoreCardRepository scoreCardRepository;


    @Test
    void findByHomeTeamNameNull() {
        for (USTATeamScoreCard scoreCard: scoreCardRepository.findByHomeTeamNameNull()) {
            scoreCard.setHomeTeamName(scoreCard.getHomeTeam().getName());
            scoreCard.setGuestTeamName(scoreCard.getGuestTeam().getName());
            scoreCardRepository.save(scoreCard);
            System.out.println("score card " + scoreCard.getId() + " is updated");
        }
    }
}