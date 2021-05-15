package com.spring.SpringbootCustomTokenStore.repository;


import com.spring.SpringbootCustomTokenStore.entites.Role;
import com.spring.SpringbootCustomTokenStore.util.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleType user);
}
