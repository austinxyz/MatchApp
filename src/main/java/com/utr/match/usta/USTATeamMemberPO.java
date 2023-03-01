package com.utr.match.usta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTATeamEntity;
import com.utr.match.entity.USTATeamMember;

import javax.persistence.*;

public class USTATeamMemberPO {

    @JsonIgnore
    private USTATeamMember member;

    public USTATeamMemberPO(USTATeamMember member) {
        this.member = member;
    }

    public String getDivisionName() {
        return this.member.getTeam().getDivisionName();
    }

    public String getFlightName() {
        return this.member.getTeam().getAreaCode() + "-" + this.member.getTeam().getFlight();
    }

    public String getTeamName() {
        return this.member.getTeam().getName();
    }

    public long getTeamId() {
        return this.member.getTeam().getId();
    }

    public String getTeamAlias() {
        return this.member.getTeam().getAlias();
    }

    public int getWinNo() {
        return this.member.getWinNo();
    }

    public int getLostNo() {
        return this.member.getLostNo();
    }
}
