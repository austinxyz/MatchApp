package com.utr.match.strategy;

import com.utr.match.model.*;

import java.util.*;

public class FixedPairTeamStrategy extends BaseTeamStrategy {

    Map<String, Set<String>> fixedPairs;

    public FixedPairTeamStrategy() {
        this.name = "Fixed Pair";
    }

    public void setFixedPairs(Map<String, Set<String>> fixedPairs) {
        this.fixedPairs = fixedPairs;
    }

    @Override
    protected List<Line> prepareLines(Team team) {
        List<Line> res = new ArrayList<>();

        for (Line line: team.getLines().values()) {
            if (fixedPairs.get(line.getName()) != null) {
                line.resetMatchedPairs(fixedPairs.get(line.getName()));
            }
            res.add(line);
        }

        res.sort(Comparator.comparingInt(o -> o.getMatchedPairs().size()));
        return res;
    }

}
