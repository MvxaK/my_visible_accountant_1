package org.cook.extracter.mapper;

import org.cook.extracter.entity.UserEntity;
import org.cook.extracter.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    User toModel(UserEntity entity);

    @Mapping(target = "passwordHash", ignore = true)
    UserEntity toEntity(User model);

}
