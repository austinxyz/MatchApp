package com.utr.match.strategy;

import com.utr.match.model.*;

import java.util.List;
import java.util.Map;

public class FixedPairTeamStrategy extends BaseTeamStrategy {

    Map<String, String> fixedPairs;

    public FixedPairTeamStrategy(int count, Map<String, String> fixedPairs) {
        this.count = count;
        this.name = "Fixed Pair";
        this.fixedPairs = fixedPairs;
    }

    protected List<PlayerPair> getTopNPairs(Line line) {
        if (fixedPairs.get(line.getName()) != null) {
            line.resetMatchedPairs(fixedPairs.get(line.getName()));
        }
        return line.getTopNPairs(20);
    }

}
