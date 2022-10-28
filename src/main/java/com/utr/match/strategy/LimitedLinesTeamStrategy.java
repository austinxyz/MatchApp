package com.utr.match.strategy;

import com.utr.match.model.Lineup;

public class LimitedLinesTeamStrategy extends BaseTeamStrategy {

    public LimitedLinesTeamStrategy() {
        this.count = 5;
        this.name = "limit_lines";
    }

    @Override
    protected float getScore(Lineup lineup) {
        return lineup.getD2().getGAP()
                + lineup.getD3().getGAP()
                + lineup.getMd().getGAP();
    }

}
