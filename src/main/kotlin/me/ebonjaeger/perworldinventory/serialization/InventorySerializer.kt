package me.ebonjaeger.perworldinventory.serialization

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.PlayerInfo
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object InventorySerializer
{

    /**
     * Serialize a player's inventory. This will save the armor contents of the
     * inventory along with the main inventory items.
     *
     * @param player The player to serialize
     * @return A JsonObject with a player's armor and inventory contents
     */
    fun serializeAllInventories(player: PlayerInfo): JsonObject
    {
        val obj = JsonObject()
        val inventory = serializeInventory(player.inventory)
        val armor = serializeInventory(player.armor)

        obj.add("inventory", inventory)
        obj.add("armor", armor)

        return obj
    }

    /**
     * Serialize an inventory's contents.
     *
     * @param contents The items in the inventory
     * @return A JsonArray containing the inventory contents
     */
    fun serializeInventory(contents: Array<out ItemStack>): JsonArray
    {
        val inventory = JsonArray()

        for (i in contents.indices)
        {
            val item = ItemSerializer.serialize(contents[i], i)
            inventory.add(item)
        }

        return inventory
    }

    /**
     * Sets a player's armor and inventory contents.
     *
     * @param player The player whose inventory to update
     * @param obj The serialized inventory data
     * @param format The data format being used
     */
    fun setInventories(player: Player, obj: JsonObject, format: Int)
    {
        val inventory = player.inventory
        val armor = deserialize(obj["armor"].asJsonArray, 4, format)
        val inv = deserialize(obj["inventory"].asJsonArray, inventory.size, format)

        inventory.clear()
        inventory.armorContents = armor
        inventory.contents = inv
    }

    /**
     * Gets an ItemStack array from serialized inventory contents.
     *
     * @param array The array of items to deserialize
     * @param size The expected size of the inventory; can be greater than expected
     * @param format The data format being used
     * @return An array of ItemStacks
     */
    fun deserialize(array: JsonArray, size: Int, format: Int): Array<out ItemStack>
    {
        val contents = Array(size, { ItemStack(Material.AIR) })

        for (i in 0..array.size())
        {
            val obj = array[i].asJsonObject
            val index = obj["index"].asInt
            val item = ItemSerializer.deserialize(obj, format)

            contents[index] = item
        }

        return contents
    }
}