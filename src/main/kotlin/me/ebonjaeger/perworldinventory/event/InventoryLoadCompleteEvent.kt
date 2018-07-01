package me.ebonjaeger.perworldinventory.event

import me.ebonjaeger.perworldinventory.Group
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event fired after inventory data has been applied to a player.
 * If the plugin is not configured to separate inventories by GameMode,
 * then [GameMode.SURVIVAL] will be returned by this event.
 *
 * @property player The [Player] that was effected.
 * @property group The [Group] the data was loaded for.
 * @property gameMode The [GameMode] the used to find the data.
 */
class InventoryLoadCompleteEvent(val player: Player,
                                 val group: Group,
                                 val gameMode: GameMode) : Event()
{

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList() = HANDLERS
    }

    override fun getHandlers(): HandlerList
    {
        return InventoryLoadCompleteEvent.HANDLERS
    }
}
