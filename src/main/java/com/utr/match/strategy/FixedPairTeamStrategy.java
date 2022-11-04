package com.utr.match.strategy;

import com.utr.match.model.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FixedPairTeamStrategy extends BaseTeamStrategy {

    Map<String, Set<String>> fixedPairs;

    public FixedPairTeamStrategy() {
        this.name = "Fixed Pair";
    }

    public void setFixedPairs(Map<String, Set<String>> fixedPairs) {
        this.fixedPairs = fixedPairs;
    }
    protected List<PlayerPair> getTopNPairs(Line line) {
        if (fixedPairs.get(line.getName()) != null) {
            line.resetMatchedPairs(fixedPairs.get(line.getName()));
        }
        return line.getTopNPairs(20);
    }

}
