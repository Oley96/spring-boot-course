package it.oleynik.customer.dto;

import it.oleynik.customer.Gender;

import java.util.List;

public record CustomerDTO(
        Integer id,
        String name,
        String email,
        Gender gender,
        Integer age,
        List<String> roles,
        String username) {

}

