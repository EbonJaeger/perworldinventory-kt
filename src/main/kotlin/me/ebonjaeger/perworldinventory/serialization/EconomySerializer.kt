package me.ebonjaeger.perworldinventory.serialization

import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.data.PlayerProfile

object EconomySerializer
{

    /**
     * Serialize a player's money balance into a JsonObject.
     *
     * @param player The player's information
     * @return A JsonObject containing the balance
     */
    fun serialize(player: PlayerProfile): JsonObject
    {
        val obj = JsonObject()
        obj.addProperty("balance", player.balance)
        return obj
    }

    /**
     * Get a player's currency amount.
     *
     * @param data The JsonObject with the balance data
     */
    fun deserialize(data: JsonObject): Double
    {
        if (data.has("balance"))
        {
            return data["balance"].asDouble
        }

        return 0.0
    }
}
