package com.utr.match.entity;

import com.utr.match.TeamLoader;
import com.utr.model.Division;
import com.utr.model.Event;
import com.utr.model.Player;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EventPersistApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventPersistApplication.class, args);
    }

    @Bean
    public CommandLineRunner mappingDemo(EventRepository eventRepository,
                                         DivisionRepository divisionRepository,
                                         PlayerRepository playerRepository) {
        return args -> {

            for (PlayerEntity player: playerRepository.findByFirstNameLike("%Ya%")) {
                System.out.println(player.getLastName() + " " + player.getFirstName());
            }

        };
    }

    private void createNewPlayer(Player player, PlayerRepository playerRepository) {
        PlayerEntity playerEntity = new PlayerEntity();
        playerEntity.setGender(player.getGender());
        playerEntity.setUtrId(player.getId());

        String firstName = getFirstName(player.getName());
        String lastName = getLastName(player.getName());
        System.out.println(firstName);
        System.out.println(lastName);
        playerEntity.setFirstName(firstName);
        playerEntity.setLastName(lastName);
        playerRepository.save(playerEntity);
    }

    private String getFirstName(String name) {
        int index = name.indexOf(" ");
        while(index < name.length() && name.charAt(index+1)==' ') {
            index++;
        }
        return name.substring(index+1);
    }

    private String getLastName(String name) {
        int index = name.indexOf(" ");
        while(index < name.length() && name.charAt(index+1)==' ') {
            index++;
        }
        return name.substring(0, index);
    }
}
