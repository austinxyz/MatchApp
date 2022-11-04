package com.utr.match.strategy;

public class TeamStrategyFactory {

    public static final int FixedWithMoreVariable = 4;

    public static BaseTeamStrategy getStrategy(int strategyNo) {
        switch (strategyNo) {
            case 0: return new BaseTeamStrategy();
            case 1: return new MoreVariableTeamStrategy();
            case 2: return new LimitedLinesTeamStrategy();
            case 3: return new FixedPairTeamStrategy();
            case FixedWithMoreVariable: return new FixedPairWithMoreVariableTeamStrategy();
            default: return new MoreVariableTeamStrategy();
        }
    }
}
