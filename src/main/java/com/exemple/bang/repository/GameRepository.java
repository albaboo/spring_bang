package com.exemple.bang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.exemple.bang.entity.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

}
