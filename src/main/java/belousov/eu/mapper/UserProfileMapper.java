package belousov.eu.mapper;

import belousov.eu.model.User;
import belousov.eu.model.dto.UserProfileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserProfileMapper {

    UserProfileMapper INSTANCE = Mappers.getMapper(UserProfileMapper.class);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    UserProfileDto toDto(User user);
}
