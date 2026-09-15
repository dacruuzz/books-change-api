package br.com.bookschange.api.application.address.usecases;

import br.com.bookschange.api.application.address.adapters.in.dtos.response.AddressResponseDTO;
import br.com.bookschange.api.application.address.mappers.AddressMapper;
import br.com.bookschange.api.application.address.ports.in.FindAddressPortIn;
import br.com.bookschange.api.application.address.ports.out.FindAddressPortOut;
import br.com.bookschange.api.domain.models.Address;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindAddressUseCase implements FindAddressPortIn {

    private final AddressMapper mapper;
    private final FindAddressPortOut findAddressPortOut;

    @Override
    public AddressResponseDTO findByUuid(UUID uuid) {
        log.info("Buscando endereço por uuid | uuid: {}", uuid);

        Address foundAddress = findAddressPortOut.findByUuidOrThrow(uuid);

        log.info("Endereço encontrado | uuid: {} | cep: {}", foundAddress.getUuid(), foundAddress.getZipCode());
        return mapper.entityToAddressResponseDto(foundAddress);
    }
}
