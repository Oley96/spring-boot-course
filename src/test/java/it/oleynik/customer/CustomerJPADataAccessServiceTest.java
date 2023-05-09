package it.oleynik.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerJPADataAccessServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    private CustomerJPADataAccessService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @Test
    void shouldSelectAllCustomers() {
        // When
        underTest.selectAllCustomers();

        // Then
        verify(customerRepository).findAll();
    }

    @Test
    void shouldSelectCustomerById() {
        // Given
        int id = 1;

        // When
        underTest.selectCustomerById(id);

        // Then
        verify(customerRepository).findById(id);
    }

    @Test
    void shouldInsertCustomer() {
        // Given
        Customer customer = new Customer("Vova", "vova@gmail.com", 22);

        // When
        underTest.insertCustomer(customer);

        // Then
        verify(customerRepository).save(customer);
    }

    @Test
    void shouldExistsCustomerWithEmail() {
        // Given
        String email = "vova@gmail.com";

        // When
        underTest.existsCustomerWithEmail(email);

        // Then
        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void shouldExistsCustomerWithId() {
        // Given
        Integer id = 1;

        // When
        underTest.existsCustomerWithId(id);

        // Then
        verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void shouldDeleteCustomerById() {
        // Given
        Integer id = 1;

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void shouldUpdateCustomer() {
        Customer customer = new Customer("Vova", "vova@gmail.com", 22);

        // When
        underTest.updateCustomer(customer);

        // Then
        verify(customerRepository).save(customer);
    }
}
