package it.oleynik;

import com.github.javafaker.Faker;
import it.oleynik.customer.Gender;
import it.oleynik.customer.db.Customer;
import it.oleynik.customer.db.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SpringBootEntry {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootEntry.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            PasswordEncoder encoder = new BCryptPasswordEncoder(10);
            Faker faker = new Faker();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();

            Customer customer = new Customer(
                    firstName + " " + lastName,
                    firstName.toLowerCase() + "." + lastName.toLowerCase() + "@mail.com",
                    faker.number().numberBetween(16, 99),
                    Gender.FEMALE,
                    encoder.encode(faker.internet().password())
            );

            customerRepository.save(customer);
        };
    }
}
