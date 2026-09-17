package br.com.bookschange.api.application.address.ports.in;

import br.com.bookschange.api.application.address.adapters.in.dtos.request.UpdateAddressRequestDTO;
import br.com.bookschange.api.application.address.adapters.in.dtos.response.AddressResponseDTO;

import java.util.UUID;

public interface UpdateAddressPortIn {
    AddressResponseDTO update(UUID uuid, UpdateAddressRequestDTO request);
}
