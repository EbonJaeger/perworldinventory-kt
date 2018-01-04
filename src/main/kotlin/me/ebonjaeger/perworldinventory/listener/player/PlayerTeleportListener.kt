package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class PlayerTeleportListener(private val groupManager: GroupManager) : Listener
{

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerTeleport(event: PlayerTeleportEvent)
    {
        if (event.isCancelled ||
                event.from.world == event.to.world)
        {
            return
        }

        val player = event.player
        val worldFromName = event.from.world.name
        val worldToName = event.to.world.name
        val groupFrom = groupManager.getGroupFromWorld(worldFromName)
        val groupTo = groupManager.getGroupFromWorld(worldToName)

        ConsoleLogger.debug(
                "[EVENTS] Player '${event.player.name}' going from world '$worldFromName' to world '$worldToName'")

        if (groupFrom == groupTo)
        {
            return
        }

        // TODO: Add the player's information to the cache

        // TODO: Save the player's last location

        // Possibly prevents item duping exploit
        player.closeInventory()
    }
}
