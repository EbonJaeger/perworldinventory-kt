package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.configuration.PluginSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import me.ebonjaeger.perworldinventory.data.ProfileManager
import me.ebonjaeger.perworldinventory.event.Cause
import me.ebonjaeger.perworldinventory.event.InventoryLoadEvent
import me.ebonjaeger.perworldinventory.permission.PlayerPermission
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import javax.inject.Inject

class PlayerChangedWorldListener @Inject constructor(private val plugin: PerWorldInventory,
                                                     private val groupManager: GroupManager,
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
        val startingGameMode = player.gameMode

        ConsoleLogger.fine("onPlayerChangedWorld: ${player.name} changed worlds")

        // Check if the FROM group is configured
        if (!groupFrom.configured && settings.getProperty(PluginSettings.SHARE_IF_UNCONFIGURED))
        {
            ConsoleLogger.debug("onPlayerChangedWorld: FROM group (${groupFrom.name}) is not defined, and plugin configured to share inventory")

            return
        }

        // Check if the groups are actually the same group
        if (groupFrom == groupTo)
        {
            ConsoleLogger.debug("onPlayerChangedWorld: Both groups are the same: '$groupFrom'")
            return
        }

        // Check of the TO group is configured
        if (!groupTo.configured && settings.getProperty(PluginSettings.SHARE_IF_UNCONFIGURED))
        {
            ConsoleLogger.debug("onPlayerChangedWorld: FROM group (${groupTo.name}) is not defined, and plugin configured to share inventory")

            return
        }

        // Check if the player bypasses the changes
        if (!settings.getProperty(PluginSettings.DISABLE_BYPASS) &&
                player.hasPermission(PlayerPermission.BYPASS_WORLDS.getNode()))
        {
            ConsoleLogger.debug("onPlayerChangedWorld: Player '${player.name}' has bypass worlds permission")
            return
        }

        // Check if we manage GameModes. If we do, we can skip loading the data
        // for a mode they're only going to be in for half a second.
        if (settings.getProperty(PluginSettings.MANAGE_GAMEMODES) &&
                !player.hasPermission(PlayerPermission.BYPASS_ENFORCE_GAMEMODE.getNode()))
        {
            if (player.gameMode != groupTo.defaultGameMode)
            {
                ConsoleLogger.debug("onPlayerChangedWorld: We manage GameModes and the GameMode for this group is different from ${player.name}'s current GameMode")
                player.gameMode = groupTo.defaultGameMode

                // If GameMode inventories are separated, then the other listener will
                // handle it, so we do nothing more here. Else, we do still have to load
                // a new inventory, which shouldn't be a problem because if GameModes
                // aren't separated, the other listener will return early. Thus it should
                // be perfectly safe to load the data from here.

                if (settings.getProperty(PluginSettings.SEPARATE_GM_INVENTORIES))
                {
                    ConsoleLogger.debug("onPlayerChangedWorld: GameMode inventories are separated, so returning from here")
                    return
                }
            }
        }

        // All other checks are done, time to get the data
        ConsoleLogger.fine("onPlayerChangedWorld: Loading data for player '${player.name}' for group: $groupTo")

        // Add player to the timeouts to prevent item dupe
        if (plugin.updateTimeoutsTaskId != -1)
        {
            plugin.timeouts[player.uniqueId] = plugin.SLOT_TIMEOUT
        }

        val loadEvent = InventoryLoadEvent(player, Cause.WORLD_CHANGE,
                startingGameMode, player.gameMode, groupTo)
        Bukkit.getPluginManager().callEvent(loadEvent)
        if (!loadEvent.isCancelled)
        {
            profileManager.getPlayerData(player, groupTo, player.gameMode)
        }
    }
}
