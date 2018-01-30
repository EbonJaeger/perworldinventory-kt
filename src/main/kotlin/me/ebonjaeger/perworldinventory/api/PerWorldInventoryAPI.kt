package me.ebonjaeger.perworldinventory.api

import me.ebonjaeger.perworldinventory.Group
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.configuration.PluginSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import me.ebonjaeger.perworldinventory.data.ProfileManager
import javax.inject.Inject

/**
 * This class is for other plugins to interact with some parts of PWI.
 */
class PerWorldInventoryAPI @Inject constructor(private val plugin: PerWorldInventory,
                                               private val groupManager: GroupManager,
                                               private val profileManager: ProfileManager,
                                               private val settings: Settings)
{

    /**
     * Check if two worlds are a part of the same [Group] and thus can share
     * inventories. If one of the groups is not configured in the worlds.json,
     * this method will return true if the option for sharing inventories
     * between non-configured worlds is true in the config.yml file.
     *
     * @param first The name of the first world
     * @param second The name of the second world
     * @return True if both worlds are in the same group, or one group is not
     * configured and the option to share is set to true.
     */
    fun canWorldsShare(first: String, second: String): Boolean
    {
        val firstGroup = groupManager.getGroupFromWorld(first)
        val secondGroup = groupManager.getGroupFromWorld(second)

        return if (!firstGroup.configured || !secondGroup.configured)
        {
            firstGroup.containsWorld(second) || settings.getProperty(PluginSettings.SHARE_IF_UNCONFIGURED)
        } else
        {
            firstGroup.containsWorld(second)
        }
    }

    /**
     * Get a [Group] by name. If no group with that name exists, the method
     * will return null. See [GroupManager.getGroup]
     *
     * @param name The name of the group
     * @return The group if it exists, or null
     */
    fun getGroup(name: String) =
            groupManager.getGroup(name)

    /**
     * Get the [Group] that a given world is in. This method will always return
     * a group, even if a new one has to be created.
     *
     * See [GroupManager.getGroupFromWorld]
     *
     * @param worldName The name of the world
     * @return The group the world is in
     */
    fun getGroupFromWorld(worldName: String) =
            groupManager.getGroupFromWorld(worldName)
}
