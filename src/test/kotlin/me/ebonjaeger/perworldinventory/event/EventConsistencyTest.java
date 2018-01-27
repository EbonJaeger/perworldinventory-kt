package me.ebonjaeger.perworldinventory.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests that events are set up to conform to Bukkit's requirements.
 * <p>
 * This test is implemented in Java as to ensure that required static members are really
 * visible as such inside Java.
 */
public class EventConsistencyTest {

    /**
     * Bukkit requires a static getHandlerList() method on all event classes, see {@link Event}.
     * This test checks that such a method is present and that it returns the same instance as the
     * method on the {@link Event} interface.
     */
    @Test
    public void shouldHaveStaticEventHandlerMethod() throws Exception {
        // given
        Reflections reflections = new Reflections("me.ebonjaeger.perworldinventory", new SubTypesScanner());
        Set<Class<? extends Event>> eventClasses = reflections.getSubTypesOf(Event.class);
        if (eventClasses.isEmpty()) {
            throw new IllegalStateException("Failed to collect any Event classes. Is the package correct?");
        }

        for (Class<? extends Event> clazz : eventClasses) {
            HandlerList staticHandlerList = getHandlerListFromStaticMethod(clazz);
            assertThat("Handler list should not be null for class " + clazz,
                staticHandlerList, not(nullValue()));
        }
    }

    private static HandlerList getHandlerListFromStaticMethod(Class<?> clz) throws Exception {
        Method staticHandlerListMethod = getStaticHandlerListMethod(clz);
        return (HandlerList) staticHandlerListMethod.invoke(null);
    }

    private static Method getStaticHandlerListMethod(Class<?> clz) throws NoSuchMethodException {
        Method method = null;
        try {
            method = clz.getMethod("getHandlerList");
        } catch (NoSuchMethodException e) {
            // swallow
        }

        if (method == null) {
            throw new IllegalStateException("No public getHandlerList() method on " + clz);
        } else if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException("Method getHandlerList() must be static on " + clz);
        }
        return method;
    }
}
