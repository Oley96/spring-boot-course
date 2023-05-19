package it.oleynik.customer;

import it.oleynik.customer.db.Customer;
import it.oleynik.customer.db.CustomerDao;
import it.oleynik.customer.dto.CustomerDTO;
import it.oleynik.customer.dto.CustomerDTOMapper;
import it.oleynik.customer.dto.CustomerRegistrationRequest;
import it.oleynik.customer.dto.CustomerUpdateRequest;
import it.oleynik.exception.DuplicatedResourceException;
import it.oleynik.exception.RequestValidationException;
import it.oleynik.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao,
                           BCryptPasswordEncoder passwordEncoder,
                           CustomerDTOMapper customerDTOMapper) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerDao.selectAllCustomers()
                .stream()
                .map(customerDTOMapper)
                .toList();
    }

    public CustomerDTO getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("customer with id [%s] not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest request) {
        String email = request.email();
        if (customerDao.existsCustomerWithEmail(email)) {
            throw new DuplicatedResourceException("email already taken");
        }

        Customer customer = new Customer(
                request.name(),
                request.email(),
                request.age(),
                request.gender(),
                passwordEncoder.encode(request.password())
        );
        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer id) {
        if (!customerDao.existsCustomerWithId(id)) {
            throw new ResourceNotFoundException("customer with id [%s] not found".formatted(id));
        }

        customerDao.deleteCustomerById(id);
    }

    public void updateCustomer(CustomerUpdateRequest request, Integer id) {
        if (!customerDao.existsCustomerWithId(id)) {
            throw new ResourceNotFoundException("customer with id [%s] not found".formatted(id));
        }
        Customer customer = customerDao.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customer with id [%s] not found".formatted(id)));
        boolean needToUpdate = false;

        if (request.name() != null && !request.name().equals(customer.getName())) {
            customer.setName(request.name());
            needToUpdate = true;
        }

        if (request.email() != null && !request.email().equals(customer.getEmail())) {
            if (customerDao.existsCustomerWithEmail(request.email())) {
                throw new DuplicatedResourceException("email already taken");
            }

            customer.setEmail(request.email());
            needToUpdate = true;
        }

        if (request.age() != null && !request.age().equals(customer.getAge())) {
            customer.setAge(request.age());
            needToUpdate = true;
        }

        if (request.gender() != null && !request.gender().equals(customer.getGender())) {
            customer.setGender(request.gender());
            needToUpdate = true;
        }

        if (!needToUpdate) {
            throw new RequestValidationException("no data changes needed");
        }

        customerDao.updateCustomer(customer);
    }
}
