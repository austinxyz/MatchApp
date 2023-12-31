package com.utr.match.usta;

import com.utr.match.entity.USTATeamEntity;
import com.utr.match.entity.USTATeamMember;

import java.util.ArrayList;
import java.util.List;

public class MixedTeamRatingBuilder implements ITeamRatingBuilder {

    float level;

    public MixedTeamRatingBuilder(float level) {
        this.level = level;
    }

    @Override
    public double getRating(USTATeamEntity teamEntity) {

        return getRating(teamEntity, 5);

    }
    @Override
    public double getStrongestRating(USTATeamEntity teamEntity) {
        return getRating(teamEntity, 3);
    }

    private double getRating(USTATeamEntity teamEntity, int top) {

        List<USTATeamMember> players = teamEntity.getPlayers();

        if (players.isEmpty()) {
            return 0.0d;
        }

        players.sort((USTATeamMember o1, USTATeamMember o2) -> Double.compare(o2.getDUTR(), o1.getDUTR()));

        List<USTATeamMember> mPlayers = new ArrayList<>();
        List<USTATeamMember> fPlayers = new ArrayList<>();

        for (USTATeamMember player: players) {
            if (player.getGender().equals("M")) {
                mPlayers.add(player);
            } else {
                fPlayers.add(player);
            }
        }

        double[][] pair = new double[3][top];
        for (int i=0; i<3; i++) {
            float mLevel = level/2.0f + 0.5f * (float)(1-i);
            pair[i] = caculateDouble(mPlayers, fPlayers, mLevel, top);
        }

        return getScore(pair, top);
    }


    private double getScore(double[][] pair, int top) {
        double res = pair[0][top-1]* top;

        for (int i = 0; i< top - 1 ; i++) {
            for (int j = 0; j< top-i-1; j++ ) {
                double cScore = pair[0][i] * (i + 1) + pair[1][j] * (j + 1) +
                        (i + j + 2 < top ? pair[2][top - i - j - 3] * (top - i - j - 2) : 0.0d);
                System.out.println("i:" + i + ", j:" + j + "k:" + (top-i-j-3) + " res:" + cScore);
                res = Math.max(cScore, res);
            }
        }

        for (int j=0; j<top-1; j++) {
            res = Math.max(pair[1][j] * (j+1) + pair[2][top-j-2]*(top-j-1), res);
        }

        res = Math.max(res, pair[1][top-1] * top);

        return res/(double)top;
    }

    private double[] caculateDouble(List<USTATeamMember> mPlayers, List<USTATeamMember> fPlayers, float mLevel, int top) {
        float fLevel = this.level - mLevel;

        double[] res = new double[top];

        int mIndex = 0;
        int fIndex = 0;

        double sum = 0.0d;

        int index = 0;

        while (mIndex < mPlayers.size() && fIndex < fPlayers.size() && index < top) {

            while (mIndex < mPlayers.size() && Math.abs(mPlayers.get(mIndex).getLevel() - mLevel) > 0.1f ) {
                mIndex++;
            }

            while (fIndex < fPlayers.size() && Math.abs(fPlayers.get(fIndex).getLevel() - fLevel) > 0.1f ) {
                fIndex++;
            }

            if (mIndex < mPlayers.size() && fIndex < fPlayers.size()) {
                sum = sum + mPlayers.get(mIndex).getDUTR() + fPlayers.get(fIndex).getDUTR();
                res[index] = sum/(double)(index+1);
                mIndex++;
                fIndex++;
                index++;
            }

        }

        return res;
    }
}
