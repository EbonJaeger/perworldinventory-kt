package me.ebonjaeger.perworldinventory.listener.entity

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPortalEvent
import javax.inject.Inject

class EntityPortalEventListener @Inject constructor(private val groupManager: GroupManager) : Listener
{

    @EventHandler (priority = EventPriority.NORMAL)
    fun onEntityPortalTeleport(event: EntityPortalEvent)
    {
        if (event.entity !is Item)
            return

        ConsoleLogger.fine("[EntityPortalEvent] A '${event.entity.name}' is going through a portal")

        val worldFrom = event.from.world
        val locationTo = event.to ?: return // A destination location is not guaranteed to exist
        val worldTo = locationTo.world

        if (worldFrom == null || worldTo == null) {
            ConsoleLogger.fine("[EntityPortalEvent] One of the worlds was null, returning")
            return
        }

        val from = groupManager.getGroupFromWorld(worldFrom.name)
        val to = groupManager.getGroupFromWorld(worldTo.name)

        // If the groups are different, cancel the event
        if (from != to)
        {
            ConsoleLogger.debug("[EntityPortalEvent] Group '${from.name}' and group '${to.name}' are different! Canceling event!")
            event.isCancelled = true
        }
    }
}
