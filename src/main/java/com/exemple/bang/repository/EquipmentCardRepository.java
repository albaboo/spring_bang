package com.exemple.bang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.exemple.bang.entity.EquipmentCard;

@Repository
public interface EquipmentCardRepository extends JpaRepository<EquipmentCard, Long> {

}
