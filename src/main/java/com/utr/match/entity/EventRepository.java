package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<EventEntity, Long> {

    List<EventEntity> findByNameLike(String name);

    EventEntity findByEventId(String id);
}
