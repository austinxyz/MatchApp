package com.utr.match.usta;

import com.utr.match.entity.USTATeamEntity;

public class ComboTeamRatingBuilder implements ITeamRatingBuilder {
    public ComboTeamRatingBuilder(float level) {
    }

    @Override
    public double getRating(USTATeamEntity teamEntity) {
        return 0.0d;
    }

    @Override
    public double getStrongestRating(USTATeamEntity teamEntity) {
        return 0;
    }
}
