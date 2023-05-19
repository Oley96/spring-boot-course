package it.oleynik.customer.dto;

import it.oleynik.customer.Gender;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age,
        Gender gender,
        String password
) {
}
