package com.exemple.bang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.exemple.bang.entity.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

}
