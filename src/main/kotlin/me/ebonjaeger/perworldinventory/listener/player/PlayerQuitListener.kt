package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.PerWorldInventory
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import javax.inject.Inject

class PlayerQuitListener @Inject constructor(private val plugin: PerWorldInventory) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        plugin.timeouts.remove(event.player.uniqueId)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerKick(event: PlayerKickEvent) {
        plugin.timeouts.remove(event.player.uniqueId)
    }
}
