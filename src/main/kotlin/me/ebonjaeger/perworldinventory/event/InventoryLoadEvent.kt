package me.ebonjaeger.perworldinventory.event

import me.ebonjaeger.perworldinventory.Group
import me.ebonjaeger.perworldinventory.serialization.DeserializeCause
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event called when a player's new inventory is about to be
 * loaded. If the event is cancelled, the inventory will not
 * be loaded.
 */
class InventoryLoadEvent(val player: Player,
                         val cause: DeserializeCause,
                         val newGameMode: GameMode,
                         val group: Group) : Event(), Cancellable
{

    private val HANDLERS = HandlerList()

    private var isEventCancelled = false

    override fun getHandlers(): HandlerList
    {
        return HANDLERS
    }

    override fun isCancelled(): Boolean
    {
        return isEventCancelled
    }

    override fun setCancelled(cancelled: Boolean)
    {
        isEventCancelled = cancelled
    }
}
