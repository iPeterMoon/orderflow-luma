package mx.edu.orderflow.notifications;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class NotificationServiceTest {
    
    @ParameterizedTest
    @NullAndEmptySource
    void rejectsNullOrBlankOrderId(String orderId) {
        // Arrange
        var s = new NotificationService();

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> s.confirmation(orderId, "c-1"));
    }

    @Test
    void createsConfirmation() {
        // Arrange
        var s = new NotificationService();

        // Act
        var m = s.confirmation("1001", "c-1");

        // Assert
        assertTrue(m.message().contains("1001"));
    }
}
