package me.ebonjaeger.perworldinventory

import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

/**
 * This class is used to represent a Player.
 * It contains all of the variables that can be saved, as well as a few things
 * for internal use.
 *
 * @param player The player to grab data from
 */
data class PlayerInfo(private val player: Player,
                      private val group: Group,
                      val balance: Double,
                      private val useAttributes: Boolean)
{

    /* Inventories */
    val armor = player.inventory.armorContents
    val enderChest = player.enderChest.contents
    val inventory = player.inventory.contents

    /* Player Stats */
    val allowFlight = player.allowFlight
    val displayName = player.displayName
    val exhaustion = player.exhaustion
    val experience = player.exp
    val isFlying = player.isFlying
    val foodLevel = player.foodLevel
    val maxHealth = if (useAttributes) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue else player.maxHealth
    val health = player.health
    val gameMode = player.gameMode
    val level = player.level
    val saturation = player.saturation
    val potionEffects = player.activePotionEffects
    val fallDistance = player.fallDistance
    val fireTicks = player.fireTicks
    val maximumAir = player.maximumAir
    val remainingAir = player.remainingAir

    /* Other stuff */
    val location = player.location
    val uuid = player.uniqueId
    val name = player.name

    var saved = false
}
