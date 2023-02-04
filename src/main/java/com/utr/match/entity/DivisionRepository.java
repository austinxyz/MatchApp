package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DivisionRepository extends CrudRepository<DivisionEntity, Long> {
    Optional<DivisionEntity> findByPlayers_Id(long id);

    List<DivisionEntity> findByNameLike(String name);

    DivisionEntity findByName(String name);

}
