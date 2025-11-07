package com.exemple.bang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.exemple.bang.entity.UseCard;

@Repository
public interface UseCardRepository extends JpaRepository<UseCard, Long> {

}
