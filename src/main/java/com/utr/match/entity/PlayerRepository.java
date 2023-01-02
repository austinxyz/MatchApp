package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface PlayerRepository extends CrudRepository<PlayerEntity, Long> {

    List<PlayerEntity> findAll();

    PlayerEntity findByUtrId(String id);

    List<PlayerEntity> findByFirstNameLike(String name);

    List<PlayerEntity> findByLastNameLike(String name);
}
