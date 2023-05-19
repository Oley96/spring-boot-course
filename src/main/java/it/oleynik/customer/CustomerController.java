package it.oleynik.customer;

import it.oleynik.customer.dto.CustomerDTO;
import it.oleynik.customer.dto.CustomerRegistrationRequest;
import it.oleynik.customer.dto.CustomerUpdateRequest;
import it.oleynik.jwt.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {

    private final CustomerService customerService;
    private final JwtTokenService jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public CustomerController(CustomerService customerService,
                              JwtTokenService jwtUtil,
                              @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/customers")
    public List<CustomerDTO> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO fetchCustomer(@PathVariable(name = "id") Integer id) {
        return customerService.getCustomer(id);
    }

    @PostMapping("/customers")
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);

        UserDetails details = userDetailsService.loadUserByUsername(request.email());
        String jwtToken = jwtUtil.generateAccessToken(details);

//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(request.email(), request.password());
//        Authentication auth = authManager.authenticate(authenticationToken);
//
//        CustomUsrDetails user = (CustomUsrDetails) usrDetailsService.loadUserByUsername(request.username);
//        String access_token = tokenService.generateAccessToken(user);
//        String refresh_token = tokenService.generateRefreshToken(user);

        return ResponseEntity
                .ok()
                .body(jwtToken);

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
