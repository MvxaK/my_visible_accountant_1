package org.cook.extracter.mapper;

import org.cook.extracter.entity.CategoryEntity;
import org.cook.extracter.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "userId", expression = "java(EntityIdUtils.extractId(entity.getUser()))")
    Category toModel(CategoryEntity entity);

    @Mapping(target = "user", ignore = true)
    CategoryEntity toEntity(Category model);

}
