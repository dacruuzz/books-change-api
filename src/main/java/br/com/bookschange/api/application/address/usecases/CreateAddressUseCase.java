package br.com.bookschange.api.application.address.usecases;

import br.com.bookschange.api.application.address.adapters.in.dtos.request.CreateAddressRequest;
import br.com.bookschange.api.application.address.adapters.in.dtos.response.AddressResponse;
import br.com.bookschange.api.application.address.mappers.AddressMapper;
import br.com.bookschange.api.application.address.ports.in.CreateAddressPortIn;
import br.com.bookschange.api.application.address.ports.out.SaveAddressPortOut;
import br.com.bookschange.api.application.address.services.normalizers.AddressNormalizer;
import br.com.bookschange.api.application.address.services.validators.AddressValidator;
import br.com.bookschange.api.domain.models.Address;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateAddressUseCase implements CreateAddressPortIn {

    private final AddressMapper mapper;
    private final AddressNormalizer normalizer;
    private final AddressValidator validator;
    private final SaveAddressPortOut saveAddressPortOut;

    @Override
    @Transactional
    public AddressResponse create(CreateAddressRequest request) {
        log.info("Criando endereço | cep: {}", request.zipCode());

        validator.validateZipCode(request.zipCode());

        Address address = mapper.createAddressRequestToEntity(request);

        normalizer.normalizeData(address);

        Address createdAddress = saveAddressPortOut.save(address);

        log.info("Endereço criado | uuid: {} | cep: {}", createdAddress.getUuid(), createdAddress.getZipCode());
        return mapper.entityToAddressResponse(createdAddress);
    }
}
