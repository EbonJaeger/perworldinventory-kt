package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.data.ProfileManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import javax.inject.Inject

class PlayerTeleportListener @Inject constructor(private val groupManager: GroupManager,
                             private val profileManager: ProfileManager) : Listener
{

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerTeleport(event: PlayerTeleportEvent)
    {
        if (event.isCancelled || event.from.world == event.to.world)
        {
            return
        }

        val player = event.player
        val worldFromName = event.from.world.name
        val worldToName = event.to.world.name
        val groupFrom = groupManager.getGroupFromWorld(worldFromName)
        val groupTo = groupManager.getGroupFromWorld(worldToName)

        ConsoleLogger.fine("onPlayerTeleport: '${event.player.name}' going teleporting to another world")
        ConsoleLogger.debug("onPlayerTeleport: worldFrom='$worldFromName', worldTo='$worldToName'")

        if (groupFrom == groupTo)
        {
            return
        }

        profileManager.addPlayerProfile(player, groupFrom, player.gameMode)

        // TODO: Save the player's last location

        // Possibly prevents item duping exploit
        player.closeInventory()
    }
}
