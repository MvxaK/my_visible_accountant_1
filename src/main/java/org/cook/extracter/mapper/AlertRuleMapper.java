package org.cook.extracter.mapper;

import org.cook.extracter.entity.AlertRuleEntity;
import org.cook.extracter.model.AlertRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlertRuleMapper {

    @Mapping(target = "userId", expression = "java(EntityIdUtils.extractId(entity.getUser()))")
    @Mapping(target = "categoryId", expression = "java(EntityIdUtils.extractId(entity.getCategory()))")
    AlertRule toModel(AlertRuleEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    AlertRuleEntity toEntity(AlertRule model);

}
