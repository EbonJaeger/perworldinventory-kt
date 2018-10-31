package me.ebonjaeger.perworldinventory.configuration

import ch.jalu.configme.configurationdata.ConfigurationData
import ch.jalu.configme.migration.MigrationService
import ch.jalu.configme.migration.PlainMigrationService
import ch.jalu.configme.resource.PropertyReader
import me.ebonjaeger.perworldinventory.LogLevel

class PwiMigrationService : PlainMigrationService()
{

    override fun performMigrations(reader: PropertyReader, configurationData: ConfigurationData): Boolean
    {
        return migrateDebugLevels(reader, configurationData)
    }

    /**
     * Migrate the old simple on-off debug mode to the new multi-leveled debug mode.
     *
     * @param reader The property reader
     * @param configurationData The configuration data
     * @return True if the configuration has changed, false otherwise
     */
    private fun migrateDebugLevels(reader: PropertyReader, configurationData: ConfigurationData) : Boolean
    {
        val oldPath = "debug-mode"
        val newSetting = PluginSettings.LOGGING_LEVEL

        if (!newSetting!!.isPresent(reader) && reader.contains(oldPath))
        {
            val oldValue = reader.getBoolean(oldPath) ?: return false
            val level = if (oldValue) LogLevel.FINE else LogLevel.INFO

            configurationData.setValue(newSetting, level)
            return MigrationService.MIGRATION_REQUIRED
        }

        return MigrationService.NO_MIGRATION_NEEDED
    }
}
