package dev.alexandreoliveira.microservices.cardsapi.mappers;

import dev.alexandreoliveira.microservices.cardsapi.database.entities.CardEntity;
import dev.alexandreoliveira.microservices.cardsapi.database.entities.enums.CardTypeEnum;
import dev.alexandreoliveira.microservices.cardsapi.dtos.CardDto;
import dev.alexandreoliveira.microservices.cardsapi.exceptions.MapperException;
import org.mapstruct.EnumMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", unexpectedValueMappingException = MapperException.class)
public interface CardMapper {

    CardDto toDto(CardEntity cardEntity);

    @EnumMapping(nameTransformationStrategy = MappingConstants.CASE_TRANSFORMATION, configuration = "upper")
    @Named("toTransformAccountType")
    CardTypeEnum toTransformCardType(String cardType);
}
