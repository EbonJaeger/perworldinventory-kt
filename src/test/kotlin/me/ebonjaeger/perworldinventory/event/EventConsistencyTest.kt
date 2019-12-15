package me.ebonjaeger.perworldinventory.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.junit.jupiter.api.Test
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.test.assertNotNull

/**
 * Tests that events are set up to conform to Bukkit's requirements.
 *
 *
 * This test is implemented in Java as to ensure that required static members are really
 * visible as such inside Java.
 */
class EventConsistencyTest {

    /**
     * Bukkit requires a static getHandlerList() method on all event classes, see [Event].
     * This test checks that such a method is present and that it returns the same instance as the
     * method on the [Event] interface.
     */
    @Test
    @Throws(Exception::class)
    fun shouldHaveStaticEventHandlerMethod() {
        // given
        val reflections = Reflections("me.ebonjaeger.perworldinventory", SubTypesScanner())
        val eventClasses = reflections.getSubTypesOf(Event::class.java)

        check(eventClasses.isNotEmpty()) { "Failed to collect any Event classes. Is the package correct?" }

        for (clazz in eventClasses) {
            val staticHandlerList = getHandlerListFromStaticMethod(clazz)
            assertNotNull(staticHandlerList, "Handler list should not be null for class $clazz")
        }
    }

    companion object {

        @Throws(Exception::class)
        private fun getHandlerListFromStaticMethod(clz: Class<*>): HandlerList {
            val staticHandlerListMethod = getStaticHandlerListMethod(clz)
            return staticHandlerListMethod.invoke(null) as HandlerList
        }

        private fun getStaticHandlerListMethod(clz: Class<*>): Method {
            var method: Method? = null
            try {
                method = clz.getMethod("getHandlerList")
            } catch (e: NoSuchMethodException) { // swallow
            }

            checkNotNull(method) { "No public getHandlerList() method on $clz" }
            check(Modifier.isStatic(method.modifiers)) { "Method getHandlerList() must be static on $clz" }

            return method
        }
    }
}
