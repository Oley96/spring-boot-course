package it.oleynik.customer.auth;

import it.oleynik.customer.dto.CustomerDTO;

public record AuthenticationResponse(String token, CustomerDTO customerDTO) {
}
