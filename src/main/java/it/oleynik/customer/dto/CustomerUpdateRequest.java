package it.oleynik.customer.dto;

import it.oleynik.customer.Gender;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age,
        Gender gender
) {
}
