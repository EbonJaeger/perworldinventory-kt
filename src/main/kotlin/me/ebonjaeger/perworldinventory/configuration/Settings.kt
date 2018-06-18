package me.ebonjaeger.perworldinventory.configuration

import ch.jalu.configme.SettingsHolder
import ch.jalu.configme.SettingsManager
import ch.jalu.configme.migration.MigrationService
import ch.jalu.configme.resource.YamlFileResource
import java.io.File

/**
 * Settings class for PWI settings.
 *
 * @param file The configuration file to load
 * @param migrater The configuration migrater to use to add new config options
 * @param settingsHolders Classes that hold the actual properties
 */
class Settings private constructor(file: YamlFileResource,
                                   migrater: MigrationService,
                                   vararg settingsHolders: Class<out SettingsHolder>) :
        SettingsManager(file, migrater, *settingsHolders)
{

    companion object {

        /** All [SettingsHolder] classes of PerWorldInventory. */
        private val PROPERTY_HOLDERS = arrayOf(
                PluginSettings::class.java,
                MetricsSettings::class.java,
                PlayerSettings::class.java)

        /**
         * Creates a [Settings] instance, using the given file as config file.
         *
         * @param file the config file to load
         * @return settings instance for the file
         */
        fun create(file: File): Settings {
            val fileResource = YamlFileResource(file)
            val migrater = PwiMigrationService()

            return Settings(fileResource, migrater, *PROPERTY_HOLDERS)
        }
    }
}
