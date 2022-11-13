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
            Set<String> fixedPair = fixedPairs.get(line.getName());
            if (fixedPair != null) {
                fixedPairs.put(line.getName(), prepareFixedPairs(line, fixedPair));
                line.resetMatchedPairs(fixedPairs.get(line.getName()));
            }
            res.add(line);
        }

        res.sort(Comparator.comparingInt(o -> o.getMatchedPairs().size()));
        return res;
    }

    protected Set<String> prepareFixedPairs(Line line, Set<String> pairNames) {
        Set<String> result = new HashSet<>();

        for (String pairName: pairNames) {
            if (pairName.indexOf(",") > 0) {
                result.add(pairName);
            } else {
                for (PlayerPair pair: line.getMatchedPairs()) {
                    if (pair.getPairName().indexOf(pairName) >= 0) {
                        result.add(pair.getPairName());
                    }
                }
            }
        }
        return result;
    }

}
