package me.ebonjaeger.perworldinventory.initialization

import javax.inject.Provider
import kotlin.reflect.KClass

/**
 * Kotlin wrapper for [ch.jalu.injector.Injector].
 */
class Injector(private val jaluInjector: ch.jalu.injector.Injector) {

    /**
     * Registers an object as the singleton of the given class. Throws an exception if a singleton is already
     * available for the class.
     *
     * @param clazz the class to register the object for
     * @param singleton the object
     * @param T the type to register the object for
     */
    fun <T: Any> register(clazz: KClass<T>, singleton: T) {
        jaluInjector.register(clazz.java, singleton)
    }

    /**
     * Registers a provider for the given class. The provider is used whenever the class needs to be instantiated.
     *
     * @param clazz the class to register the provider for
     * @param provider the provider
     * @param T the class' type
     */
    fun <T: Any> registerProvider(clazz: KClass<in T>, provider: Provider<T>) {
        jaluInjector.registerProvider(clazz.java, provider)
    }

    /**
     * Registers the provider class to instantiate a given class. The first time the {@code clazz} has to
     * be instantiated, the {@code providerClass} will be instantiated.
     *
     * @param clazz the class to register the provider for
     * @param providerClass the class of the provider
     * @param T the class' type
     * @param P the provider's type
     */
    fun <T: Any, P: Provider<out T>> registerProvider(clazz: KClass<T>, providerClass: KClass<P>) {
        jaluInjector.registerProvider(clazz.java, providerClass.java)
    }

    /**
     * Processes an annotation with an associated object. The actual behavior of this method depends on the
     * configured handlers of the injector. By default it register the given object for the annotation such
     * that it may be later injected with the annotation as identifier.
     *
     * @param annotation the annotation
     * @param value the object
     */
    fun provide(annotation: KClass<out Annotation>, value: Any) {
        jaluInjector.provide(annotation.java, value)
    }

    /**
     * Retrieves or instantiates an object of the given type (singleton scope).
     *
     * @param clazz the class to retrieve the value for
     * @param T the class' type
     * @return object of the class' type
     */
    fun <T: Any> getSingleton(clazz: KClass<T>): T {
        return jaluInjector.getSingleton(clazz.java)
    }

    /**
     * Request-scoped method to instantiate a new object of the given class. The injector does <i>not</i> keep track
     * of it afterwards; it will always return a new instance and forget about it.
     *
     * @param clazz the class to instantiate
     * @param T the class' type
     * @return new instance of class T
     */
    fun <T: Any> newInstance(clazz: KClass<T>): T {
        return jaluInjector.newInstance(clazz.java)
    }

    /**
     * Returns the singleton of the given class if available. This simply returns the instance if present, and
     * otherwise {@code null}. Calling this method will never create any new objects.
     *
     * @param clazz the class to retrieve the instance for
     * @param T the class' type
     * @return instance or null if not available
     */
    fun <T: Any> getIfAvailable(clazz: KClass<T>): T? {
        return jaluInjector.getIfAvailable(clazz.java)
    }

    /**
     * Creates an instance of the given class if all of its dependencies are available. A new instance
     * is returned each time and the created object is not stored in the injector.
     * <p>
     * <b>Note:</b> Currently, all dependencies of the class need to be registered singletons for a new
     * instance to be created. This limitation may be lifted in future versions.
     *
     * @param clazz the class to construct if possible
     * @param T the class' type
     * @return instance of the class, or {@code null} if any dependency does not already exist
     */
    fun <T: Any> createIfHasDependencies(clazz: KClass<T>): T? {
        return jaluInjector.createIfHasDependencies(clazz.java)
    }

    /**
     * Returns all known singletons of the given type. Typically used
     * with interfaces in order to perform an action without knowing its concrete implementors.
     * Trivially, using {@link Object} as {@code clazz} will return all known singletons.
     *
     * @param clazz the class to retrieve singletons of
     * @param T the class' type
     * @return list of singletons of the given type
     */
    fun <T: Any> retrieveAllOfType(clazz: KClass<T>): MutableCollection<T> {
        return jaluInjector.retrieveAllOfType(clazz.java)
    }
}