package me.ebonjaeger.perworldinventory.serialization

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.data.PlayerProfile
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object InventoryHelper
{

    /**
     * Serialize an inventory's contents.
     *
     * @param contents The items in the inventory
     * @return A JsonArray containing the inventory contents
     */
    fun serializeInventory(contents: Array<out ItemStack>): List<Map<String, Any>>
    {
        val inventory = mutableListOf<Map<String, Any>>()

        contents.indices
                .map { ItemSerializer.serialize(contents[it], it) }
                .forEach { inventory.add(it) }

        return inventory
    }

    fun listToInventory(items: List<*>): Array<out ItemStack> {
        val contents = mutableListOf<ItemStack>()
        val iter = items.listIterator()
        while (iter.hasNext()) {
            val next = iter.next() as Map<*, *>
            val item = next["item"] as ItemStack
            contents.add(item)
        }

        return contents.toTypedArray()
    }

    /**
     * Gets an ItemStack array from serialized inventory contents.
     *
     * @param array The array of items to deserialize
     * @param size The expected size of the inventory; can be greater than expected
     * @param format The data format being used
     * @return An array of ItemStacks
     */
    fun deserialize(array: JSONArray, size: Int, format: Int): Array<out ItemStack>
    {
        val contents = Array(size) { ItemStack(Material.AIR) }

        for (i in 0 until array.size)
        {
            // We don't want to risk failing to deserialize a players inventory.
            // Try your best to deserialize as much as possible.
            try
            {
                val obj = array[i] as JSONObject
                val index = obj["index"] as Int
                val item = ItemSerializer.deserialize(obj, format)

                contents[index] = item
            } catch (ex: Exception)
            {
                ConsoleLogger.warning("Failed to deserialize item in inventory:", ex)
            }
        }

        return contents
    }
}
