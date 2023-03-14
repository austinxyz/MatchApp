package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

public interface USTATeamMatchRepository extends CrudRepository<USTATeamMatch, Long> {
    USTATeamMatch findByMatchDateAndTeam_IdAndOpponentTeam_IdAndHome(Date matchDate, long id, long id1, Boolean home);

    List<USTATeamMatch> findByTeamOrderByMatchDateAsc(USTATeamEntity team);
    USTATeamMatch findByMatchDateAndTeam_IdAndOpponentTeam_Id(Date matchDate, long teamId, long oppTeamId);
}