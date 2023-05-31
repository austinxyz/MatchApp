package com.utr.match.utr;

import com.utr.match.entity.DivisionCandidate;
import com.utr.match.entity.DivisionEntity;
import com.utr.match.entity.PlayerEntity;
import com.utr.match.model.Line;
import com.utr.match.model.PlayerPair;
import com.utr.match.model.Team;
import com.utr.model.Event;
import com.utr.model.Player;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("singleton")
public class TeamFactory {

    Map<String, CandidateTeam> teams = new HashMap<>();

    public CandidateTeam createTeam(DivisionEntity div) {
        return createTeam(div, false);
    }

    public CandidateTeam createTeam(DivisionEntity div, boolean forceUpdate) {

        String teamId = Long.toString(div.getId());
        if (teams.containsKey(teamId)) {
            if (!forceUpdate) {
                CandidateTeam existTeam = teams.get(teamId);
                if (existTeam.getCandidates().size() == div.getCandidates().size()) {
                    return teams.get(teamId);
                }
            }
            teams.remove(teamId);
        }

        CandidateTeam team = null;
        if (div.getEvent().getType().equals("HC23")) {
            team = createHCTeam(div);
        }

        if (team == null) {
            return null;
        }

        initLinePairs(team);

        teams.put(teamId, team);

        return team;
    }

    private void initLinePairs(CandidateTeam team) {
        int size = team.getCandidates().size();
        for (int i=0; i< size-1; i++) {
            DivisionCandidate candidate1 = team.getCandidates().get(i);
            Player player1 = toPlayer(candidate1);
            for (int j=i+1; j<size; j++) {
                DivisionCandidate candidate2 = team.getCandidates().get(j);
                Player player2 = toPlayer(candidate2);
                PlayerPair pair = new PlayerPair(player1, player2);
                for (Line line : team.getLines().values()) {
                    if (line.isMatch(pair)) {
                        candidate1.addPartner(line.getName(), pair);
                        candidate2.addPartner(line.getName(), pair);
                    }
                    line.addMatchedPair(pair);
                }
            }
        }
    }

    private Player toPlayer(DivisionCandidate candidate) {
        Player player = new Player(candidate.getFirstName(), candidate.getLastName(), candidate.getGender(), Double.toString(candidate.getUTR()));
        player.setdUTR(candidate.getDUTR());
        player.setsUTR(candidate.getSUTR());
        player.setdUTRStatus(candidate.getDUTRStatus());
        player.setsUTRStatus(candidate.getSUTRStatus());
        player.setUTR(String.valueOf(candidate.getUTR()));
        player.setId(candidate.getUtrId());
        return player;
    }

    private CandidateTeam createHCTeam(DivisionEntity div) {
        CandidateTeam team = new CandidateTeam(div);
        team.getLines().put("D1", new Line("D1", (float) 12.5, 0));
        team.getLines().put("D2", new Line("D2", (float) 10.5, 0));
        team.getLines().put("D3", new Line("D3", (float) 9.5, 0));
        team.getLines().put("D4", new Line("D4", (float) 8.0, 0));
        return team;
    }
}
