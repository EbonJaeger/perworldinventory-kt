package me.ebonjaeger.perworldinventory.serialization

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.PlayerInfo
import me.ebonjaeger.perworldinventory.Utils
import me.ebonjaeger.perworldinventory.configuration.PlayerSettings
import me.ebonjaeger.perworldinventory.configuration.PluginSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

class StatSerializer(private val config: Settings)
{

    /**
     * Serialize a player's stats into a [JsonObject].
     *
     * @param player The player whose stats to serialize
     * @return The serialized stats
     */
    fun serialize(player: PlayerInfo): JsonObject
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

    fun applyStats(player: Player, stats: JsonObject, format: Int)
    {
        val useAttributes = Utils.checkServerVersion(Bukkit.getServer().version, 1, 9, 0)
        val loadGameMode = config.getProperty(PlayerSettings.LOAD_GAMEMODE) &&
                !config.getProperty(PluginSettings.SEPERATE_GM_INVENTORIES)

        if (config.getProperty(PlayerSettings.LOAD_ALLOW_FLIGHT) && stats.has("can-fly"))
            player.allowFlight = stats["can-fly"].asBoolean
        if (config.getProperty(PlayerSettings.LOAD_DISPLAY_NAME) && stats.has("display-name"))
            player.displayName = stats["display-name"].asString
        if (config.getProperty(PlayerSettings.LOAD_EXHAUSTION) && stats.has("exhaustion"))
            player.exhaustion = stats["exhaustion"].asFloat
        if (config.getProperty(PlayerSettings.LOAD_EXP) && stats.has("exp"))
            player.exp = stats["exp"].asFloat
        if (config.getProperty(PlayerSettings.LOAD_FLYING) && stats.has("flying") && player.allowFlight)
            player.isFlying = stats["flying"].asBoolean
        if (config.getProperty(PlayerSettings.LOAD_HUNGER) && stats.has("food"))
            player.foodLevel = stats["food"].asInt
        if (config.getProperty(PlayerSettings.LOAD_HEALTH) &&
                stats.has("health") && stats.has("max-health"))
        {
            val maxHealth = stats["max-health"].asDouble
            val health = stats["health"].asDouble

            // Attributes aren't in older versions of Bukkit/Spigot
            if (useAttributes)
            {
                setHealth(player, maxHealth, health, true)
            } else
            {
                setHealth(player, maxHealth, health, false)
            }
        }
        if (loadGameMode && stats.has("gamemode"))
            setGameMode(player, stats)
        if (config.getProperty(PlayerSettings.LOAD_LEVEL) && stats.has("level"))
            player.level = stats["level"].asInt
        if (config.getProperty(PlayerSettings.LOAD_SATURATION) && stats.has("saturation"))
            player.saturation = stats["saturation"].asFloat
        if (config.getProperty(PlayerSettings.LOAD_FALL_DISTANCE) && stats.has("fallDistance"))
            player.fallDistance = stats["fallDistance"].asFloat
        if (config.getProperty(PlayerSettings.LOAD_FIRE_TICKS) && stats.has("fireTicks"))
            player.fireTicks = stats["fireTicks"].asInt
        if (config.getProperty(PlayerSettings.LOAD_MAX_AIR) && stats.has("maxAir"))
            player.maximumAir = stats["maxAir"].asInt
        if (config.getProperty(PlayerSettings.LOAD_REMAINING_AIR) && stats.has("remainingAir"))
            player.remainingAir = stats["remainingAir"].asInt
        if (config.getProperty(PlayerSettings.LOAD_POTION_EFFECTS) && stats.has("potion-effects"))
        {
            if (format >= 2)
            {
                setPotionEffects(player, stats["potion-effects"].asJsonArray)
            } else
            {
                throw IllegalArgumentException("Older format '$format' for potion effects is no longer supported")
            }
        }
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
