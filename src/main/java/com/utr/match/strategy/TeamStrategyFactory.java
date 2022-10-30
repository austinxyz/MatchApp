package com.utr.match.strategy;

public class TeamStrategyFactory {

    public static BaseTeamStrategy getStrategy(int strategyNo) {
        switch (strategyNo) {
            case 0: return new BaseTeamStrategy();
            case 1: return new MoreVariableTeamStrategy();
            case 2: return new LimitedLinesTeamStrategy();
            case 3: return new FixedPairTeamStrategy();
            case 4: return new FixedPairWithMoreVariableTeamStrategy();
            default: return new MoreVariableTeamStrategy();
        }
    }
}
