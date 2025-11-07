package com.exemple.bang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.exemple.bang.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

}
