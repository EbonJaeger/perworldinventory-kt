package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.PerWorldInventory
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCreativeEvent
import javax.inject.Inject

class InventoryCreativeListener @Inject constructor(private val plugin: PerWorldInventory) : Listener
{

    @EventHandler(priority = EventPriority.LOWEST)
    fun onCreativeSlotChange(event: InventoryCreativeEvent)
    {
        if (!plugin.timeouts.isEmpty())
        {
            return
        }

        val holder = event.inventory.holder
        if (holder is Player && plugin.timeouts.containsKey(holder.uniqueId))
        {
            event.result = Event.Result.DENY
        }
    }
}
