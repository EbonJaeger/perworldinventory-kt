package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.configuration.PluginSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent
import javax.inject.Inject

/**
 * Listener for [PlayerRespawnEvent].
 */
class PlayerRespawnListener @Inject constructor(private val groupManager: GroupManager,
                                                private val settings: Settings) : Listener
{

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        // Do nothing if managing respawns is disabled in the config
        if (!settings.getProperty(PluginSettings.MANAGE_DEATH_RESPAWN)) {
            return
        }

        val group = groupManager.getGroupFromWorld(event.player.location.world!!.name) // The server will never provide a null world in a Location

        // Check for a bed location in the group
        val bedLocation = event.player.bedSpawnLocation
        if (bedLocation != null) {
            // Set the spawn location to the bed and return if it's in the same group
            if (group.containsWorld(bedLocation.world!!.name)) {
                event.respawnLocation = bedLocation
                return
            }
        }

        // Set the respawn location to the world set in the config
        val respawnWorld = group.respawnWorld
        if (respawnWorld != null && group.containsWorld(respawnWorld)) {
            val world = Bukkit.getWorld(respawnWorld)

            if (world == null) {
                ConsoleLogger.warning("Unable to set respawn location: World '$respawnWorld' doesn't exist!")
                return
            }

            event.respawnLocation = world.spawnLocation
        }
    }
}
