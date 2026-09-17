package br.com.bookschange.api.application.address.adapters.in.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record CreateAddressRequestDTO(

        @NotBlank(message = "O CEP é obrigatório")
        String zipCode,

        @NotBlank(message = "A rua é obrigatória")
        String street,

        @NotBlank(message = "O número é obrigatório")
        String number,

        @NotBlank(message = "O estado é obrigatório")
        String state,

        @NotBlank(message = "O país é obrigatório")
        String country,

        String city,
        String complement,
        String neighborhood
) {
}