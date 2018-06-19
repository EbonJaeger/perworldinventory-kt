package me.ebonjaeger.perworldinventory.serialization

import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.data.PlayerDefaults
import me.ebonjaeger.perworldinventory.data.PlayerProfile
import org.bukkit.GameMode

object StatSerializer
{

    /**
     * Serialize a player's stats into a [JsonObject].
     *
     * @param player The player whose stats to serialize
     * @return The serialized stats
     */
    fun serialize(player: PlayerProfile): JsonObject
    {
        val obj = JsonObject()

        obj.addProperty("can-fly", player.allowFlight)
        obj.addProperty("display-name", player.displayName)
        obj.addProperty("exhaustion", player.exhaustion)
        obj.addProperty("exp", player.experience)
        obj.addProperty("flying", player.isFlying)
        obj.addProperty("food", player.foodLevel)
        obj.addProperty("gamemode", player.gameMode.toString())
        obj.addProperty("max-health", player.maxHealth)
        obj.addProperty("health", player.health)
        obj.addProperty("level", player.level)
        obj.addProperty("saturation", player.saturation)
        obj.addProperty("fallDistance", player.fallDistance)
        obj.addProperty("fireTicks", player.fireTicks)
        obj.addProperty("maxAir", player.maximumAir)
        obj.addProperty("remainingAir", player.remainingAir)
        obj.add("potion-effects", PotionSerializer.serialize(player.potionEffects))

        return obj
    }

    /**
     * Validate data by making sure that all stats are present.
     * If something is missing, add it to the object with sane
     * defaults, in most cases using the [PlayerDefaults] object.
     *
     * @param data The data to validate
     * @return The data with all stats present
     */
    fun validateStats(data: JsonObject): JsonObject
    {
        if (!data.has("can-fly")) data.addProperty("can-fly", false)
        if (!data.has("display-name")) data.addProperty("display-name", "")
        if (!data.has("exhaustion")) data.addProperty("exhaustion", PlayerDefaults.EXHAUSTION)
        if (!data.has("exp")) data.addProperty("exp", PlayerDefaults.EXPERIENCE)
        if (!data.has("flying")) data.addProperty("flying", false)
        if (!data.has("food")) data.addProperty("food", PlayerDefaults.FOOD_LEVEL)
        if (!data.has("gamemode")) data.addProperty("gamemode", GameMode.SURVIVAL.toString())
        if (!data.has("max-health")) data.addProperty("max-health", PlayerDefaults.HEALTH)
        if (!data.has("health")) data.addProperty("health", PlayerDefaults.HEALTH)
        if (!data.has("level")) data.addProperty("level", PlayerDefaults.LEVEL)
        if (!data.has("saturation")) data.addProperty("saturation", PlayerDefaults.SATURATION)
        if (!data.has("fallDistance")) data.addProperty("fallDistance", PlayerDefaults.FALL_DISTANCE)
        if (!data.has("fireTicks")) data.addProperty("fireTicks", PlayerDefaults.FIRE_TICKS)
        if (!data.has("maxAir")) data.addProperty("maxAir", PlayerDefaults.MAXIMUM_AIR)
        if (!data.has("remainingAir")) data.addProperty("remainingAir", PlayerDefaults.REMAINING_AIR)

        return data
    }
}
