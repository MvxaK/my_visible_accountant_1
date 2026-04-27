package org.cook.extracter.mapper;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
public final class EntityIdUtils {

    public static <T> Long extractId(T entity){
        if(entity == null)
            return null;

        try{
            return (Long) entity.getClass().getMethod("getId").invoke(entity);
        }catch (Exception e){
            return null;
        }
    }

    public static <T> List<Long> extractIds(List<T> entities){
        if(entities == null)
            return null;

        return entities.stream()
                .map(EntityIdUtils::extractId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
