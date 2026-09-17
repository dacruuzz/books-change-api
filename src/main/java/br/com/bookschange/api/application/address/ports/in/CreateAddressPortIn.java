package br.com.bookschange.api.application.address.ports.in;

import br.com.bookschange.api.application.address.adapters.in.dtos.request.CreateAddressRequestDTO;
import br.com.bookschange.api.application.address.adapters.in.dtos.response.AddressResponseDTO;

public interface CreateAddressPortIn {
    AddressResponseDTO create(CreateAddressRequestDTO request);
}
