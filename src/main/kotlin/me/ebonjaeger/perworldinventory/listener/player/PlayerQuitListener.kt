package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.data.DataSource
import me.ebonjaeger.perworldinventory.data.ProfileManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import javax.inject.Inject

class PlayerQuitListener @Inject constructor(private val plugin: PerWorldInventory,
                                             private val dataSource: DataSource,
                                             private val groupManager: GroupManager,
                                             private val profileManager: ProfileManager) : Listener
{

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent)
    {
        plugin.timeouts.remove(event.player.uniqueId)

        val player = event.player
        val group = groupManager.getGroupFromWorld(player.location.world!!.name) // The server will never provide a null world in a Location

        dataSource.saveLogout(player)
        profileManager.addPlayerProfile(player, group, player.gameMode)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerKick(event: PlayerKickEvent)
    {
        plugin.timeouts.remove(event.player.uniqueId)

        val player = event.player
        val group = groupManager.getGroupFromWorld(player.location.world!!.name) // The server will never provide a null world in a Location

        dataSource.saveLogout(player)
        profileManager.addPlayerProfile(player, group, player.gameMode)
    }
}
