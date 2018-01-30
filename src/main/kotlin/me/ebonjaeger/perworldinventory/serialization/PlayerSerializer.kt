package me.ebonjaeger.perworldinventory.serialization

import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.data.PlayerProfile
import org.bukkit.GameMode
import org.bukkit.Location

object PlayerSerializer
{

    /**
     * Serialize a [PlayerProfile] into a JsonObject. The player's EnderChest, inventory
     * (including armor) and stats such as experience and potion effects will
     * be saved unless disabled. A data format number is included to tell
     * which methods to use for some serializations/deserializations.
     *
     * <p>
     *     Formats:
     *     0: Deserialize items with the old TacoSerialization methods
     *     1: (De)serialize items with Base64
     *     2: Serialize/Deserialize PotionEffects as JsonObjects
     * </p>
     *
     * @param player The player profile to serialize
     * @return The serialized player's data in its entirety
     */
    fun serialize(player: PlayerProfile): JsonObject
    {
        ConsoleLogger.debug("[SERIALIZER] Serializing player '${player.displayName}'")
        val obj = JsonObject()

        obj.addProperty("data-format", 2)
        obj.add("ender-chest", InventorySerializer.serializeInventory(player.enderChest))
        obj.add("inventory", InventorySerializer.serializeAllInventories(player))
        obj.add("stats", StatSerializer.serialize(player))
        obj.add("economy", EconomySerializer.serialize(player))
        obj.add("location", LocationSerializer.serialize(player.location))

        ConsoleLogger.debug("[SERIALIZER] Done serializing player '${player.displayName}'")
        return obj
    }

    fun deserialize(data: JsonObject, spawnLoc: Location): PlayerProfile
    {
        // Get the data format being used
        var format = 2
        if (data.has("data-format"))
        {
            format = data["data-format"].asInt
        }

        // TODO: Do something other than hardcoded size number
        val inventory = InventorySerializer.deserialize(data["inventory"].asJsonArray,
                37, // 27 storage slots, 9 hotbar slots, and an off-hand slot
                format)
        val armor = InventorySerializer.deserialize(data["armor"].asJsonArray, 4, format)
        val enderChest = InventorySerializer.deserialize(data["ender-chest"].asJsonArray,
                27,
                format)
        val stats = data["stats"].asJsonObject
        val potionEffects = PotionSerializer.deserialize(stats["potion-effects"].asJsonArray)
        val balance = EconomySerializer.deserialize(data["economy"].asJsonObject)
        val location = if (data.has("location"))
        {
            LocationSerializer.deserialize(data["location"].asJsonObject)
        } else
        {
            spawnLoc
        }

        return PlayerProfile(armor,
                enderChest,
                inventory,
                stats["can-fly"].asBoolean,
                stats["display-name"].asString,
                stats["exhaustion"].asFloat,
                stats["exp"].asFloat,
                stats["flying"].asBoolean,
                stats["food"].asInt,
                stats["max-health"].asDouble,
                stats["health"].asDouble,
                GameMode.valueOf(stats["gamemode"].asString),
                stats["level"].asInt,
                stats["saturation"].asFloat,
                potionEffects,
                stats["fallDistance"].asFloat,
                stats["fireTicks"].asInt,
                stats["maxAir"].asInt,
                stats["remainingAir"].asInt,
                balance,
                location)
    }
}
