package com.utr.match.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Line {

    String name;
    @JsonIgnore
    float utrLimit;
    @JsonIgnore
    int femaleCount;

    @JsonIgnore
    int count = 10;

    @JsonIgnore
    List<PlayerPair> matchedPairs;

    public Line(String name, float utrLimit, int femaleCount) {
        this.name = name;
        this.utrLimit = utrLimit;
        this.femaleCount = femaleCount;
        this.matchedPairs = new ArrayList<>();
    }

    public float getUtrLimit() {
        return utrLimit;
    }

    public List<PlayerPair> getTopPairs() {
        return getTopNPairs(this.count);
    }

    @JsonIgnore
    public List<PlayerPair> getMatchedPairs() {
        matchedPairs.sort((o1, o2) -> Float.compare(o2.getTotalUTR(), o1.getTotalUTR()));
        return matchedPairs;
    }

    public void resetMatchedPairs(Set<String> pairNames) {
        List<PlayerPair> result = new ArrayList<>();

        for (PlayerPair pair: matchedPairs) {
            if (pairNames.contains(pair.getPairName())) {
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

    boolean isMatch(PlayerPair pair) {
        return pair.getWCount() >= this.femaleCount &&
                pair.getTotalUTR() <= this.utrLimit ;
    }

    public void addMatchedPair(PlayerPair pair) {
        if (isMatch(pair)) {
            this.matchedPairs.add(pair);
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
