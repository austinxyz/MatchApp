package com.utr.match.usta;

public class TeamRatingFactory {
    public static ITeamRatingBuilder getTeamScoreBuilder(String ageLevel, boolean mixed, boolean combo, float level) {

        if (mixed) {
            return new MixedTeamRatingBuilder(level);
        }

        if (combo) {
            return new ComboTeamRatingBuilder(level);
        }

        if (ageLevel.equals("40")) {
            return new FortyAdultTeamRatingBuilder();
        }

        return new EighteenAdultTeamRatingBuilder();

    }
}
