package com.utr.match.entity;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

public interface USTATeamMatchRepository extends CrudRepository<USTATeamMatch, Long> {

    List<USTATeamMatch> findByTeamOrderByMatchDateAsc(USTATeam team);
    USTATeamMatch findByMatchDateAndTeam_IdAndOpponentTeam_Id(Date matchDate, long teamId, long oppTeamId);
}