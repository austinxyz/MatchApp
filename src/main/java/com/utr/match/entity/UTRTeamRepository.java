package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UTRTeamRepository extends CrudRepository<UTRTeamEntity, Long> {

    List<UTRTeamEntity> findByName(String name);

    UTRTeamEntity findByUtrTeamId(String utrTeamId);

    List<UTRTeamEntity> findAll();

}
