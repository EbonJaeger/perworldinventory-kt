package me.ebonjaeger.perworldinventory.listener

import me.ebonjaeger.perworldinventory.TestHelper
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner

/**
 * Consistency test for [Listener] implementations.
 */
class ListenerConsistencyTest {

    @Test
    fun shouldOnlyHaveEventHandlerMethods() {
        // given
        val reflections = Reflections(TestHelper.PROJECT_PACKAGE, SubTypesScanner())
        val listeners = reflections.getSubTypesOf(Listener::class.java)
        if (listeners.isEmpty()) {
            throw IllegalStateException("Did not find any Listener implementations. Is the package correct?")
        }

        // when / then
        for (listener in listeners) {
            listener.methods
                .filter { it.declaringClass != Object::class.java }
                .filter { !it.isAnnotationPresent(EventHandler::class.java) }
                .forEach {
                    fail("Expected method " + it.declaringClass.simpleName + "#" + it.name
                        + " to be annotated with @EventHandler")
                }
        }
    }

}