package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface USTADivisionRepository extends CrudRepository<USTADivision, Long> {

    List<USTADivision> findByNameLike(String name);

    USTADivision findByName(String name);

    List<USTADivision> findAll();
}
