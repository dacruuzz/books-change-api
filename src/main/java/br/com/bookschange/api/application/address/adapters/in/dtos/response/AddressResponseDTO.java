package br.com.bookschange.api.application.address.adapters.in.dtos.response;

import java.util.UUID;

public record AddressResponseDTO(
        UUID uuid,
        String zipCode,
        String street,
        String number,
        String state,
        String country,
        String city,
        String complement,
        String neighborhood
) {
}
