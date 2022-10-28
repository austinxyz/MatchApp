package com.utr.match.strategy;

import com.utr.match.model.Lineup;
import com.utr.match.model.PlayerPair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoreVariableTeamStrategy extends BaseTeamStrategy {

    public MoreVariableTeamStrategy() {
        this.name = "More Variable";
    }
    @Override
    protected boolean isGoodCandidate(List<Lineup> candidateLineups, Lineup newCandidateLineup) {

        if (!checkPairVariable("D1", candidateLineups, newCandidateLineup)) {
            return false;
        }
        if (!checkPairVariable("D2", candidateLineups, newCandidateLineup)) {
            return false;
        }
        if (!checkPairVariable("D3", candidateLineups, newCandidateLineup)) {
            return false;
        }
        if (!checkPairVariable("WD", candidateLineups, newCandidateLineup)) {
            return false;
        }
        return checkPairVariable("MD", candidateLineups, newCandidateLineup);
    }

    private boolean checkPairVariable(String lineName, List<Lineup> candidateLineups, Lineup newCandidateLineup) {

        int currentCandidateSize = candidateLineups.size();

        Set<PlayerPair> pairs = new HashSet<>();

        for (Lineup lineup: candidateLineups) {
            pairs.add(lineup.getLinePair(lineName).getPair());
        }

        pairs.add(newCandidateLineup.getLinePair(lineName).getPair());

        if (pairs.size() == 1 && currentCandidateSize >= 3 ) {
            return false;
        }
        return pairs.size() != 2 || currentCandidateSize != 4;
    }
}
