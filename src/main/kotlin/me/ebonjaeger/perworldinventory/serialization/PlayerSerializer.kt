package me.ebonjaeger.perworldinventory.serialization

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.data.PlayerProfile
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
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
     *     3: Items are not stored encoded
     * </p>
     *
     * @param player The player profile to serialize
     * @return The serialized player's data in its entirety
     */
    fun serialize(player: PlayerProfile): JSONObject
    {
        ConsoleLogger.debug("[SERIALIZER] Serializing player '${player.displayName}'")
        val obj = JSONObject()

        obj["data-format"] = 3
        obj["ender-chest"] = InventorySerializer.serializeInventory(player.enderChest)
        obj["inventory"] = InventorySerializer.serializeAllInventories(player)
        obj["stats"] = StatSerializer.serialize(player)
        obj["economy"] = EconomySerializer.serialize(player)

        ConsoleLogger.debug("[SERIALIZER] Done serializing player '${player.displayName}'")
        return obj
    }

    fun deserialize(data: JSONObject, playerName: String, inventorySize: Int, eChestSize: Int): PlayerProfile
    {
        // Get the data format being used
        var format = 3
        if (data.containsKey("data-format"))
        {
            format = data["data-format"] as Int
        }

        val inventory = data["inventory"] as JSONObject
        val items = InventorySerializer.deserialize(inventory["inventory"] as JSONArray,
                inventorySize,
                format)
        val armor = InventorySerializer.deserialize(inventory["armor"] as JSONArray, 4, format)
        val enderChest = InventorySerializer.deserialize(data["ender-chest"] as JSONArray,
                eChestSize,
                format)
        val stats = StatSerializer.validateStats(data["stats"] as JSONObject, playerName)
        val potionEffects = PotionSerializer.deserialize(stats["potion-effects"] as JSONArray)
        val balance = if (data.containsKey("economy"))
        {
            EconomySerializer.deserialize(data["economy"] as JSONObject)
        } else
        {
            0.0
        }

        return PlayerProfile(armor,
                enderChest,
                items,
                stats["can-fly"] as Boolean,
                stats["display-name"] as String,
                stats.getAsNumber("exhaustion").toFloat(),
                stats.getAsNumber("exp").toFloat(),
                stats["flying"] as Boolean,
                stats["food"] as Int,
                stats["max-health"] as Double,
                stats["health"] as Double,
                GameMode.valueOf(stats["gamemode"] as String),
                stats["level"] as Int,
                stats.getAsNumber("saturation").toFloat(),
                potionEffects,
                stats.getAsNumber("fallDistance").toFloat(),
                stats["fireTicks"] as Int,
                stats["maxAir"] as Int,
                stats["remainingAir"] as Int,
                balance)
    }
}
