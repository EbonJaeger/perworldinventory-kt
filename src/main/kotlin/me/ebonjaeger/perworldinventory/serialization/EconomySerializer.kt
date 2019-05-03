package me.ebonjaeger.perworldinventory.serialization

import me.ebonjaeger.perworldinventory.data.PlayerProfile
import net.minidev.json.JSONObject
import org.bukkit.util.NumberConversions

object EconomySerializer
{

    /**
     * Serialize a player's money balance into a JsonObject.
     *
     * @param player The player's information
     * @return A JsonObject containing the balance
     */
    fun serialize(player: PlayerProfile): JSONObject
    {
        val obj = JSONObject()
        obj["balance"] = player.balance
        return obj
    }

    /**
     * Get a player's currency amount.
     *
     * @param data The JsonObject with the balance data
     */
    fun deserialize(data: JSONObject): Double
    {
        if (data.containsKey("balance"))
        {
            return NumberConversions.toDouble(data["balance"])
        }

        return 0.0
    }
}
