package it.oleynik;

import com.github.javafaker.Faker;
import it.oleynik.customer.Customer;
import it.oleynik.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootEntry {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootEntry.class, args);

    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            Faker faker = new Faker();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();

            Customer customer = new Customer(
                    firstName + " " + lastName,
                    firstName.toLowerCase() + "." + lastName.toLowerCase() + "@mail.com",
                    faker.number().numberBetween(16, 99)
            );

            customerRepository.save(customer);
        };
    }
}
