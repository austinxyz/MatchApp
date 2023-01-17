package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

public interface USTATeamMatchLineRepository extends CrudRepository<USTATeamMatchLine, Long> {
    USTATeamMatchLine findByMatch_IdAndName(long id, String name);
}