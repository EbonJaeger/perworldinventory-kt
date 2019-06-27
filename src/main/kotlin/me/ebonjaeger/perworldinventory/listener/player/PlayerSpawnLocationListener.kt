package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.configuration.PluginSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import me.ebonjaeger.perworldinventory.data.DataSource
import me.ebonjaeger.perworldinventory.data.ProfileManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.spigotmc.event.player.PlayerSpawnLocationEvent
import javax.inject.Inject


class PlayerSpawnLocationListener @Inject constructor(private val dataSource: DataSource,
                                                      private val groupManager: GroupManager,
                                                      private val settings: Settings,
                                                      private val profileManager: ProfileManager) : Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerSpawn(event: PlayerSpawnLocationEvent)
    {
        if (!settings.getProperty(PluginSettings.LOAD_DATA_ON_JOIN))
            return

        val player = event.player
        val spawnWorld = event.spawnLocation.world!!.name // The server will never provide a null world in a Location
        ConsoleLogger.fine("onPlayerSpawn: '${player.name}' joining in world '$spawnWorld'")

        val location = dataSource.getLogout(player)

        if (location == null || location.world == null) { // No valid location found
            return
        }

        ConsoleLogger.debug("onPlayerSpawn: Logout location found for player '${player.name}': $location")

        val world = location.world
        if (world!!.name != spawnWorld) { // We saved this Location, so we can assume it has a world
            val spawnGroup = groupManager.getGroupFromWorld(spawnWorld)
            val logoutGroup = groupManager.getGroupFromWorld(world.name)

            if (spawnGroup != logoutGroup) {
                ConsoleLogger.fine("onPlayerSpawn: Current group does not match logout group for '${player.name}'")
                ConsoleLogger.debug("onPlayerSpawn: spawnGroup=$spawnGroup, logoutGroup=$logoutGroup")

                profileManager.addPlayerProfile(player, logoutGroup, player.gameMode)
                profileManager.getPlayerData(player, spawnGroup, player.gameMode)
            }
        }
    }
}
