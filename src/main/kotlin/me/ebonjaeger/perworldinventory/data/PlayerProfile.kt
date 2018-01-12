package me.ebonjaeger.perworldinventory.data

import me.ebonjaeger.perworldinventory.Group
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import java.util.*

/**
 * This class is used to represent a Player.
 * It contains all of the variables that can be saved, as well as a few things
 * for internal use.
 *
 * @param player The player to grab data from
 */
data class PlayerProfile(private val player: Player,
                         private val group: Group,
                         val balance: Double,
                         private val useAttributes: Boolean)
{

    /* Inventories */
    val armor: Array<out ItemStack> = player.inventory.armorContents
    val enderChest: Array<out ItemStack> = player.enderChest.contents
    val inventory: Array<out ItemStack> = player.inventory.contents

    /* Player Stats */
    val allowFlight = player.allowFlight
    val displayName: String = player.displayName
    val exhaustion = player.exhaustion
    val experience = player.exp
    val isFlying = player.isFlying
    val foodLevel = player.foodLevel
    val maxHealth = if (useAttributes) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue else player.maxHealth
    val health = player.health
    val gameMode: GameMode = player.gameMode
    val level = player.level
    val saturation = player.saturation
    val potionEffects: MutableCollection<PotionEffect> = player.activePotionEffects
    val fallDistance = player.fallDistance
    val fireTicks = player.fireTicks
    val maximumAir = player.maximumAir
    val remainingAir = player.remainingAir

    /* Other stuff */
    val location: Location = player.location
    val uuid: UUID = player.uniqueId
    val name: String = player.name

    var saved = false
}
