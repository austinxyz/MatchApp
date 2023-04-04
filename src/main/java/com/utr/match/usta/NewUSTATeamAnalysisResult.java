package com.utr.match.usta;

import com.utr.match.entity.USTAMatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewUSTATeamAnalysisResult {

    NewUSTATeam team1;
    NewUSTATeam team2;

    List<USTAMatch> pastScores;
    Map<String, List<USTAMatch>> matchesWithSameTeam;

    public NewUSTATeamAnalysisResult(NewUSTATeam team1, NewUSTATeam team2) {
        this.team1 = team1;
        this.team2 = team2;
        this.pastScores = new ArrayList<>();
        this.matchesWithSameTeam = new HashMap<String, List<USTAMatch>>();
    }

    public NewUSTATeam getTeam1() {
        return team1;
    }

    public NewUSTATeam getTeam2() {
        return team2;
    }

    public List<USTAMatch> getPastScores() {
        return pastScores;
    }

    public Map<String, List<USTAMatch>> getMatchesWithSameTeam() {
        return matchesWithSameTeam;
    }
}
