package org.cook.extracter.mapper;

import org.cook.extracter.entity.TransactionEntity;
import org.cook.extracter.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "documentId", expression = "java(EntityIdUtils.extractId(entity.getDocument()))")
    @Mapping(target = "userId", expression = "java(EntityIdUtils.extractId(entity.getUser()))")
    @Mapping(target = "categoryId", expression = "java(EntityIdUtils.extractId(entity.getCategory()))")
    Transaction toModel(TransactionEntity entity);

    @Mapping(target = "document", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    TransactionEntity toEntity(Transaction model);

}
