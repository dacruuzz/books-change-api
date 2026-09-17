package br.com.bookschange.api.application.address.ports.in;

import br.com.bookschange.api.application.address.adapters.in.dtos.response.AddressResponseDTO;

import java.util.UUID;

public interface FindAddressPortIn {
    AddressResponseDTO findByUuid(UUID uuid);
}
