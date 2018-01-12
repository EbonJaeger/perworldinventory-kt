package me.ebonjaeger.perworldinventory.serialization

import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.configuration.PlayerSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import me.ebonjaeger.perworldinventory.data.PlayerProfile
import org.bukkit.entity.Player

class PlayerSerializer(private val plugin: PerWorldInventory,
                       private val settings: Settings)
{

    private val statSerializer = StatSerializer(settings)

    /**
     * Serialize a Player into a JsonObject. The player's EnderChest, inventory
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
     * @param player The player to serialize
     * @return The serialized player's data in its entirety
     */
    fun serialize(player: PlayerProfile): JsonObject
    {
        ConsoleLogger.debug("[SERIALIZER] Serializing player '${player.name}'")
        val obj = JsonObject()

        obj.addProperty("data-format", 2)
        obj.add("ender-chest", InventorySerializer.serializeInventory(player.enderChest))
        obj.add("inventory", InventorySerializer.serializeAllInventories(player))
        obj.add("stats", statSerializer.serialize(player))
        obj.add("economy", EconomySerializer.serialize(player))

        ConsoleLogger.debug("[SERIALIZER] Done serializing player '${player.name}'")
        return obj
    }

    fun deserialize(data: JsonObject, player: Player, cause: DeserializeCause)
    {
        ConsoleLogger.debug("[SERIALIZER] Deserializing player '${player.name}'")

        // Get the data format being used
        var format = 2
        if (data.has("data-format"))
        {
            format = data["data-format"].asInt
        }

        // Set the player's new EnderChest contents
        if (settings.getProperty(PlayerSettings.LOAD_ENDER_CHEST) &&
                data.has("ender-chest"))
        {
            player.enderChest.clear()
            player.enderChest.contents =
                    InventorySerializer.deserialize(data["ender-chest"].asJsonArray,
                            player.enderChest.size, format)
        }

        // Set the player's new inventory and armor contents
        if (settings.getProperty(PlayerSettings.LOAD_INVENTORY) &&
                data.has("inventory"))
        {
            InventorySerializer.setInventories(player, data["inventory"].asJsonObject, format)
        }

        // Apply the player's stats and potion effects
        if (data.has("stats"))
        {
            statSerializer.applyStats(player, data["stats"].asJsonObject, format)
        }

        if (plugin.econEnabled)
        {
            val econ = plugin.economy
            if (econ == null)
            {
                ConsoleLogger.warning("Economy saving is turned on, but no economy found!")
            } else
            {
                ConsoleLogger.debug("[ECON] Withdrawing ${econ.getBalance(player)} from '${player.name}'!")
                val er = econ.withdrawPlayer(player, econ.getBalance(player))

                if (er.transactionSuccess())
                {
                    if (data.has("economy"))
                    {
                        EconomySerializer.apply(player, econ, data["economy"].asJsonObject)
                    }
                } else
                {
                    ConsoleLogger.warning(
                            "[ECON] Unable to withdraw funds from '${player.name}': ${er.errorMessage}")
                }
            }
        }

        ConsoleLogger.debug("[SERIALIZER] Done deserializing player '${player.name}'")

        // TODO: Call event to signal loading is done
    }
}
