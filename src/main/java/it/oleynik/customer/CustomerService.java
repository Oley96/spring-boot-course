package it.oleynik.customer;

import it.oleynik.exception.DuplicatedResourceException;
import it.oleynik.exception.RequestValidationException;
import it.oleynik.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customer with id [%s] not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest request) {
        String email = request.email();
        if (customerDao.existsCustomerWithEmail(email)) {
            throw new DuplicatedResourceException("email already taken");
        }

        Customer customer = new Customer(request.name(), request.email(), request.age());
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
        Customer customer = this.getCustomer(id);
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

        if (!needToUpdate) {
            throw new RequestValidationException("no data changes needed");
        }

        customerDao.updateCustomer(customer);
    }
}
