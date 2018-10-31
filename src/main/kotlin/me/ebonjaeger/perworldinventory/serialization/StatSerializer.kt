package me.ebonjaeger.perworldinventory.serialization

import me.ebonjaeger.perworldinventory.data.PlayerDefaults
import me.ebonjaeger.perworldinventory.data.PlayerProfile
import net.minidev.json.JSONObject
import org.bukkit.GameMode

object StatSerializer
{

    /**
     * Serialize a player's stats into a [JSONObject].
     *
     * @param player The player whose stats to serialize
     * @return The serialized stats
     */
    fun serialize(player: PlayerProfile): JSONObject
    {
        val obj = JSONObject()

        obj["can-fly"] = player.allowFlight
        obj["display-name"] = player.displayName
        obj["exhaustion"] = player.exhaustion
        obj["exp"] = player.experience
        obj["flying"] = player.isFlying
        obj["food"] = player.foodLevel
        obj["gamemode"] = player.gameMode.toString()
        obj["max-health"] = player.maxHealth
        obj["health"] = player.health
        obj["level"] = player.level
        obj["saturation"] = player.saturation
        obj["fallDistance"] = player.fallDistance
        obj["fireTicks"] = player.fireTicks
        obj["maxAir"] = player.maximumAir
        obj["remainingAir"] = player.remainingAir
        obj["potion-effects"] = PotionSerializer.serialize(player.potionEffects)

        return obj
    }

    /**
     * Validate data by making sure that all stats are present.
     * If something is missing, add it to the object with sane
     * defaults, in most cases using the [PlayerDefaults] object.
     *
     * @param data The data to validate
     * @param playerName The name of the player. Necessary in case
     * display name is not stored in the data
     * @return The data with all stats present
     */
    fun validateStats(data: JSONObject, playerName: String): JSONObject
    {
        if (!data.containsKey("can-fly")) data["can-fly"] = false
        if (!data.containsKey("display-name")) data["display-name"] = playerName
        if (!data.containsKey("exhaustion")) data["exhaustion"] = PlayerDefaults.EXHAUSTION
        if (!data.containsKey("exp")) data["exp"] = PlayerDefaults.EXPERIENCE
        if (!data.containsKey("flying")) data["flying"] = false
        if (!data.containsKey("food")) data["food"] = PlayerDefaults.FOOD_LEVEL
        if (!data.containsKey("gamemode")) data["gamemode"] = GameMode.SURVIVAL.toString()
        if (!data.containsKey("max-health")) data["max-health"] = PlayerDefaults.HEALTH
        if (!data.containsKey("health")) data["health"] = PlayerDefaults.HEALTH
        if (!data.containsKey("level")) data["level"] = PlayerDefaults.LEVEL
        if (!data.containsKey("saturation")) data["saturation"] = PlayerDefaults.SATURATION
        if (!data.containsKey("fallDistance")) data["fallDistance"] = PlayerDefaults.FALL_DISTANCE
        if (!data.containsKey("fireTicks")) data["fireTicks"] = PlayerDefaults.FIRE_TICKS
        if (!data.containsKey("maxAir")) data["maxAir"] = PlayerDefaults.MAXIMUM_AIR
        if (!data.containsKey("remainingAir")) data["remainingAir"] = PlayerDefaults.REMAINING_AIR

        return data
    }
}
