package it.oleynik.integration;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import it.oleynik.customer.Gender;
import it.oleynik.customer.dto.CustomerDTO;
import it.oleynik.customer.dto.CustomerRegistrationRequest;
import it.oleynik.customer.dto.CustomerUpdateRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final Random RANDOM = new Random();

    @Test
    void shouldRegisterCustomer() {
        // Given
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.firstName();
        String email = fakerName.lastName().toLowerCase() + UUID.randomUUID() + "@mail.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = Gender.MALE;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age, Gender.MALE, "password");

        String token = webTestClient.post()
                .uri("/api/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri("/api/v1/customers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        int id = allCustomers.stream().filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id).findFirst().orElseThrow();

        CustomerDTO expectedDTO = new CustomerDTO(id, name, email, gender, age, List.of("ROLE_USER"), email);

        Assertions.assertThat(allCustomers).contains(expectedDTO);


        webTestClient.get()
                .uri("/api/v1/customers/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .isEqualTo(expectedDTO);
    }

    @Test
    void shouldDeleteCustomer() {
        // Given
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.firstName();
        String email = fakerName.lastName().toLowerCase() + UUID.randomUUID() + "@mail.com";
        int age = RANDOM.nextInt(1, 100);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age, Gender.FEMALE, "password");

        String token = webTestClient.post()
                .uri("/api/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri("/api/v1/customers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();


        int id = allCustomers.stream().filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id).findFirst().orElseThrow();

        webTestClient.delete()
                .uri("/api/v1/customers/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.get()
                .uri("/api/v1/customers/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    void shouldUpdateCustomer() {
        // Given
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.firstName();
        String email = fakerName.lastName().toLowerCase() + UUID.randomUUID() + "@mail.com";
        Gender gender = Gender.FEMALE;
        int age = RANDOM.nextInt(1, 100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age, gender, "password");

        String token = webTestClient.post()
                .uri("/api/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();;

        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri("/api/v1/customers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();


        int id = allCustomers.stream().filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id).findFirst().orElseThrow();

        // When
        String newName = "Vova";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(newName, null, null, null);
        webTestClient.put()
                .uri("/api/v1/customers/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        CustomerDTO expected = new CustomerDTO(id, newName, email, gender, age, List.of("ROLE_USER"), email);

        // Then
        CustomerDTO actual = webTestClient.get()
                .uri("/api/v1/customers/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
