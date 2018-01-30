package me.ebonjaeger.perworldinventory.data

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
 */
data class PlayerProfile constructor(val armor: Array<out ItemStack>,
                                     val enderChest: Array<out ItemStack>,
                                     val inventory: Array<out ItemStack>,
                                     val allowFlight: Boolean,
                                     val displayName: String,
                                     val exhaustion: Float,
                                     val experience: Float,
                                     val isFlying: Boolean,
                                     val foodLevel: Int,
                                     val maxHealth: Double,
                                     val health: Double,
                                     val gameMode: GameMode,
                                     val level: Int,
                                     val saturation: Float,
                                     val potionEffects: MutableCollection<PotionEffect>,
                                     val fallDistance: Float,
                                     val fireTicks: Int,
                                     val maximumAir: Int,
                                     val remainingAir: Int,
                                     val balance: Double,
                                     val location: Location)
{

    /**
     * Simple constructor to use when you have a [Player] object to work with.
     *
     * @param player The player to build this profile from
     * @param balance The amount of currency the player has
     * @param useAttributes If the [Attribute] class should be used for max health
     */
    constructor(player: Player,
                balance: Double,
                useAttributes: Boolean) : this(player.inventory.armorContents,
            player.enderChest.contents,
            player.inventory.contents,
            player.allowFlight,
            player.displayName,
            player.exhaustion,
            player.exp,
            player.isFlying,
            player.foodLevel,
            if (useAttributes) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue else player.maxHealth,
            player.health,
            player.gameMode,
            player.level,
            player.saturation,
            player.activePotionEffects,
            player.fallDistance,
            player.fireTicks,
            player.maximumAir,
            player.remainingAir,
            balance,
            player.location)

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (other !is PlayerProfile) return false

        if (!Arrays.equals(armor, other.armor)) return false
        if (!Arrays.equals(enderChest, other.enderChest)) return false
        if (!Arrays.equals(inventory, other.inventory)) return false
        if (allowFlight != other.allowFlight) return false
        if (displayName != other.displayName) return false
        if (exhaustion != other.exhaustion) return false
        if (experience != other.experience) return false
        if (isFlying != other.isFlying) return false
        if (foodLevel != other.foodLevel) return false
        if (maxHealth != other.maxHealth) return false
        if (health != other.health) return false
        if (gameMode != other.gameMode) return false
        if (level != other.level) return false
        if (saturation != other.saturation) return false
        if (potionEffects != other.potionEffects) return false
        if (fallDistance != other.fallDistance) return false
        if (fireTicks != other.fireTicks) return false
        if (maximumAir != other.maximumAir) return false
        if (remainingAir != other.remainingAir) return false
        if (balance != other.balance) return false

        return true
    }

    override fun hashCode(): Int
    {
        var result = Arrays.hashCode(armor)
        result = 31 * result + Arrays.hashCode(enderChest)
        result = 31 * result + Arrays.hashCode(inventory)
        result = 31 * result + allowFlight.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + exhaustion.hashCode()
        result = 31 * result + experience.hashCode()
        result = 31 * result + isFlying.hashCode()
        result = 31 * result + foodLevel
        result = 31 * result + maxHealth.hashCode()
        result = 31 * result + health.hashCode()
        result = 31 * result + gameMode.hashCode()
        result = 31 * result + level
        result = 31 * result + saturation.hashCode()
        result = 31 * result + potionEffects.hashCode()
        result = 31 * result + fallDistance.hashCode()
        result = 31 * result + fireTicks
        result = 31 * result + maximumAir
        result = 31 * result + remainingAir
        result = 31 * result + balance.hashCode()
        return result
    }
}
