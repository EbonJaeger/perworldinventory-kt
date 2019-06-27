package me.ebonjaeger.perworldinventory.event

import me.ebonjaeger.perworldinventory.Group
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * The cause of this loading event.
 */
enum class Cause
{
    /**
     * The player changed worlds.
     */
    WORLD_CHANGE,

    /**
     * The player changed their GameMode.
     */
    GAMEMODE_CHANGE
}

/**
 * Event called when a player's new inventory is about to be
 * loaded. If the event is cancelled, the inventory will not
 * be loaded.
 *
 * @property player The [Player] that caused this event.
 * @property cause The [Cause] of the event.
 * @property oldGameMode The player's old [GameMode].
 * @property newGameMode The player's new [GameMode].
 * @property group The [Group] that the player is going to.
 */
class InventoryLoadEvent(val player: Player,
                         val cause: Cause,
                         val oldGameMode: GameMode,
                         val newGameMode: GameMode,
                         val group: Group) : Event(), Cancellable
{
    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList() = HANDLERS
    }

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
