package it.oleynik.integration;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import it.oleynik.customer.Gender;
import it.oleynik.customer.auth.AuthenticationRequest;
import it.oleynik.customer.auth.AuthenticationResponse;
import it.oleynik.customer.dto.CustomerDTO;
import it.oleynik.customer.dto.CustomerRegistrationRequest;
import it.oleynik.jwt.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtTokenService tokenService;


    private static final Random RANDOM = new Random();


    @Test
    void shouldAuthenticateCustomer() {
        // Given
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.firstName();
        String email = fakerName.lastName().toLowerCase() + UUID.randomUUID() + "@mail.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = Gender.MALE;
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(name, email, age, Gender.MALE, "password");

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(registrationRequest.email(), registrationRequest.password());

        webTestClient.post()
                .uri("/api/v1/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        webTestClient.post()
                .uri("/api/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
                .uri("/api/v1/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult();

        AuthenticationResponse authenticationResponse = result.getResponseBody();
        CustomerDTO customerDTO = authenticationResponse.customerDTO();

        assertThat(tokenService.parseToken(authenticationResponse.token()))
                .isEqualTo(customerDTO.username());

        assertThat(customerDTO.age()).isEqualTo(age);
        assertThat(customerDTO.email()).isEqualTo(email);
        assertThat(customerDTO.name()).isEqualTo(name);
        assertThat(customerDTO.username()).isEqualTo(email);
        assertThat(customerDTO.gender()).isEqualTo(gender);
        assertThat(customerDTO.roles()).isEqualTo(List.of("ROLE_USER"));
    }
}
