package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

public interface USTAMatchRepository extends CrudRepository<USTAMatch, Long> {

    List<USTAMatch> findByHomeTeam_IdOrGuestTeam_IdOrderByMatchDateAsc(long id, long id1);

    USTAMatch findByMatchDateAndHomeTeam_IdAndGuestTeam_Id(Date matchDate, long id, long id1);

}