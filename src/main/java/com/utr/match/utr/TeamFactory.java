package com.utr.match.utr;

import com.utr.match.entity.UTRTeamCandidate;
import com.utr.match.entity.DivisionEntity;
import com.utr.match.entity.UTRTeamEntity;
import com.utr.match.entity.UTRTeamMember;
import com.utr.match.model.Line;
import com.utr.match.model.PlayerPair;
import com.utr.model.Player;
import com.utr.match.model.Team;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("singleton")
public class TeamFactory {

    Map<String, CandidateTeam> candidateTeams = new HashMap<>();
    Map<String, Team> teams = new HashMap<>();

    public Team createTeam(UTRTeamEntity teamEntity) {
        String teamId = Long.toString(teamEntity.getId());
        if (teams.containsKey(teamId)) {
            return teams.get(teamId);
        }

        Team team = initTeam(teamEntity);

        teams.put(teamId, team);

        return team;
    }

    private Team initTeam(UTRTeamEntity teamEntity) {
        Team team = new Team(teamEntity.getName());
        team.setTeamId(String.valueOf(teamEntity.getId()));

        for (UTRTeamMember member: teamEntity.getPlayers()) {
            team.getPlayers().add(toPlayer(member));
        }
        if (teamEntity.getType()!=null && teamEntity.getType().equals("Gold")) {
            team.getLines().put("D1", new Line("D1", (float) 17, 0));
            team.getLines().put("D2", new Line("D2", (float) 15, 0));
            team.getLines().put("D3", new Line("D3", (float) 13, 0));
            team.getLines().put("D4", new Line("D4", (float) 11, 0));
        } else {
            team.getLines().put("D1", new Line("D1", (float) 12.5, 0));
            team.getLines().put("D2", new Line("D2", (float) 10.5, 0));
            team.getLines().put("D3", new Line("D3", (float) 9.5, 0));
            team.getLines().put("D4", new Line("D4", (float) 8.0, 0));
        }

        int size = teamEntity.getPlayers().size();
        for (int i=0; i< size-1; i++) {
            Player player1 = team.getPlayers().get(i);
            for (int j=i+1; j<size; j++) {
                Player player2 = team.getPlayers().get(j);
                PlayerPair pair = new PlayerPair(player1, player2);
                for (Line line : team.getLines().values()) {
                    line.addMatchedPair(pair);
                }
            }
        }

        return team;
    }

    public CandidateTeam createCandidateTeam(DivisionEntity div) {
        return createCandidateTeam(div, false);
    }

    public CandidateTeam createCandidateTeam(DivisionEntity div, boolean forceUpdate) {

        String teamId = Long.toString(div.getId());
        if (candidateTeams.containsKey(teamId)) {
            if (!forceUpdate) {
                CandidateTeam existTeam = candidateTeams.get(teamId);
                if (existTeam.getCandidates().size() == div.getCandidates().size()) {
                    return candidateTeams.get(teamId);
                }
            }
            candidateTeams.remove(teamId);
        }

        CandidateTeam team = null;
        if (div.getEvent().getType().equals("HC23")) {
            team = createHCCandidateTeam(div);
        }

        if (team == null) {
            return null;
        }

        initLinePairs(team);

        candidateTeams.put(teamId, team);

        return team;
    }

    private void initLinePairs(CandidateTeam team) {
        int size = team.getCandidates().size();
        for (int i=0; i< size-1; i++) {
            UTRTeamCandidate candidate1 = team.getCandidates().get(i);
            Player player1 = toPlayer(candidate1);
            for (int j=i+1; j<size; j++) {
                UTRTeamCandidate candidate2 = team.getCandidates().get(j);
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

    private Player toPlayer(UTRTeamCandidate candidate) {
        Player player = new Player(candidate.getFirstName(), candidate.getLastName(), candidate.getGender(), Double.toString(candidate.getUTR()));
        player.setdUTR(candidate.getDUTR());
        player.setsUTR(candidate.getSUTR());
        player.setdUTRStatus(candidate.getDUTRStatus());
        player.setsUTRStatus(candidate.getSUTRStatus());
        player.setUTR(String.valueOf(candidate.getUTR()));
        player.setId(candidate.getUtrId());
        return player;
    }

    private Player toPlayer(UTRTeamMember member) {
        Player player = new Player(member.getFirstName(), member.getLastName(), member.getGender(), Double.toString(member.getMatchUTR()));
        player.setdUTR(member.getDUTR());
        player.setsUTR(member.getSUTR());
        player.setdUTRStatus(member.getDUTRStatus());
        player.setsUTRStatus(member.getSUTRStatus());
        player.setUTR(String.valueOf(member.getMatchUTR()));
        player.setId(member.getUtrId());
        player.setUstaRating(member.getUSTARating());
        player.setSuccessRate(member.getSuccessRate());
        player.setWholeSuccessRate(member.getWholeSuccessRate());
        return player;
    }

    private CandidateTeam createHCCandidateTeam(DivisionEntity div) {
        CandidateTeam team = new CandidateTeam(div);
        team.getLines().put("D1", new Line("D1", (float) 12.5, 0));
        team.getLines().put("D2", new Line("D2", (float) 10.5, 0));
        team.getLines().put("D3", new Line("D3", (float) 9.5, 0));
        team.getLines().put("D4", new Line("D4", (float) 8.0, 0));
        return team;
    }
}
