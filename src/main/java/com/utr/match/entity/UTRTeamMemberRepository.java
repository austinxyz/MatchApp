package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UTRTeamMemberRepository extends CrudRepository<UTRTeamMember, Long> {
    List<UTRTeamMember> findByPlayer_Id(long id);

}