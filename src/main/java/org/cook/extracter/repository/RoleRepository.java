package org.cook.extracter.repository;

import org.cook.extracter.entity.RoleEntity;
import org.cook.extracter.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByRole(Role role);
}
