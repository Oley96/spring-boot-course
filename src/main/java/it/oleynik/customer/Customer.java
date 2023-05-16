package it.oleynik.customer;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "customer", uniqueConstraints = {
        @UniqueConstraint(
                name = "customer_id_unique",
                columnNames = "email"),
})
public class Customer {
    @Id
    @SequenceGenerator(
            name = "customer_id_seq",
            sequenceName = "customer_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "customer_id_seq"
    )
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Integer age;

    private String gender;

    public Customer() {
    }

    public Customer(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public Customer(Integer id, String name, String email, Integer age) {
        this(name, email, age);
        this.id = id;
    }
}
