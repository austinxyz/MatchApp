package com.utr.match.usta;

public class TeamScoreFactory {
    public static ITeamScoreBuilder getTeamScoreBuilder(String ageLevel, boolean mixed, float level) {

        if (mixed) {
            return new MixedTeamScoreBuilder(level);
        }

        if (ageLevel.equals("40")) {
            return new FortyAdultTeamScoreBuilder();
        }

        return new EighteenAdultTeamScoreBuilder();

    }
}
