package br.com.bookschange.api.application.address.mappers;

import br.com.bookschange.api.application.address.adapters.in.dtos.request.CreateAddressRequestDTO;
import br.com.bookschange.api.application.address.adapters.in.dtos.request.UpdateAddressRequestDTO;
import br.com.bookschange.api.application.address.adapters.in.dtos.response.AddressResponseDTO;
import br.com.bookschange.api.domain.models.Address;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address createAddressRequestDtoToEntity(CreateAddressRequestDTO request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAddressRequestDtoToEntity(UpdateAddressRequestDTO request, @MappingTarget Address address);

    AddressResponseDTO entityToAddressResponse(Address address);
}
