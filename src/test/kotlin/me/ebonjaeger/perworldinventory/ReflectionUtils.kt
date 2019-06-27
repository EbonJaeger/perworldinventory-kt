package me.ebonjaeger.perworldinventory

import java.lang.String.format
import java.lang.reflect.Field
import kotlin.reflect.KClass

object ReflectionUtils
{

    /**
     * Set the field of a given object to a new value with reflection.
     *
     * @param clazz The class of the object.
     * @param instance The instance to modify (null for static fields).
     * @param fieldName The name of the field to modify.
     * @param value The value to give the field.
     */
    fun <T: Any> setField(clazz: KClass<T>, instance: T?, fieldName: String, value: Any)
    {
        try
        {
            val field = getField(clazz, instance, fieldName)
            field.set(instance, value)
        } catch (ex: UnsupportedOperationException)
        {
            throw UnsupportedOperationException(format("Could not set field '%s' for instance '%s' of class '%s'.",
                    fieldName, instance, clazz.simpleName), ex)
        }
    }

    private fun <T: Any> getField(clazz: KClass<T>, instance: T?, fieldName: String): Field
    {
        try
        {
            val field = clazz.java.getDeclaredField(fieldName)
            field.isAccessible = true
            return field
        } catch (ex: NoSuchFieldException)
        {
            throw UnsupportedOperationException(format("Could not get field '%s' for instance '%s' of class '%s'.",
                    fieldName, instance, clazz.simpleName), ex)
        }
    }
}
