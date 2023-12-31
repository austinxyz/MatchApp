package com.utr.match.usta;

import com.utr.match.entity.USTATeamEntity;

public interface ITeamRatingBuilder {
    double getRating(USTATeamEntity teamEntity);

    double getStrongestRating(USTATeamEntity teamEntity);
}
