package me.ebonjaeger.perworldinventory.serialization

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.data.PlayerProfile
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

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
     * Set a player's health and maximum health. If the server supports
     * [Attribute]s, use that to set the max health instead of the method in
     * the [Player] class.
     *
     * @param player The player to apply the values to
     * @param maxHealth The new maximum health value
     * @param health The new health value
     * @param useAttributes Use the attribute for a player's max health
     */
    private fun setHealth(player: Player, maxHealth: Double, health: Double, useAttributes: Boolean)
    {
        if (useAttributes)
        {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue = maxHealth
        } else
        {
            player.maxHealth = maxHealth
        }

        if (health <= maxHealth)
        {
            if (health <= 0)
            {
                player.health = maxHealth
            } else
            {
                player.health = health
            }
        } else
        {
            player.health = maxHealth
        }
    }

    /**
     * Set a player's [GameMode].
     *
     * @param player The player to set the GameMode of
     * @param stats A JsonObject with the new stats
     */
    private fun setGameMode(player: Player, stats: JsonObject)
    {
        if (stats["gamemode"].asString.length > 1)
        {
            player.gameMode = GameMode.valueOf(stats["gamemode"].asString)
        } else
        {
            val gm = stats["gamemode"].asInt
            when (gm)
            {
                0 -> player.gameMode = GameMode.CREATIVE
                1 -> player.gameMode = GameMode.SURVIVAL
                2 -> player.gameMode = GameMode.ADVENTURE
                3 -> player.gameMode = GameMode.SPECTATOR
            }
        }
    }

    /**
     * Apply potion effects in a JsonArray to a player.
     *
     * @param player The player to apply the effects to
     * @param json The potion effects
     */
    private fun setPotionEffects(player: Player, json: JsonArray)
    {
        // Remove any current effects
        if (player.activePotionEffects != null)
        {
            player.activePotionEffects.forEach { player.removePotionEffect(it.type) }
        }

        // Deserialize effects and apply to the player
        val effects = PotionSerializer.deserialize(json)
        player.addPotionEffects(effects)
    }
}
