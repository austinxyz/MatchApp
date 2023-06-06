package com.utr.match.utr;

import com.utr.match.entity.DivisionEntity;
import com.utr.model.Conference;
import com.utr.model.League;
import com.utr.model.Session;
import com.utr.model.UTRTeam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UTRServiceTest {

    @Autowired
    UTRService service;

    @Test
    void addCandidate() {
        DivisionEntity div = service.getDivision(19L);
/*        service.addCandidate(div, "3548017");
        service.addCandidate(div, "775653");
        service.addCandidate(div, "3763171");
        service.addCandidate(div, "3695913");
        service.addCandidate(div, "257166");
        service.addCandidate(div, "1360516");
        service.addCandidate(div, "887009");
        service.addCandidate(div, "1055171");
        service.addCandidate(div, "1316122");
        service.addCandidate(div, "1313603");
        service.addCandidate(div, "1301884");
        service.addCandidate(div, "2845562");
        service.addCandidate(div, "887136");
        service.addCandidate(div, "3361650");
        service.addCandidate(div, "1086819");
        service.addCandidate(div, "2547696");
        service.addCandidate(div, "3541936");
        service.addCandidate(div, "2716859");
        service.addCandidate(div, "3315883");
        service.addCandidate(div, "2645477");
        service.addCandidate(div, "2734325");
        service.addCandidate(div, "2663703");
        service.addCandidate(div, "3323081");
        service.addCandidate(div, "3601787");
        service.addCandidate(div, "2817725");
        service.addCandidate(div, "3836627");*/
        //service.addCandidate(div, "2815594");
        //service.addCandidate(div, "1725624");
        service.addCandidate(div, "2893015");
        service.addCandidate(div, "484971");
        service.addCandidate(div, "2523438");
    }

    @Test
    void getCandidateTeam() {
        CandidateTeam team = service.getCandidateTeam(19L);
        System.out.println(team);
    }

    @Test
    void importLeague() {
        League league = service.getLeague("26");
        for (Conference conf: league.getConferences()) {
            for (Session session: conf.getSessions()) {
                for (UTRTeam team: session.getTeams()) {
                    service.importTeam(team.getId(), league);
                }
            }
        }
    }
}