package UC5Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PortalTest {

    static class Portal {

        private boolean activated = false;

        public void activate() {
            activated = true;
        }

        public boolean isActivated() {
            return activated;
        }
    }

    @Test
    void testPortalActivated() {

        Portal portal = new Portal();

        portal.activate();

        assertTrue(portal.isActivated());
    }
}