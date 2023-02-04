package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface USTATeamScoreCardRepository extends CrudRepository<USTATeamScoreCard, Long> {
    List<USTATeamScoreCard> findByHomeTeamNameNull();


}