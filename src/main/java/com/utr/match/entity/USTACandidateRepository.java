package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface USTACandidateRepository extends CrudRepository<USTACandidate, Long> {
    List<USTACandidate> findByPlayer_Id(long id);


}
