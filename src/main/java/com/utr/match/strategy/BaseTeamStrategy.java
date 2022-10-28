package com.utr.match.strategy;

import com.utr.match.model.Lineup;
import com.utr.match.model.Team;

import java.util.ArrayList;
import java.util.List;

public class BaseTeamStrategy {
    int count = 5;
    String name = "base";

    public List<Lineup> getPreferedLineups(Team team) {
        List<Lineup> lineups = team.getLineups();

        ranking(lineups);

        return getLineups(lineups);
    }

    public String getName() {
        return name;
    }

    private boolean getLineups(List<Lineup> lineups, List<Lineup> candidateLineups, int index) {
        if (candidateLineups.size() == this.count) {
            return true;
        }

        if (lineups.size() == index) {
            return true;
        }

        Lineup newCandidateLineup = lineups.get(index);

        if (isGoodCandidate(candidateLineups, newCandidateLineup)) {
            candidateLineups.add(newCandidateLineup);
        }

        return getLineups(lineups, candidateLineups, index + 1);

    }

    protected boolean isGoodCandidate(List<Lineup> candidateLineups, Lineup newCandidateLineup){
        return true;
    }

    private List<Lineup> getLineups(List<Lineup> lineups) {
        List<Lineup> result = new ArrayList<>();

        getLineups(lineups, result, 0);

        return result;
    }

    private void ranking(List<Lineup> lineups) {
        lineups.sort((o1, o2) -> Float.compare(getScore(o1), getScore(o2)));
    }

    public void matchingPairs(Team team) {
        team.matchingPairs();
    }

    protected float getScore(Lineup lineup) {
        return lineup.getGAPs();
    }
}
