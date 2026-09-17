package br.com.bookschange.api.application.user.mappers;

import br.com.bookschange.api.application.user.adapters.in.dtos.request.CreateUserRequestDTO;
import br.com.bookschange.api.application.user.adapters.in.dtos.request.UpdateUserRequestDTO;
import br.com.bookschange.api.application.user.adapters.in.dtos.response.UserResponseDTO;
import br.com.bookschange.api.domain.models.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User createUserRequestDtoToEntity(CreateUserRequestDTO request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserRequestDtoToEntity(UpdateUserRequestDTO request, @MappingTarget User user);

    UserResponseDTO entityToUserResponseDto(User user);
}
