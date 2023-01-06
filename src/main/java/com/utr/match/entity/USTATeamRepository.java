package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface USTATeamRepository extends CrudRepository<USTATeam, Long> {

    List<USTATeam> findByNameLike(String name);

    List<USTATeam> findAll();

}
