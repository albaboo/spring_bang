package com.exemple.bang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.exemple.bang.entity.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

}
