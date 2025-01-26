package com.utr.match.utr;

import com.utr.match.TeamLoader;
import com.utr.match.entity.DivisionEntity;
import com.utr.match.entity.UTRTeamCandidate;
import com.utr.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UTRServiceTest {

    @Autowired
    UTRService service;

    @Autowired
    TeamLoader loader;

    @Test
    void addCandidate() {
        DivisionEntity div = service.getDivision(43L);
        div = service.addCandidate(div, "3375010");
        div = service.addCandidate(div, "1947802");
        div = service.addCandidate(div, "705972");
        div = service.addCandidate(div, "2716050");
        div = service.addCandidate(div, "3047794");
        div = service.addCandidate(div, "2833417");
        div = service.addCandidate(div, "3409739");
        div = service.addCandidate(div, "3309901");
        div = service.addCandidate(div, "3184161");
        div = service.addCandidate(div, "592561");
        div = service.addCandidate(div, "3296537");
        div = service.addCandidate(div, "3042827");
        div = service.addCandidate(div, "4016599");

/*        div = service.addCandidate(div, "2818039");
        div = service.addCandidate(div, "2560896");
        div = service.addCandidate(div, "4536644");
        div = service.addCandidate(div, "3285765");
        div = service.addCandidate(div, "2920275");
        div = service.addCandidate(div, "4047074");
        div = service.addCandidate(div, "2523438");
        div = service.addCandidate(div, "2683513");
        div = service.addCandidate(div, "3501040");
        div = service.addCandidate(div, "1638504");
        div = service.addCandidate(div, "3575648");
        div = service.addCandidate(div, "3853897");
        div = service.addCandidate(div, "3944053");
        div = service.addCandidate(div, "3391820");
        div = service.addCandidate(div, "1441303");
        div = service.addCandidate(div, "3822713");
        div = service.addCandidate(div, "3516039");*/
/*        div = service.addCandidate(div, "3501040");
        div = service.addCandidate(div, "3285765");
        div = service.addCandidate(div, "2560896");
        div = service.addCandidate(div, "2683513");
        div = service.addCandidate(div, "2523438");
        div = service.addCandidate(div, "1638504");
        div = service.addCandidate(div, "1316122");
        div = service.addCandidate(div, "2920275");
        div = service.addCandidate(div, "3822713");
        div = service.addCandidate(div, "3516039");
        div = service.addCandidate(div, "3549785");
        div = service.addCandidate(div, "3236097");
        div = service.addCandidate(div, "3575648");
        div = service.addCandidate(div, "3622118");
        div = service.addCandidate(div, "1733583");
        div = service.addCandidate(div, "1449517");
        div = service.addCandidate(div, "3944053");
        div = service.addCandidate(div, "2639219");
        div = service.addCandidate(div, "4309417");
        div = service.addCandidate(div, "3391820");
        div = service.addCandidate(div, "2818039");
        div = service.addCandidate(div, "4047074");*/
    }

    @Test
    void getCandidateTeam() {
        CandidateTeam team = service.getCandidateTeam(40L);
        for (UTRTeamCandidate candidate: team.getCandidates()){
            System.out.println(candidate.getName() + ":" + candidate.getUTR());
        }

    }

    @Test
    void importLeague() {
        League league = service.getLeague("26");
        for (Conference conf: league.getConferences()) {
            if (conf.getName().startsWith("2024")) {
                for (Session session : conf.getSessions()) {
                    for (UTRTeam team : session.getTeams()) {
                        service.importTeam(team.getId(), league);
                    }
                }
            }
        }
    }

    @Test
    void importEvent() {
        for (Division div: loader.getDivisions()) {
            service.importTeam(div);
        }
    }

}