package com.utr.match.utr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utr.match.entity.DivisionCandidate;
import com.utr.match.entity.DivisionEntity;
import com.utr.match.model.Line;
import com.utr.match.model.Lineup;
import com.utr.model.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CandidateTeam {

    Map<String, Line> lines;
    @JsonIgnore
    DivisionEntity div;

    public CandidateTeam(DivisionEntity div) {
        this.div = div;
        this.lines = new HashMap<>();
    }

    public String getDisplayName() {
        return div.getChineseName();
    }

    public Long getTeamId() {
        return div.getId();
    }

    public String getName() {
        return div.getName();
    }

    public String getEnglishName() {
        return div.getEnglishName();
    }

    public List<DivisionCandidate> getCandidates() {
        return div.getCandidates();
    }

    public Map<String, Line> getLines() {
        return lines;
    }

    @Override
    public String toString() {
        return "CandidateTeam{" +
                "name='" + getName() + '\'' +
                ", candidates=" + getCandidates() +
                ", displayName='" + getDisplayName() + '\'' +
                ", teamId='" + getTeamId() + '\'' +
                '}';
    }
}
