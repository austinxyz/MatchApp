package com.utr.match.usta;

import com.utr.match.entity.USTATeamEntity;
import com.utr.match.entity.USTATeamMember;

import java.util.List;

public class FortyAdultTeamRatingBuilder implements ITeamRatingBuilder {
    @Override
    public double getRating(USTATeamEntity teamEntity) {
        return calRating(teamEntity, 3, 10, 2);
    }

    private static double calRating(USTATeamEntity teamEntity, int d1Count, int doubleCount, int singleCount) {
        List<USTATeamMember> players = teamEntity.getPlayers();

        if (players.isEmpty()) {
            return 0.0d;
        }

        players.sort((USTATeamMember o1, USTATeamMember o2) -> Double.compare(o2.getDUTR(), o1.getDUTR()));

        double topD1 = 0.0d;
        double topDouble = 0.0d;
        int index = 0;

        for (USTATeamMember member: players) {
            if (index < d1Count) {
                topD1 = topD1 + member.getDUTR();
            }
            if (index < doubleCount) {
                topDouble = topDouble + member.getDUTR();
            } else {
                break;
            }
            index++;
        }

        d1Count = index < d1Count? index: d1Count;
        doubleCount = index < doubleCount? index: doubleCount;

        double topD1UTR = topD1/(double)d1Count * 2.0d;
        double topDoubleUTR = topDouble/(double)doubleCount * 2.0d;

        players.sort((USTATeamMember o1, USTATeamMember o2) -> Double.compare(o2.getSUTR(), o1.getSUTR()));

        double topSingle = 0.0d;
        index = 0;
        for (USTATeamMember member: players) {
            if (index < singleCount) {
                topSingle = topSingle + member.getSUTR();
            }
            index++;
        }

        singleCount = index < singleCount? index: singleCount;

        double topSingleUTR = topSingle/(double)singleCount;

        return (topD1UTR* 2.0d + topDoubleUTR* 4.0d + topSingleUTR)/7.0d;

    }

    @Override
    public double getStrongestRating(USTATeamEntity teamEntity) {
        return calRating(teamEntity, 2, 6, 1);
    }


}
