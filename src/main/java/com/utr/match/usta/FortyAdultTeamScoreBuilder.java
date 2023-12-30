package com.utr.match.usta;

import com.utr.match.entity.USTATeamEntity;
import com.utr.match.entity.USTATeamMember;

import java.util.List;

public class FortyAdultTeamScoreBuilder implements ITeamScoreBuilder {
    @Override
    public double getScore(USTATeamEntity teamEntity) {
        List<USTATeamMember> players = teamEntity.getPlayers();

        if (players.isEmpty()) {
            return 0.0d;
        }

        players.sort((USTATeamMember o1, USTATeamMember o2) -> Double.compare(o2.getDUTR(), o1.getDUTR()));

        double top3 = 0.0d;
        double top10 = 0.0d;
        int index = 0;


        for (USTATeamMember member: players) {
            if (index <3) {
                top3 = top3 + member.getDUTR();
            }
            if (index <10) {
                top10 = top10 + member.getDUTR();
            } else {
                break;
            }
            index++;
        }

        players.sort((USTATeamMember o1, USTATeamMember o2) -> Double.compare(o2.getSUTR(), o1.getSUTR()));

        double top2 = 0.0d;
        if (players.size() > 1) {
            top2 = players.get(0).getSUTR() + players.get(1).getSUTR();
        } else {
            top2 = players.get(0).getSUTR() * 2.0d;
        }

        return (top3/3.0d * 2.0d * 2.0d + top10/10.0d * 2.0d * 4.0d + top2/2.0d) / 7.0d;

    }


}
