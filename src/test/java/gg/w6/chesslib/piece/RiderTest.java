package gg.w6.chesslib.piece;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import gg.w6.chesslib.util.Color;
import gg.w6.chesslib.util.Offset;

class RiderTest {

    private static Set<Class<? extends Rider>> riderClasses;

    @BeforeAll
    static void discoverRiders() {
        final Reflections reflections = new Reflections("gg.w6.chesslib.piece");
        riderClasses = reflections.getSubTypesOf(Rider.class).stream()
                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                .collect(Collectors.toSet());

        assertFalse(riderClasses.isEmpty(), "No Rider subclasses found");
    }

    @Test
    void testOffsets() throws Exception {
        for (final Class<? extends Rider> cls : riderClasses) {
            final Rider rider = cls.getConstructor(Color.class)
                    .newInstance(Color.WHITE);
            final Offset[] offsets = rider.getOffsets();

            assertNotNull(offsets, cls.getSimpleName()
                    + " returned null offsets");
            assertTrue(offsets.length > 0, cls.getSimpleName()
                    + " returned empty offsets");
        }
    }

    @Test
    void testRange() throws Exception {
        for (final Class<? extends Rider> cls : riderClasses) {
            final Rider rider = cls.getConstructor(Color.class)
                    .newInstance(Color.WHITE);
            final int range = rider.getRange();

            assertTrue(range >= 1, cls.getSimpleName()
                    + " returned invalid range: " + range);
        }
    }
}
