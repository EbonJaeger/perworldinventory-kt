package me.ebonjaeger.perworldinventory.data

import com.dumptruckman.bukkit.configuration.util.SerializationHelper
import me.ebonjaeger.perworldinventory.serialization.InventoryHelper
import me.ebonjaeger.perworldinventory.serialization.PotionSerializer
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.util.NumberConversions
import java.util.*

/**
 * This class is used to represent a Player.
 * It contains all of the variables that can be saved, as well as a few things
 * for internal use.
 */
data class PlayerProfile(val armor: Array<out ItemStack>,
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
                         val balance: Double) : ConfigurationSerializable {

    /**
     * Simple constructor to use when you have a [Player] object to work with.
     *
     * @param player The player to build this profile from
     * @param balance The amount of currency the player has
     */
    constructor(player: Player,
                balance: Double) : this(
            player.inventory.armorContents,
            player.enderChest.contents,
            player.inventory.contents,
            player.allowFlight,
            player.displayName,
            player.exhaustion,
            player.exp,
            player.isFlying,
            player.foodLevel,
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue, // If this is ever null, I will be very surprised
            player.health,
            player.gameMode,
            player.level,
            player.saturation,
            player.activePotionEffects,
            player.fallDistance,
            player.fireTicks,
            player.maximumAir,
            player.remainingAir,
            balance)

    companion object {

        /**
         * Deserialize a [Map] into a [PlayerProfile].
         *
         * @param map The map to deserialize
         * @return The profile from the given data
         * @see ConfigurationSerializable
         */
        @JvmStatic
        fun deserialize(map: Map<String, Any>): PlayerProfile {
            val inventory = map["inventory"] as Map<*, *>

            /* Inventory contents */
            val contentsList = inventory["contents"] as List<*>
            val contents = InventoryHelper.listToInventory(contentsList)

            /* Armor contents */
            val armorList = inventory["armor"] as List<*>
            val armor = InventoryHelper.listToInventory(armorList)

            /* Ender Chest contents */
            val enderChestList = map["ender-chest"] as List<*>
            val enderChest = InventoryHelper.listToInventory(enderChestList)

            /* Player stats */
            val stats = map["stats"] as MutableMap<*, *>

            /* Potion effects */
            val potionsList = map["potion-effects"] as List<*>
            val potions = mutableListOf<PotionEffect>()
            potionsList.forEach { pot -> potions.add(SerializationHelper.deserialize(pot as Map<*, *>) as PotionEffect) }

            /* Put it all together */
            return PlayerProfile(
                    armor,
                    enderChest,
                    contents,
                    stats["can-fly"] as Boolean,
                    stats["display-name"] as String,
                    stats["exhaustion"] as Float,
                    stats["exp"] as Float,
                    stats["flying"] as Boolean,
                    stats["food"] as Int,
                    NumberConversions.toDouble(stats["max-health"]),
                    NumberConversions.toDouble(stats["health"]),
                    GameMode.valueOf(stats["gamemode"] as String),
                    stats["level"] as Int,
                    stats["saturation"] as Float,
                    potions,
                    stats["fallDistance"] as Float,
                    stats["fireTicks"] as Int,
                    stats["maxAir"] as Int,
                    stats["remainingAir"] as Int,
                    NumberConversions.toDouble(map["balance"])
            )
        }
    }

    /**
     * Serialize a [PlayerProfile] to it's [Map] representation.
     *
     * @return A Map representing the profile's state
     * @see ConfigurationSerializable.serialize
     */
    override fun serialize(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()

        /* Player inventories */
        val contents = InventoryHelper.serializeInventory(this.inventory)
        val armor = InventoryHelper.serializeInventory(this.armor)
        val inventory = linkedMapOf(Pair("contents", contents), Pair("armor", armor))
        val enderChest = InventoryHelper.serializeInventory(this.enderChest)

        /* Player stats */
        val stats = linkedMapOf<String, Any>()
        stats["can-fly"] = this.allowFlight
        stats["display-name"] = this.displayName
        stats["exhaustion"] = this.exhaustion
        stats["exp"] = this.experience
        stats["flying"] = this.isFlying
        stats["food"] = this.foodLevel
        stats["gamemode"] = this.gameMode.toString()
        stats["max-health"] = this.maxHealth
        stats["health"] = this.health
        stats["level"] = this.level
        stats["saturation"] = this.saturation
        stats["fallDistance"] = this.fallDistance
        stats["fireTicks"] = this.fireTicks
        stats["maxAir"] = this.maximumAir
        stats["remainingAir"] = this.remainingAir

        val potionEffects = PotionSerializer.serialize(this.potionEffects)

        map["data-format"] = 4
        map["inventory"] = inventory
        map["ender-chest"] = enderChest
        map["stats"] = stats
        map["potion-effects"] = potionEffects
        map["balance"] = this.balance

        return map
    }

    override fun equals(other: Any?): Boolean {
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

    override fun hashCode(): Int {
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
