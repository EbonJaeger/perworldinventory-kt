package me.ebonjaeger.perworldinventory.serialization

import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.data.PlayerProfile
import org.bukkit.GameMode

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

        ConsoleLogger.debug("[SERIALIZER] Done serializing player '${player.displayName}'")
        return obj
    }

    fun deserialize(data: JsonObject, inventorySize: Int, eChestSize: Int): PlayerProfile
    {
        // Get the data format being used
        var format = 2
        if (data.has("data-format"))
        {
            format = data["data-format"].asInt
        }

        val inventory = data["inventory"].asJsonObject
        val items = InventorySerializer.deserialize(inventory["inventory"].asJsonArray,
                inventorySize,
                format)
        val armor = InventorySerializer.deserialize(inventory["armor"].asJsonArray, 4, format)
        val enderChest = InventorySerializer.deserialize(data["ender-chest"].asJsonArray,
                eChestSize,
                format)
        val stats = data["stats"].asJsonObject
        val potionEffects = PotionSerializer.deserialize(stats["potion-effects"].asJsonArray)
        val balance = if (data.has("economy"))
        {
            EconomySerializer.deserialize(data["economy"].asJsonObject)
        } else
        {
            0.0
        }

        return PlayerProfile(armor,
                enderChest,
                items,
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
                balance)
    }
}
