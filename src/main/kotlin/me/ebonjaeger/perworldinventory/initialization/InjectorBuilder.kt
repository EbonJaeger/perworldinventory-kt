package me.ebonjaeger.perworldinventory.initialization

/**
 * Kotlin wrapper for [ch.jalu.injector.InjectorBuilder], creating a Kotlin-type [Injector].
 */
class InjectorBuilder {

    val jaluInjectorBuilder: ch.jalu.injector.InjectorBuilder = ch.jalu.injector.InjectorBuilder()

    fun addDefaultHandlers(rootPackage: String): InjectorBuilder {
        jaluInjectorBuilder.addDefaultHandlers(rootPackage)
        return this
    }

    fun create(): Injector {
        return Injector(jaluInjectorBuilder.create())
    }
}