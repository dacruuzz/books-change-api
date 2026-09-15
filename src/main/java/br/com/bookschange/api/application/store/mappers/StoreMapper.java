package br.com.bookschange.api.application.store.mappers;

import br.com.bookschange.api.application.store.adapters.in.dtos.request.CreateStoreRequestDTO;
import br.com.bookschange.api.application.store.adapters.in.dtos.request.UpdateStoreRequestDTO;
import br.com.bookschange.api.application.store.adapters.in.dtos.response.StoreResponseDTO;
import br.com.bookschange.api.domain.models.Store;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StoreMapper {
    Store createStoreRequestDtoToEntity(CreateStoreRequestDTO request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStoreRequestDtoToEntity(UpdateStoreRequestDTO request, @MappingTarget Store store);

    @Mapping(target = "ownerUuid", source = "owner.uuid")
    StoreResponseDTO entityToStoreResponseDto(Store store);
}
