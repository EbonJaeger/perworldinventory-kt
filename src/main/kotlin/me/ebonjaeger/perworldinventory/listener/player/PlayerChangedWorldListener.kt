package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.configuration.PluginSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import me.ebonjaeger.perworldinventory.data.ProfileManager
import me.ebonjaeger.perworldinventory.event.InventoryLoadEvent
import me.ebonjaeger.perworldinventory.serialization.DeserializeCause
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import javax.inject.Inject

class PlayerChangedWorldListener @Inject constructor(private val groupManager: GroupManager,
                                 private val profileManager: ProfileManager,
                                 private val settings: Settings) : Listener
{

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent)
    {
        val player = event.player
        val worldFrom = event.from
        val worldTo = player.world
        val groupFrom = groupManager.getGroupFromWorld(worldFrom.name)
        val groupTo = groupManager.getGroupFromWorld(worldTo.name)

        // Check if the FROM group is configured
        if (!groupFrom.configured && settings.getProperty(PluginSettings.SHARE_IF_UNCONFIGURED))
        {
            ConsoleLogger.debug(
                    "[PROCESS] FROM group (${groupFrom.name}) is not defined, and plugin configured to share inventory")

            return
        }

        // Check if the groups are actually the same group
        if (groupFrom == groupTo)
        {
            ConsoleLogger.debug("[PROCESS] Both groups are the same: '$groupFrom'")
            return
        }

        // Check of the TO group is configured
        if (!groupTo.configured && settings.getProperty(PluginSettings.SHARE_IF_UNCONFIGURED))
        {
            ConsoleLogger.debug(
                    "[PROCESS] FROM group (${groupTo.name}) is not defined, and plugin configured to share inventory")

            return
        }

        // Check if the player bypasses the changes
        if (!settings.getProperty(PluginSettings.DISABLE_BYPASS)
                // TODO: Check player permission for bypass
        )
        {
            return
        }

        // All other checks are done, time to get the data
        ConsoleLogger.debug("[PROCESS] Loading data for player " +
                "'${player.name}' for group: $groupTo")

        val loadEvent = InventoryLoadEvent(player, DeserializeCause.WORLD_CHANGE,
                player.gameMode, groupTo)
        Bukkit.getPluginManager().callEvent(loadEvent)
        if (!loadEvent.isCancelled)
        {
            profileManager.getPlayerData(player, groupTo, player.gameMode)
        }
    }
}
