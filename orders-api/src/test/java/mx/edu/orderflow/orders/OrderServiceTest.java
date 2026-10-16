package mx.edu.orderflow.orders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class OrderServiceTest {

    @Test
    void createsValidOrder() {
        // Arrange
        var s = new OrderService();
        var total = new BigDecimal("150.00");

        // Act
        var o = s.create("student-1", total);

        // Assert
        assertEquals(OrderStatus.CREATED, o.status());
        assertEquals("student-1", o.customerId());
    }

    @Test
    void rejectsNullCustomerId() {
        // Arrange
        var s = new OrderService();
        String customerId = null;
        BigDecimal total = new BigDecimal("150.00");

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> s.create(customerId, total));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void rejectsBlankCustomerId(String customerId) {
        // Arrange
        var s = new OrderService();
        BigDecimal total = new BigDecimal("150.00");

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> s.create(customerId, total));
    }

    @Test
    void rejectsNullTotal() {
        // Arrange
        var s = new OrderService();

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> s.create("student-1", null));
    }

    @Test
    void rejectsZeroTotal() {
        // Arrange
        var s = new OrderService();
        var zeroTotal = BigDecimal.ZERO;

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> s.create("student-1", zeroTotal));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.1", "-1", "-200.12", "-1000"})
    void rejectsNegativeTotal(String value) { 
        // Arrange
        var s = new OrderService();
        var negativeTotal = new BigDecimal(value);

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> s.create("student-1", negativeTotal));
    }
}
