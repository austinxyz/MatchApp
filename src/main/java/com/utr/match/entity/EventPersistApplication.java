package com.utr.match.entity;

import com.utr.match.TeamLoader;
import com.utr.model.Division;
import com.utr.model.Event;
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
                                         DivisionRepository divisionRepository) {
        return args -> {

            Event event = TeamLoader.getInstance().getEvent(TeamLoader.DEFAULT_EVENT_ID);

            EventEntity eventEntity = new EventEntity(event.getName(), "UTR", event.getId());

            eventRepository.save(eventEntity);

            eventEntity = eventRepository.findAll().iterator().next();

            for (Division div : event.getDivisions()) {
                DivisionEntity divEntity = new DivisionEntity(div.getName(), eventEntity);
                divisionRepository.save(divEntity);
            }

        };
    }
}
