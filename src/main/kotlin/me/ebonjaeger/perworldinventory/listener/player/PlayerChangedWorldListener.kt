package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.configuration.PluginSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import me.ebonjaeger.perworldinventory.event.InventoryLoadEvent
import me.ebonjaeger.perworldinventory.serialization.DeserializeCause
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerChangedWorldListener(private val groupManager: GroupManager,
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
            ConsoleLogger.debug("[PROCESS] Both groups are the same: '${groupFrom.name}'")
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

        // Check if GameModes have separate inventories
        if (settings.getProperty(PluginSettings.SEPERATE_GM_INVENTORIES))
        {
            ConsoleLogger.debug("[PROCESS] GameModes are separated! " +
                    "Loading data for player '${player.name}' for group " +
                    "'${groupTo.name}' in GameMode '${player.gameMode.name}'")

            val loadEvent = InventoryLoadEvent(player, DeserializeCause.WORLD_CHANGE,
                    player.gameMode, groupTo)
            Bukkit.getPluginManager().callEvent(loadEvent)
            if (!loadEvent.isCancelled)
            {
                // TODO: Apply player data
            }
        } else
        {
            ConsoleLogger.debug("[PROCESS] Loading data for player " +
                    "'${player.name}' for group '${groupTo.name}'")

            val loadEvent = InventoryLoadEvent(player, DeserializeCause.WORLD_CHANGE,
                    GameMode.SURVIVAL, groupTo)
            Bukkit.getPluginManager().callEvent(loadEvent)
            if (!loadEvent.isCancelled)
            {
                // TODO: Apply player data
            }
        }
    }
}
