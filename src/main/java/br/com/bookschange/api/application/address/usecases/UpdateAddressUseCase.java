package br.com.bookschange.api.application.address.usecases;

import br.com.bookschange.api.application.address.adapters.in.dtos.request.UpdateAddressRequest;
import br.com.bookschange.api.application.address.adapters.in.dtos.response.AddressResponse;
import br.com.bookschange.api.application.address.mappers.AddressMapper;
import br.com.bookschange.api.application.address.ports.in.UpdateAddressPortIn;
import br.com.bookschange.api.application.address.ports.out.FindAddressPortOut;
import br.com.bookschange.api.application.address.ports.out.SaveAddressPortOut;
import br.com.bookschange.api.application.address.services.AddressNormalizer;
import br.com.bookschange.api.application.address.services.AddressValidator;
import br.com.bookschange.api.domain.models.Address;
import br.com.bookschange.api.shared.services.TextNormalizer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateAddressUseCase implements UpdateAddressPortIn {

    private final AddressMapper mapper;
    private final AddressNormalizer normalizer;
    private final AddressValidator validator;
    private final FindAddressPortOut findAddressPortOut;
    private final SaveAddressPortOut saveAddressPortOut;

    @Override
    @Transactional
    public AddressResponse update(UUID uuid, UpdateAddressRequest request) {
        log.info("Buscando endereço | uuid: {}", uuid);

        Address foundAddress = findAddressPortOut.findByUuidOrThrow(uuid);

        if (request.zipCode() != null) {
            validator.validateZipCode(request.zipCode());
        }

        mapper.updateAddressRequestToEntity(request, foundAddress);

        normalizer.normalizeData(foundAddress);

        Address updatedAddress = saveAddressPortOut.save(foundAddress);

        log.info("Edição de endereço feita com sucesso | uuid: {}", updatedAddress.getUuid());
        return mapper.entityToAddressResponse(updatedAddress);
    }
}
