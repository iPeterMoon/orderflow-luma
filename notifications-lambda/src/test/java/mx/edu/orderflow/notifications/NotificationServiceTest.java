package mx.edu.orderflow.notifications;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationServiceTest {
    @Test 
    void rejectsNullOrderId() {
        var s = new NotificationService();
        assertThrows(IllegalArgumentException.class, () -> s.confirmation(null, "c-1"));
    }

    @Test 
    void rejectsBlankOrderId() {
        var s = new NotificationService();
        assertThrows(IllegalArgumentException.class, () -> s.confirmation("", "c-1"));
    }

    @Test
    void createsConfirmation() {
        var m = new NotificationService().confirmation("1001", "c-1");
        assertTrue(m.message().contains("1001"));
    }
}
