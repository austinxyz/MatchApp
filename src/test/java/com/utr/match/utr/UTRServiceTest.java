package com.utr.match.utr;

import com.utr.match.entity.DivisionEntity;
import com.utr.match.entity.UTRTeamCandidate;
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
        DivisionEntity div = service.getDivision(20L);
        div = service.addCandidate(div, "4029352");
        div =service.addCandidate(div, "2970765");
/*        div = service.addCandidate(div, "3501040");
        div = service.addCandidate(div, "2683513");
        div =service.addCandidate(div, "1316122");
        div =service.addCandidate(div, "1638504");
        div =service.addCandidate(div, "3549785");
        div =service.addCandidate(div, "2920275");
        div =service.addCandidate(div, "3575648");
        div =service.addCandidate(div, "2523438");
        div =service.addCandidate(div, "2560896");
        div =service.addCandidate(div, "3516039");
        div =service.addCandidate(div, "488277");
        div =service.addCandidate(div, "3285765");
        div =service.addCandidate(div, "1449517");
        div =service.addCandidate(div, "3236097");

        div =service.addCandidate(div, "2685222");
        div =service.addCandidate(div, "3991946");

        div =service.addCandidate(div, "3375010");
        div =service.addCandidate(div, "3047794");
        div =service.addCandidate(div, "3635576");
        div =service.addCandidate(div, "3296537");
        div =service.addCandidate(div, "4044725");
        div =service.addCandidate(div, "4016599");
        div =service.addCandidate(div, "2833417");*/

    }

    @Test
    void getCandidateTeam() {
        CandidateTeam team = service.getCandidateTeam(20L);
        for (UTRTeamCandidate candidate: team.getCandidates()){
            System.out.println(candidate.getName() + ":" + candidate.getUTR());
        }

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