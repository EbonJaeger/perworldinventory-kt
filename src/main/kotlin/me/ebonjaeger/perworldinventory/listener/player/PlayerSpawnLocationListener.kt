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
        val spawnWorld = event.spawnLocation.world.name
        ConsoleLogger.debug("Player '${player.name}' joining! Spawning in world '$spawnWorld'. Getting last logout location")

        val location = dataSource.getLogout(player)
        if (location != null)
        {
            ConsoleLogger.debug("Logout location found for player '${player.getName()}'!")

            if (location.world.name != spawnWorld)
            {
                val spawnGroup = groupManager.getGroupFromWorld(spawnWorld)
                val logoutGroup = groupManager.getGroupFromWorld(location.world.name)

                profileManager.addPlayerProfile(player, logoutGroup, player.gameMode)
                profileManager.getPlayerData(player, spawnGroup, player.gameMode)
            }
        }
    }
}
