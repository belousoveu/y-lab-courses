package belousov.eu.mapper;

import belousov.eu.model.Budget;
import belousov.eu.model.dto.BudgetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BudgetMapper {

    BudgetMapper INSTANCE = Mappers.getMapper(BudgetMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "period", source = "period")
    @Mapping(target = "amount", source = "amount")
    Budget toEntity(BudgetDto dto);


    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "period", source = "period")
    @Mapping(target = "amount", source = "amount")
    BudgetDto toDto(Budget budget);

}
