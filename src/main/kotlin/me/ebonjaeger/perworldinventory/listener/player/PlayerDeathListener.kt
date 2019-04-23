package me.ebonjaeger.perworldinventory.listener.player

import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.data.PlayerDefaults
import me.ebonjaeger.perworldinventory.data.ProfileManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import javax.inject.Inject

/**
 * Listens for [PlayerDeathEvent]s to prevent inventory
 * duplication when the player re-enters the world.
 */
class PlayerDeathListener @Inject constructor(private val groupManager: GroupManager,
                                              private val profileManager: ProfileManager) : Listener
{

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDeath(event: PlayerDeathEvent)
    {
        val player = event.entity
        val location = player.location
        val group = groupManager.getGroupFromWorld(location.world!!.name)

        if (!event.keepInventory)
        {
            player.inventory.clear()
        }

        if (!event.keepLevel)
        {
            player.totalExperience = event.newExp
            player.level = event.newLevel
        }

        player.foodLevel = PlayerDefaults.FOOD_LEVEL
        player.saturation = PlayerDefaults.SATURATION
        player.exhaustion = PlayerDefaults.EXHAUSTION
        player.fallDistance = PlayerDefaults.FALL_DISTANCE
        player.fireTicks = PlayerDefaults.FIRE_TICKS
        player.activePotionEffects.forEach { player.removePotionEffect(it.type) }

        profileManager.addPlayerProfile(player, group, player.gameMode)
    }
}
