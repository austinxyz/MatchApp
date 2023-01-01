package com.utr.entity;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class PlayerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository playerRepo;

    @Test
    void findAll() {
        List<PlayerEntity> players = playerRepo.findAll();

        for (PlayerEntity player: players) {
            System.out.println(player.getFirstName());
        }
    }
}