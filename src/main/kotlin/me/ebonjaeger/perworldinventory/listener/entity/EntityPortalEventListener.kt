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

        ConsoleLogger.debug("[ENTITYPORTALEVENT] A '${event.entity.name}' is going through a portal!")

        val worldFrom = event.from.world.name

        if (event.to == null || event.to.world == null)
        {
            ConsoleLogger.debug("[ENTITYPORTALEVENT] event.getTo().getWorld().getName() would throw a NPE! Exiting method!")
            return
        }
        val worldTo = event.to.world.name
        val from = groupManager.getGroupFromWorld(worldFrom)
        val to = groupManager.getGroupFromWorld(worldTo)

        // If the groups are different, cancel the event
        if (from != to)
        {
            ConsoleLogger.debug("[ENTITYPORTALEVENT] Group '${from.name}' and group '${to.name}' are different! Canceling event!")
            event.isCancelled = true
        }
    }
}
