package it.oleynik.customer.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
