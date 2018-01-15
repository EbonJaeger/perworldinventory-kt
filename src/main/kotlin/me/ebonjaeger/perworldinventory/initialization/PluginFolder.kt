package me.ebonjaeger.perworldinventory.initialization

/**
 * Annotation used to identify the plugin's data folder for injection.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class PluginFolder