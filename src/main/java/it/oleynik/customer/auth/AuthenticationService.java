package it.oleynik.customer.auth;

import it.oleynik.customer.db.Customer;
import it.oleynik.customer.dto.CustomerDTO;
import it.oleynik.customer.dto.CustomerDTOMapper;
import it.oleynik.jwt.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final CustomerDTOMapper customerDTOMapper;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager,
                                 CustomerDTOMapper customerDTOMapper, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.customerDTOMapper = customerDTOMapper;
        this.jwtTokenService = jwtTokenService;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        Customer principal = (Customer) authentication.getPrincipal();
        String token = jwtTokenService.generateAccessToken(principal);
        CustomerDTO customerDTO = customerDTOMapper.apply(principal);

        return new AuthenticationResponse(token, customerDTO);
    }
}
