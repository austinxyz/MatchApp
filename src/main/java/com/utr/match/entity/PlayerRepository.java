package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface PlayerRepository extends CrudRepository<PlayerEntity, Long> {

    List<PlayerEntity> findAll();

}
