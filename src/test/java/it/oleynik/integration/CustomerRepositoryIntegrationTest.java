package it.oleynik.integration;

import it.oleynik.AbstractTestContainers;
import it.oleynik.customer.Gender;
import it.oleynik.customer.db.Customer;
import it.oleynik.customer.db.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryIntegrationTest extends AbstractTestContainers {

    @Autowired
    private CustomerRepository underTest;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @Test
    void shouldExistsCustomerByEmail() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20,
                Gender.FEMALE,
                "password"
        );
        Customer saved = underTest.save(customer);

        //When
        boolean actual = underTest.existsCustomerByEmail(saved.getEmail());

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldNotExistsCustomerByEmail_WhenEmailNotPresent() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        //When
        boolean actual = underTest.existsCustomerByEmail(email);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldExistsCustomerById() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20,
                Gender.MALE,
                "password"
        );
        Customer saved = underTest.save(customer);

        //When
        boolean actual = underTest.existsCustomerById(saved.getId());

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldNotExistsCustomerById_WhenIdNotPresent() {
        Integer id = -1;

        //When
        boolean actual = underTest.existsCustomerById(id);

        //Then
        assertThat(actual).isFalse();
    }
}
