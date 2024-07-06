package com.example.userms.repository;

import com.example.userms.model.entity.Role;
import com.example.userms.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findRoleByName(RoleType roleType);
}
