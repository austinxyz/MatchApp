package com.utr.match.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Line {

    String name;
    float utrLimit;
    @JsonIgnore
    int femaleCount;

    @JsonIgnore
    int count = 12;

    @JsonIgnore
    float pairScope = 2.0f;

    @JsonIgnore
    List<PlayerPair> matchedPairs;

    public int getFemaleCount() {
        return femaleCount;
    }

    public Line(String name, float utrLimit, int femaleCount) {
        this(name, utrLimit, femaleCount, 2.0f);
    }
    public Line(String name, float utrLimit, int femaleCount, float pareScope) {
        this.name = name;
        this.utrLimit = utrLimit;
        this.femaleCount = femaleCount;
        this.matchedPairs = new ArrayList<>();
        this.pairScope = pareScope;
    }

    public float getUtrLimit() {
        return utrLimit;
    }

    public List<PlayerPair> getTopPairs() {
        return getTopNPairs(this.count);
    }

    @JsonIgnore
    public List<PlayerPair> getMatchedPairs() {

        return matchedPairs;
    }

    public void resetMatchedPairs(Set<String> pairNames) {
        List<PlayerPair> result = new ArrayList<>();
        for (PlayerPair pair: matchedPairs) {
            if (pairNames.contains(pair.getPairName()) || pairNames.contains(pair.getSecondPairName())) {
                result.add(pair);
            }
        }
        matchedPairs = result;
    }

    public List<PlayerPair> getTopNPairs(int number) {
        List<PlayerPair> result = new ArrayList<>();
        getMatchedPairs();

        int index = 0;

        while (index < number && index < matchedPairs.size()) {
            result.add(matchedPairs.get(index));
            index++;
        }

        return result;
    }

    public void resetMatchPairs(int number) {

        this.matchedPairs = getTopNPairs(number);
    }

    public boolean isMatch(PlayerPair pair) {
        return pair.getWCount() >= this.femaleCount &&
                pair.getTotalUTR() <= this.utrLimit && pair.getTotalUTR() >= this.utrLimit-this.pairScope
                && !pair.large4();
    }

    public void addMatchedPair(PlayerPair pair) {
        if (isMatch(pair)) {
            this.matchedPairs.add(pair);
            matchedPairs.sort((o1, o2) -> Float.compare(o2.getTotalUTR(), o1.getTotalUTR()));
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------------\n");
        sb.append("com.utr.match.model.Line ").append(this.name).append(" for ").append(this.utrLimit)
                .append("\n");

        for (PlayerPair pair: this.getTopNPairs(10)) {
            sb.append(pair.toString()).append("\n");
        }

        return sb.toString();
    }

}
