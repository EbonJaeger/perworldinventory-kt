package me.ebonjaeger.perworldinventory.serialization

import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.data.PlayerProfile
import net.milkbowl.vault.economy.Economy
import org.bukkit.entity.Player

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
     * Apply a player's economical value.
     *
     * @param player The player
     * @param economy The server's economy hook
     * @param data The JsonObject with the balance data
     */
    fun apply(player: Player, economy: Economy, data: JsonObject)
    {
        if (data.has("balance"))
        {
            ConsoleLogger.debug("[ECON] Depositing ${data.get("balance").asDouble} to '${player.name}'!")
            economy.depositPlayer(player, data["balance"].asDouble)
        }
    }
}
