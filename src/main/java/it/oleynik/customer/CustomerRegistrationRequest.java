package it.oleynik.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}
