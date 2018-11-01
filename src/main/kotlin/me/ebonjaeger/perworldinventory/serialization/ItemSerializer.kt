package me.ebonjaeger.perworldinventory.serialization

import com.dumptruckman.bukkit.configuration.util.SerializationHelper
import me.ebonjaeger.perworldinventory.ConsoleLogger
import net.minidev.json.JSONObject
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.util.io.BukkitObjectInputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.IOException

object ItemSerializer
{

    private const val AIR = "AIR"

    /**
     * Serialize an ItemStack to a JsonObject.
     *
     * @param item The item to serialize
     * @param index The position in the inventory
     * @return A JsonObject with the item and its index
     */
    fun serialize(item: ItemStack?, index: Int): JSONObject
    {
        val obj = JSONObject()
        obj["index"] = index

        // If item is null, return air
        if (item == null)
        {
            obj["item"] = AIR
            return obj
        }

        /*
         * Check to see if the item is a skull with a null owner.
         * This is because some people are getting skulls with null owners, which causes Spigot to throw an error
         * when it tries to serialize the item.
         */
        if (item.type == Material.SKULL_ITEM)
        {
            val meta = item.itemMeta as SkullMeta
            if (meta.hasOwner() && (meta.owner.isNullOrEmpty()))
            {
                item.itemMeta = Bukkit.getServer().itemFactory.getItemMeta(Material.SKULL_ITEM)
            }
        }

        obj["item"] = SerializationHelper.serialize(item)

        return obj
    }

    /**
     * Get an ItemStack from a JsonObject.
     *
     * @param obj The Json to read
     * @param format The data format being used. Refer to {@link PlayerSerializer#serialize(PWIPlayer)}
     * @return The deserialized item stack
     */
    @Suppress("UNCHECKED_CAST") // Reading a map we created; it's safe to assume the Map types
    fun deserialize(obj: JSONObject, format: Int): ItemStack
    {
        when (format)
        {
            0 -> throw IllegalArgumentException("Old data format is not supported!")
            1, 2 ->
            {
                val encoded = obj["item"] as String
                return decodeItem(encoded)
            }
            3 ->
            {
                return if (obj["item"] is Map<*, *>)
                {
                    val item = obj["item"] as Map<String, Any>
                    SerializationHelper.deserialize(item) as ItemStack
                } else
                {
                    ItemStack(Material.AIR)
                }
            }
            else ->
            {
                throw IllegalArgumentException("Unknown data format '$format'.")
            }
        }
    }

    private fun decodeItem(encoded: String): ItemStack
    {
        try
        {
            if (encoded == AIR)
            {
                return ItemStack(Material.AIR)
            } else
            {
                ByteArrayInputStream(Base64Coder.decodeLines(encoded)).use {
                    BukkitObjectInputStream(it).use { return it.readObject() as ItemStack }
                }
            }
        } catch (ex: IOException)
        {
            ConsoleLogger.severe("Unable to deserialize an item:", ex)
            return ItemStack(Material.AIR)
        } catch (ex: ClassNotFoundException)
        {
            ConsoleLogger.severe("Unable to deserialize an item:", ex)
            return ItemStack(Material.AIR)
        }
    }
}
