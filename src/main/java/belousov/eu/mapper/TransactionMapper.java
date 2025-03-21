package belousov.eu.mapper;

import belousov.eu.model.Transaction;
import belousov.eu.model.dto.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "operationType", source = "operationType")
    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "userId", source = "user.id")
    TransactionDto toDto(Transaction transaction);
}
