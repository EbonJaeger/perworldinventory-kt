package me.ebonjaeger.perworldinventory.configuration

import ch.jalu.configme.SettingsHolder
import ch.jalu.configme.SettingsManagerImpl
import ch.jalu.configme.configurationdata.ConfigurationData
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder
import ch.jalu.configme.migration.MigrationService
import ch.jalu.configme.resource.YamlFileResource
import java.io.File

/**
 * Settings class for PWI settings.
 *
 * @param resource The property resource to read from and write to
 * @param migrater The configuration migrater to use to add new config options
 * @param settingsHolders Classes that hold the actual properties
 */
class Settings private constructor(resource: YamlFileResource,
                                   configurationData: ConfigurationData,
                                   migrater: MigrationService) :
        SettingsManagerImpl(resource, configurationData, migrater)
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
            val configurationData = ConfigurationDataBuilder.createConfiguration(*PROPERTY_HOLDERS)
            val migrater = PwiMigrationService()

            return Settings(fileResource, configurationData, migrater)
        }
    }
}
