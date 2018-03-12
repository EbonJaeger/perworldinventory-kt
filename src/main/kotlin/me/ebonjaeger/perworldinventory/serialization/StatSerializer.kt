package me.ebonjaeger.perworldinventory.serialization

import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.data.PlayerProfile

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
}
