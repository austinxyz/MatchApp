package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface USTATeamRepository extends CrudRepository<USTATeam, Long> {

    List<USTATeam> findByNameLike(String name);

    USTATeam findByName(String name);

    List<USTATeam> findAll();

    List<USTATeam> findByDivision_Id(long id);

   // List<USTATeam> findByDivision(String divisionId);

}
