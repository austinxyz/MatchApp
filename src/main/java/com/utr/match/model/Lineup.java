package com.utr.match.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Lineup {

    public String getStrategyName() {
        return strategyName;
    }

    private Map<String, LinePair> pairs = new HashMap<>();

    String strategyName;

    @JsonIgnore
    boolean has7Member = false;

    @JsonIgnore
    Set<Player> players = new HashSet<>();

    public Lineup(String strategyName, Map<String, LinePair> pairs, Set<Player> players, boolean has7Member) {
        this.strategyName = strategyName;
        this.pairs = pairs;
        this.players = players;
        this.has7Member = has7Member;
    }

    public Lineup(String teamName) {
        this.strategyName = teamName;
    }

    public LinePair getLinePair(String lineName) {
        return pairs.get(lineName);
    }
    public void setLinePair(Line line, PlayerPair pair) {
        LinePair linePair = new LinePair(line, pair);
        addPlayerPair(linePair);
    }

    public Lineup clone() {

        Map<String, LinePair> newPairs = new HashMap<>();
        for (Map.Entry<String, LinePair> entry :this.pairs.entrySet()) {
            LinePair pair = entry.getValue();
            newPairs.put(entry.getKey(), new LinePair(pair.getLine(), pair.getPair()));
        }

        Set<Player> players = new HashSet<>(this.players);

        return new Lineup(strategyName, newPairs, players, this.has7Member);
    }
    private boolean addPlayerPair(LinePair pair) {
        if (players.contains(pair.getPlayer1())) {
            return false;
        } else if (players.contains(pair.getPlayer2())) {
            return false;
        }
        if (has7Member && pair.getPair().has7Member() ) {
            return false;
        }
        has7Member = pair.getPair().has7Member();

        players.add(pair.getPlayer1());
        players.add(pair.getPlayer2());
        pairs.put(pair.getLine().getName(), pair);

        return true;
    }

    @JsonProperty(value="D1")
    public LinePair getD1() {
        return pairs.get("D1");
    }

    @JsonProperty(value="D2")
    public LinePair getD2() {
        return pairs.get("D2");
    }

    @JsonProperty(value="D3")
    public LinePair getD3() {
        return pairs.get("D3");
    }

    @JsonProperty(value="MD")
    public LinePair getMd() {
        return pairs.get("MD");
    }

    @JsonProperty(value="WD")
    public LinePair getWd() {
        return pairs.get("WD");
    }

    @JsonIgnore
    public boolean isValid() {
        int utr7Number = getUTR7Numbers();

        int urt55Number = getUTR55Numbers();

        LinePair d1 = this.getD1();
        LinePair d2 = this.getD2();
        LinePair d3 = this.getD3();

        float d1UTR = d1==null ? (float)100.0: d1.getPair().getTotalUTR();
        float d2UTR = d2==null ? d1UTR: d2.getPair().getTotalUTR();
        float d3UTR = d3==null ? d2UTR: d3.getPair().getTotalUTR();

        boolean numberAllowed = utr7Number == 0 || (utr7Number == 1 && urt55Number <= 1);
        return d1UTR >= d2UTR && d2UTR >= d3UTR && numberAllowed;
    }

    private int getUTR7Numbers() {
        int number = 0;
        for (LinePair pair: pairs.values()) {
           number = number +  (pair.getPair().has7Member() ? 1 : 0 );
        }
        return number;
    }

    private int getUTR55Numbers() {
        int number = 0;
        for (LinePair pair: pairs.values()) {
            if (pair.getLine().equals("MD") || pair.getPairName().equals("WD")) {
                number = number +  (pair.getPair().has55Member() ? 1 : 0 );
            }
        }
        return number;
    }

    private int get55UTRNumber(LinePair pair) {
        return pair == null ? 0 : (pair.getPair().has55Member() ? 1 : 0);
    }

    public int completedPairNumber() {
        return pairs.size();
    }

    @JsonIgnore
    public float getGAPs() {
        float gaps = 0.0F;
        for (LinePair pair : pairs.values()) {
            gaps = gaps + pair.getGAP();
        }
        return gaps;
    }

    private float getGap(LinePair pair) {
        return pair==null? 0: pair.getGAP();
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("------------LINEUP-------------------\n");
        for (LinePair pair: pairs.values()) {
            res.append(pair.getLine().getName()).append(":").append(pair.getPair().toString()).append("\n");
        }
        return res.toString();
    }
}
