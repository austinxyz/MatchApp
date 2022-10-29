package com.utr.match.strategy;

public class TeamStrategyFactory {

    public static BaseTeamStrategy getStrategy(int strategyNo) {
        switch (strategyNo) {
            case 0: return new BaseTeamStrategy();
            case 1: return new MoreVariableTeamStrategy();
            case 2: return new LimitedLinesTeamStrategy();
            default: return new MoreVariableTeamStrategy();
        }
    }
}
