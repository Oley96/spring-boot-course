package it.oleynik.integration;

import it.oleynik.AbstractTestContainers;
import it.oleynik.customer.Customer;
import it.oleynik.customer.CustomerJDBCDataAccessService;
import it.oleynik.customer.CustomerRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceIntegrationTest extends AbstractTestContainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void shouldSelectAllCustomers() {
        //Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );
        underTest.insertCustomer(customer);

        //When
        List<Customer> actual = underTest.selectAllCustomers();

        //Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void shouldSelectCustomerById() {
        //Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst().map(Customer::getId)
                .orElseThrow();

        //When
        Optional<Customer> actual = underTest.selectCustomerById(id);

        //Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                }
        );
    }

    @Test
    void shouldReturnEmptyWhenSelectCustomerById() {
        //Given
        int id = -1;

        //When
        Optional<Customer> actual = underTest.selectCustomerById(id);

        //Then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldExistsCustomerWithEmail() {
        //Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );

        underTest.insertCustomer(customer);

        //When
        boolean actual = underTest.existsCustomerWithEmail(customer.getEmail());

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldNotExistsCustomerWithEmail() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        //When
        boolean actual = underTest.existsCustomerWithEmail(email);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldExistsCustomerWithId() {
        //Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );

        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst().map(Customer::getId)
                .orElseThrow();

        //When
        boolean actual = underTest.existsCustomerWithId(id);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldNotExistsCustomerWithId() {
        //Given
        int id = -1;

        //When
        boolean actual = underTest.existsCustomerWithId(id);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldDeleteCustomerById() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );

        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst().map(Customer::getId)
                .orElseThrow();

        //When
        underTest.deleteCustomerById(id);

        //Then
        assertThat(underTest.existsCustomerWithId(id)).isFalse();
    }

    @Test
    void shouldUpdateCustomer() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );

        //When
        underTest.insertCustomer(customer);
        Customer existing = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst()
                .orElseThrow();
        existing.setEmail(FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID());
        existing.setName(FAKER.name().fullName());
        existing.setAge(23);

        underTest.updateCustomer(existing);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(existing.getId());
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(existing.getId());
                    assertThat(c.getName()).isEqualTo(existing.getName());
                    assertThat(c.getEmail()).isEqualTo(existing.getEmail());
                    assertThat(c.getAge()).isEqualTo(existing.getAge());
                }
        );
    }
}
