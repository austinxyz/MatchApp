package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DivisionRepository extends CrudRepository<DivisionEntity, Long> {

    List<DivisionEntity> findByNameLike(String name);

}
