package com.utr.match.strategy;

import com.utr.match.model.Line;
import com.utr.match.model.LinePair;
import com.utr.match.model.Lineup;
import com.utr.match.model.Team;

import java.util.List;
import java.util.Map;

public class FixedPairTeamStrategy extends BaseTeamStrategy {

    Map<String, String> fixedPairs;

    public FixedPairTeamStrategy(int count, Map<String, String> fixedPairs) {
        this.count = count;
        this.name = "fixed_pair";
        this.fixedPairs = fixedPairs;
    }

    @Override
    protected boolean isGoodCandidate(List<Lineup> candidateLineups, Lineup newCandidateLineup) {

        for (String name: fixedPairs.keySet()) {
            String pair = fixedPairs.get(name);
            LinePair candidatePair = newCandidateLineup.getLinePair(name);

            if (!pair.equals(candidatePair.getPairName())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void matchingPairs(Team team) {
        for (Line line: team.getLines()) {
            team.possiblePairs(line);
            if (fixedPairs.get(line.getName()) != null) {
                line.resetMatchedPairs(fixedPairs.get(line.getName()));
            }
        }
    }
}
