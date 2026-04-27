package org.cook.extracter.mapper;

import org.cook.extracter.entity.DocumentEntity;
import org.cook.extracter.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "userId", expression = "java(EntityIdUtils.extractId(entity.getUser()))")
    Document toModel(DocumentEntity entity);

    @Mapping(target = "user", ignore = true)
    DocumentEntity toEntity(Document model);

}
