package it.oleynik.customer;

import it.oleynik.exception.DuplicatedResourceException;
import it.oleynik.exception.RequestValidationException;
import it.oleynik.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void shouldGetAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void shouldGetCustomer() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Vova", "vova@gmail.com", 10);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        Customer actual = underTest.getCustomer(id);

        // Then
        Assertions.assertThat(actual).isEqualTo(customer);
    }

    @Test
    void shouldThrowWhenCustomerNotPresent() {
        // Given
        int id = 10;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void shouldAddCustomer() {
        // Given
        String email = "vova@gmail.com";
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Vova", email, 20);
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        // When
        underTest.addCustomer(request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer captured = customerArgumentCaptor.getValue();

        Assertions.assertThat(captured.getId()).isNull();
        Assertions.assertThat(captured.getName()).isEqualTo(request.name());
        Assertions.assertThat(captured.getEmail()).isEqualTo(request.email());
        Assertions.assertThat(captured.getAge()).isEqualTo(request.age());
    }

    @Test
    void shouldThrowWhenCustomerExistsByEmail() {
        // Given
        String email = "vova@gmail.com";
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Vova", email, 20);
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

        // When
        Assertions.assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicatedResourceException.class)
                .hasMessage("email already taken");

        // Then
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void shouldDeleteCustomerById() {
        // Given
        int id = 10;
        when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void shouldThrowWhenCustomerNotPresentById() {
        // Given
        int id = 10;
        when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        // Then
        Assertions.assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        // Then
        verify(customerDao, never()).deleteCustomerById(id);
    }

    @Test
    void shouldUpdateAllCustomerProperties() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Vova", "vova@gmail.com", 26);
        CustomerUpdateRequest request = new CustomerUpdateRequest("Volodya", "vova+1@gmail.com", 27);
        when(customerDao.existsCustomerWithId(id)).thenReturn(true);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(false);

        // When
        underTest.updateCustomer(request, id);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer actual = customerArgumentCaptor.getValue();

        Assertions.assertThat(actual.getId()).isEqualTo(id);
        Assertions.assertThat(actual.getEmail()).isEqualTo(request.email());
        Assertions.assertThat(actual.getName()).isEqualTo(request.name());
        Assertions.assertThat(actual.getAge()).isEqualTo(request.age());
    }

    @Test
    void shouldUpdateOnlyName() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Vova", "vova@gmail.com", 26);
        CustomerUpdateRequest request = new CustomerUpdateRequest("Volodya", null, null);
        when(customerDao.existsCustomerWithId(id)).thenReturn(true);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        underTest.updateCustomer(request, id);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer actual = customerArgumentCaptor.getValue();

        Assertions.assertThat(actual.getId()).isEqualTo(id);
        Assertions.assertThat(actual.getName()).isEqualTo(request.name());
    }

    @Test
    void shouldUpdateOnlyEmail() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Vova", "vova@gmail.com", 26);
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, "vova+1@gmail.com", null);
        when(customerDao.existsCustomerWithId(id)).thenReturn(true);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(false);

        // When
        underTest.updateCustomer(request, id);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer actual = customerArgumentCaptor.getValue();

        Assertions.assertThat(actual.getId()).isEqualTo(id);
        Assertions.assertThat(actual.getEmail()).isEqualTo(request.email());
    }

    @Test
    void shouldUpdateOnlyAge() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Vova", "vova@gmail.com", 26);
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, 27);
        when(customerDao.existsCustomerWithId(id)).thenReturn(true);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        underTest.updateCustomer(request, id);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer actual = customerArgumentCaptor.getValue();

        Assertions.assertThat(actual.getId()).isEqualTo(id);
        Assertions.assertThat(actual.getAge()).isEqualTo(request.age());
    }

    @Test
    void shouldThrowWhenCustomerNotPresentWhileUpdating() {
        // Given
        int id = 10;
        CustomerUpdateRequest request = new CustomerUpdateRequest("Vova", "vova@gmail.com", 25);
        when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        // Then
        Assertions.assertThatThrownBy(() -> underTest.updateCustomer(request, id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void shouldThrowWhenCustomerWhenEmailTaken() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Vova", "vova@gmail.com", 26);
        CustomerUpdateRequest request = new CustomerUpdateRequest("Vova", "vova+1@gmail.com", 25);

        when(customerDao.existsCustomerWithId(id)).thenReturn(true);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(true);

        // Then
        Assertions.assertThatThrownBy(() -> underTest.updateCustomer(request, id))
                .isInstanceOf(DuplicatedResourceException.class)
                .hasMessage("email already taken");
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void shouldThrowWhenCustomerDataTheSame() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Vova", "vova@gmail.com", 26);
        CustomerUpdateRequest request = new CustomerUpdateRequest("Vova", "vova@gmail.com", 26);

        when(customerDao.existsCustomerWithId(id)).thenReturn(true);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // Then
        Assertions.assertThatThrownBy(() -> underTest.updateCustomer(request, id))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes needed");
        verify(customerDao, never()).updateCustomer(any());
    }
}