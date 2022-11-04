package com.utr.match.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

public class Lineup {

    public String getStrategyName() {
        return strategyName;
    }

    String strategyName;
    @JsonProperty(value="D1")
    LinePair d1;
    @JsonProperty(value="D2")
    LinePair d2;
    @JsonProperty(value="D3")
    LinePair d3;
    @JsonProperty(value="MD")
    LinePair md;
    @JsonProperty(value="WD")
    LinePair wd;

    @JsonIgnore
    boolean has7Member = false;

    @JsonIgnore
    Set<Player> players = new HashSet<>();

    public Lineup(String strategyName, LinePair d1, LinePair d2, LinePair d3, LinePair md, LinePair wd, Set<Player> players, boolean has7Member) {
        this.strategyName = strategyName;
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
        this.md = md;
        this.wd = wd;
        this.players = players;
        this.has7Member = has7Member;
    }

    public Lineup(String teamName) {
        this.strategyName = teamName;
    }

    public void setD1(LinePair d1) {

        if (addPlayerPair(d1)) {
            this.d1 = d1;
        }

    }

    public LinePair getLinePair(String lineName) {
        if (lineName.equals("D1")) {
            return this.getD1();
        }
        if (lineName.equals("D2")) {
            return this.getD2();
        }
        if (lineName.equals("D3")) {
            return this.getD3();
        }
        if (lineName.equals("MD")) {
            return this.getMd();
        }
        if (lineName.equals("WD")) {
            return this.getWd();
        }
        return null;
    }
    public void setLinePair(Line line, PlayerPair pair) {
        LinePair linePair = new LinePair(line, pair);
        if (line.getName().equals("D1")) {
            this.setD1(linePair);
        }
        if (line.getName().equals("D2")) {
            this.setD2(linePair);
        }
        if (line.getName().equals("D3")) {
            this.setD3(linePair);
        }
        if (line.getName().equals("MD")) {
            this.setMd(linePair);
        }
        if (line.getName().equals("WD")) {
            this.setWd(linePair);
        }
    }

    public Lineup clone() {
        LinePair d1 = (this.d1==null)? null: new LinePair(this.d1.getLine(), this.d1.getPair());
        LinePair d2 = (this.d2==null)? null: new LinePair(this.d2.getLine(), this.d2.getPair());
        LinePair d3 = (this.d3==null)? null: new LinePair(this.d3.getLine(), this.d3.getPair());
        LinePair md = (this.md==null)? null: new LinePair(this.md.getLine(), this.md.getPair());
        LinePair wd = (this.wd==null)? null: new LinePair(this.wd.getLine(), this.wd.getPair());

        Set<Player> players = new HashSet<>(this.players);

        return new Lineup(strategyName, d1, d2, d3, md, wd, players, this.has7Member);
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
        return true;
    }

    public void setD2(LinePair d2) {
        if (addPlayerPair(d2)) {
            this.d2 = d2;
        }
    }

    public void setD3(LinePair d3) {
        if (addPlayerPair(d3)) {
            this.d3 = d3;
        }
    }

    public void setMd(LinePair md) {
        if (addPlayerPair(md)) {
            this.md = md;
        }
    }

    public void setWd(LinePair wd) {
        if (addPlayerPair(wd)) {
            this.wd = wd;
        }
    }

    public LinePair getD1() {
        return d1;
    }

    public LinePair getD2() {
        return d2;
    }

    public LinePair getD3() {
        return d3;
    }

    public LinePair getMd() {
        return md;
    }

    public LinePair getWd() {
        return wd;
    }

    @JsonIgnore
    public boolean isValid() {
        int utr7Number = getUTRNumber(d1)
                + getUTRNumber(d2)
                + getUTRNumber(d3)
                + getUTRNumber(md)
                + getUTRNumber(wd);

        int urt55Number = get55UTRNumber(md)
                + get55UTRNumber(wd);

        float d1UTR = d1==null ? (float)100.0: d1.getPair().getTotalUTR();
        float d2UTR = d2==null ? d1UTR: d2.getPair().getTotalUTR();
        float d3UTR = d3==null ? d2UTR: d3.getPair().getTotalUTR();

        boolean numberAllowed = utr7Number == 0 || (utr7Number == 1 && urt55Number <= 1);
        return d1UTR >= d2UTR && d2UTR >= d3UTR && numberAllowed;
    }

    private int getUTRNumber(LinePair pair) {
        return pair == null ? 0 : (pair.getPair().has7Member() ? 1 : 0);
    }

    private int get55UTRNumber(LinePair pair) {
        return pair == null ? 0 : (pair.getPair().has55Member() ? 1 : 0);
    }

    public int completedPairNumber() {
        return (d1!=null?1:0) +
                (d2!=null?1:0) +
                (d3!=null?1:0) +
                (md!=null?1:0) +
                (wd!=null?1:0);
    }

    @JsonIgnore
    public float getGAPs() {
        if (completedPairNumber()==5) {
            return d1.getGAP() + d2.getGAP() + d3.getGAP() + md.getGAP() + wd.getGAP();
        }
        return (float)-1;
    }

    @Override
    public String toString() {
        return "------------LINEUP-------------------" + "\n" +
                "D1:" + (d1!=null? d1.getPair(): "") + "\n" +
                "D2:" + (d2!=null? d2.getPair(): "") + "\n" +
                "D3:" + (d3!=null? d3.getPair(): "") + "\n" +
                "MD:" + (md!=null? md.getPair(): "") + "\n" +
                "WD:" + (wd!=null? wd.getPair(): "") + "\n";
    }
}
