package me.ebonjaeger.perworldinventory.configuration

import ch.jalu.configme.SettingsHolder
import ch.jalu.configme.SettingsManager
import ch.jalu.configme.migration.MigrationService
import ch.jalu.configme.resource.YamlFileResource

/**
 * Settings class for PWI settings.
 *
 * @param file The configuration file to load
 * @param migrater The configuration migrater to use to add new config options
 * @param settingsHolders Classes that hold the actual properties
 */
class Settings(file: YamlFileResource,
               migrater: MigrationService,
               vararg settingsHolders: Class<out SettingsHolder>) :
        SettingsManager(file, migrater, *settingsHolders)
{


}
