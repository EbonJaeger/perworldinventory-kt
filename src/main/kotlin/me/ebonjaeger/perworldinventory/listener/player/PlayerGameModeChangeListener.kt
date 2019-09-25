package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
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
import org.bukkit.event.player.PlayerGameModeChangeEvent
import javax.inject.Inject

class PlayerGameModeChangeListener @Inject constructor(private val groupManager: GroupManager,
                                                       private val profileManager: ProfileManager,
                                                       private val settings: Settings) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerChangedGameMode(event: PlayerGameModeChangeEvent) {
        if (event.isCancelled || !settings.getProperty(PluginSettings.SEPARATE_GM_INVENTORIES)) {
            return
        }

        val player = event.player
        val group = groupManager.getGroupFromWorld(player.world.name)

        ConsoleLogger.fine("onPlayerChangedGameMode: '${player.name}' changing GameModes")
        ConsoleLogger.debug("onPlayerChangedGameMode: newGameMode: ${event.newGameMode}, group: $group")

        // Save the current profile
        profileManager.addPlayerProfile(player, group, player.gameMode)

        // Check if the player can bypass the inventory switch
        if (!settings.getProperty(PluginSettings.DISABLE_BYPASS) &&
                player.hasPermission(PlayerPermission.BYPASS_GAMEMODE.getNode())) {
            ConsoleLogger.debug("onPlayerChangedGameMode: '${player.name}' is bypassing the inventory switch")
            return
        }

        // Create and call an InventoryLoadEvent. If it isn't cancelled, load
        // the player's new profile
        val loadEvent = InventoryLoadEvent(player, Cause.GAMEMODE_CHANGE, player.gameMode, event.newGameMode, group)
        Bukkit.getPluginManager().callEvent(loadEvent)
        if (!loadEvent.isCancelled) {
            ConsoleLogger.fine("onPlayerChangedGameMode: Loading profile for '${player.name}'")
            profileManager.getPlayerData(player, group, event.newGameMode)
        }
    }
}
