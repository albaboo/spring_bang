package com.exemple.bang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exemple.bang.entity.Role;
import com.exemple.bang.enums.TypeRole;

import jakarta.transaction.Transactional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Devuelve todos los roles almacenados en la base de datos
    List<Role> findAll();

    // Devuelve el numero tipos de roles
    @Query("SELECT COUNT(DISTINCT r.type) FROM Role r")
    Long countDistinctRoleTypes();

    // Crea nuevo rol a partir del enum Role
    @Modifying
    @Transactional
    @Query("INSERT INTO Role (name, description, type) VALUES (:name, :description, :type)")
    void createRole(
            @Param("description") String objective,
            @Param("type") TypeRole type);

}
