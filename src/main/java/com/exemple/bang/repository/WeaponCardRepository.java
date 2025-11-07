package com.exemple.bang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.exemple.bang.entity.WeaponCard;

@Repository
public interface WeaponCardRepository extends JpaRepository<WeaponCard, Long> {

}
