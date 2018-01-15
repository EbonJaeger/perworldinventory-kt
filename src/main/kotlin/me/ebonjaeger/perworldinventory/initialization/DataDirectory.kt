package me.ebonjaeger.perworldinventory.initialization

/**
 * Annotation used to identify the data directory for injection. Not to confuse with
 * [PluginFolder], which Bukkit refers to as "data folder."
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class DataDirectory