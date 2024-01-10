package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface USTADivisionRepository extends CrudRepository<USTADivision, Long> {

    List<USTADivision> findByNameLike(String name);

    USTADivision findByName(String name);

    List<USTADivision> findAll();

    List<USTADivision> findByLeague_Id(long id);

    List<USTADivision> findByLeague_Year(String year);

    List<USTADivision> findByLeague_Status(String status);



}
