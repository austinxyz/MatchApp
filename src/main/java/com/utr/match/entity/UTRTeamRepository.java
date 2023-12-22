package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UTRTeamRepository extends CrudRepository<UTRTeamEntity, Long> {
    List<UTRTeamEntity> findByCaptain_Id(long id);

    List<UTRTeamEntity> findByName(String name);

    UTRTeamEntity findByUtrTeamId(String utrTeamId);

    List<UTRTeamEntity> findAll();

}
