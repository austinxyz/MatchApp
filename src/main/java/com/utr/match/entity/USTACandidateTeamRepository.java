package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface USTACandidateTeamRepository extends CrudRepository<USTACandidateTeam, Long> {

    USTACandidateTeam findByName(String name);

    List<USTACandidateTeam> findAll();

    List<USTACandidateTeam> findByDivision_Id(long id);

    List<USTACandidateTeam> findByCaptain_Id(long id);

}
