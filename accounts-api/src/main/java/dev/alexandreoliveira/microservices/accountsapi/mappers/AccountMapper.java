package dev.alexandreoliveira.microservices.accountsapi.mappers;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.AccountEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDTO;
import org.mapstruct.EnumMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AccountMapper {

    @Mapping(source = "accountType", target = "accountType", qualifiedByName = "toTransformAccountType")
    @Mapping(source = "dto", target = "user", qualifiedByName = "toEntityByAccount")
    AccountEntity toEntity(AccountDTO dto);

    @Mapping(source = "accountType", target = "accountType")
    @Mapping(source = "accountNumber", target = "accountNumber")
    @Mapping(source = "user.id", target = "userId")
    AccountDTO toDto(AccountEntity entity);

    @EnumMapping(nameTransformationStrategy = MappingConstants.CASE_TRANSFORMATION, configuration = "upper")
    @Named("toTransformAccountType")
    AccountTypeEnum toTransformAccountType(String accountType);
}
