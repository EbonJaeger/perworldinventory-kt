package me.ebonjaeger.perworldinventory.serialization

import me.ebonjaeger.perworldinventory.data.PlayerProfile
import net.minidev.json.JSONObject
import org.bukkit.util.NumberConversions

object EconomySerializer
{

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
