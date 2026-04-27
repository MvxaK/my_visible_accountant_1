package org.cook.extracter.mapper;

import org.cook.extracter.entity.RoleEntity;
import org.cook.extracter.model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    default Role toModel(RoleEntity roleEntity){
        if(roleEntity == null)
            return null;

        return roleEntity.getRole();
    }

    default RoleEntity toEntity(Role role){
        if(role == null)
            return null;

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRole(role);

        return roleEntity;
    }
}
