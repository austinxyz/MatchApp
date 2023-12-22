package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UTRTeamCandidateRepository extends CrudRepository<UTRTeamCandidate, Long> {
    List<UTRTeamCandidate> findByPlayer_Id(long id);


}