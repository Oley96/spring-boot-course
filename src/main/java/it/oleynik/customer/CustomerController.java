package it.oleynik.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/customers/{id}")
    public Customer fetchCustomer(@PathVariable(name = "id") Integer id) {
        return customerService.getCustomer(id);
    }

    @PostMapping("/customers")
    public void registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable(name = "id") Integer id) {
        customerService.deleteCustomerById(id);
    }

    @PutMapping("/customers/{id}")
    public void updateCustomer(@RequestBody CustomerUpdateRequest request,
                               @PathVariable(name = "id") Integer id) {
        customerService.updateCustomer(request, id);
    }
}
