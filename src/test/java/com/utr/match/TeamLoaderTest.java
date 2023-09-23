package com.utr.match;

import com.utr.match.entity.DivisionEntity;
import com.utr.match.entity.DivisionRepository;
import com.utr.match.entity.EventEntity;
import com.utr.match.entity.EventRepository;
import com.utr.match.model.Team;
import com.utr.model.Division;
import com.utr.model.Player;
import com.utr.model.PlayerResult;
import com.utr.parser.UTRParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
class TeamLoaderTest {

    @Autowired
    TeamLoader loader;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    DivisionRepository divisionRepository;

    @Transactional
    @Test
    void initTeam() {
        Team team = loader.initTeam("USTC");

        System.out.println(team);
    }

    @Test
    void getTeams() {

        EventEntity event = eventRepository.findByEventId("196830");

        if (event == null) {
            return;
        }

        List<Division> teams = loader.getDivisions();

        for (Division team : teams) {
            if (event.getDivision(team.getId())==null) {
                DivisionEntity division = new DivisionEntity(team.getName(), event);
                division.setEnglishName(team.getName());
                division.setDivisionId(team.getId());
                //division.setChineseName(team.getName());
                divisionRepository.save(division);
                System.out.println("team :" + team.getName() + " save to DB");
            } else {
                System.out.println("team: " + team.getName() + " is existed");
                System.out.println(team);
            };
        }
    }


    @Test
    void searchPlayer() {

        PlayerResult result = loader.searchPlayerResult("1316122", true, false);

        System.out.println(result);
    }

    @Test
    void searchPlayer2() {

        List<Player> results = loader.queryPlayer("yanzhao xu", 5, false);

        System.out.println(results);
    }
}