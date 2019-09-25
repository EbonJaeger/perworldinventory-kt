package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.configuration.PluginSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import me.ebonjaeger.perworldinventory.data.DataSource
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import javax.inject.Inject

class PlayerQuitListener @Inject constructor(private val plugin: PerWorldInventory,
                                             private val dataSource: DataSource,
                                             private val settings: Settings) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        plugin.timeouts.remove(event.player.uniqueId)
        if (settings.getProperty(PluginSettings.LOAD_DATA_ON_JOIN)) {
            dataSource.saveLogout(event.player)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerKick(event: PlayerKickEvent) {
        plugin.timeouts.remove(event.player.uniqueId)
        if (settings.getProperty(PluginSettings.LOAD_DATA_ON_JOIN)) {
            dataSource.saveLogout(event.player)
        }
    }
}
