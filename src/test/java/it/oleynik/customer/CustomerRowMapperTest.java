package it.oleynik.customer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRowMapperTest {

    @Mock
    private ResultSet rs;
    private CustomerRowMapper underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerRowMapper();
    }

    @Test
    void shouldMapRow() throws SQLException {
        // Given
        Integer id = 1, age = 25;
        String email = "vova@gmail.com", name = "Vova";

        when(rs.getInt("id")).thenReturn(id);
        when(rs.getInt("age")).thenReturn(age);
        when(rs.getString("name")).thenReturn(name);
        when(rs.getString("email")).thenReturn(email);

        // When
        Customer actual = underTest.mapRow(rs, 0);

        // Then
        Customer expected = new Customer(id, name, email, age);
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}