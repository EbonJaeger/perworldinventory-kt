package me.ebonjaeger.perworldinventory.configuration

import ch.jalu.configme.migration.PlainMigrationService
import ch.jalu.configme.properties.Property
import ch.jalu.configme.resource.PropertyResource
import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.LogLevel

class PwiMigrationService : PlainMigrationService()
{

    override fun performMigrations(resource: PropertyResource?, properties: MutableList<Property<*>>?): Boolean
    {
        return migrateDebugLevels(resource!!)
    }

    /**
     * Migrate the old simple on-off debug mode to the new multi-leveled debug mode.
     *
     * @param resource The property resource
     * @return True if the configuration has changed, false otherwise
     */
    private fun migrateDebugLevels(resource: PropertyResource) : Boolean
    {
        val oldPath = "debug-mode"
        val newSetting = PluginSettings.LOGGING_LEVEL

        if (!newSetting!!.isPresent(resource) && resource.contains(oldPath))
        {
            val oldValue = resource.getBoolean(oldPath) ?: return false
            val level = if (oldValue) LogLevel.FINE else LogLevel.INFO

            resource.setValue(oldPath, null)
            resource.setValue(newSetting.path, level.name)
            return true
        }

        return false
    }

    /**
     * Checks for an old property path and moves it to a new path if it is present and the new path is not yet set.
     *
     * @param oldProperty The old property (create a temporary [Property] object with the path)
     * @param newProperty The new property to move the value to
     * @param resource The property resource
     * @param <T> The type of the property
     * @return True if a migration has been done, false otherwise
     */
    private fun <T> moveProperty(oldProperty: Property<T>,
                                 newProperty: Property<T>,
                                 resource: PropertyResource): Boolean
    {
        if (resource.contains(oldProperty.path))
        {
            if (resource.contains(newProperty.path))
            {
                ConsoleLogger.info("Detected deprecated property " + oldProperty.path)
            } else
            {
                ConsoleLogger.info("Renaming " + oldProperty.path + " to " + newProperty.path)
                resource.setValue(newProperty.path, oldProperty.getValue(resource))
            }

            return true
        }

        return false
    }
}
