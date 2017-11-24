package me.ebonjaeger.perworldinventory.serialization

import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object ItemSerializer
{

    /**
     * Serialize an ItemStack to a JsonObject.
     *
     * The item itself will be saved as a Base64 encoded string to
     * simplify the serialization and deserialization process. The result is
     * not human readable.
     *
     * @param item The item to serialize
     * @param index The position in the inventory
     * @return A JsonObject with the encoded item
     */
    fun serialize(item: ItemStack, index: Int): JsonObject
    {
        val obj = JsonObject()

        /*
         * Check to see if the item is a skull with a null owner.
         * This is because some people are getting skulls with null owners, which causes Spigot to throw an error
         * when it tries to serialize the item. If this ever gets fixed in Spigot, this will be removed.
         */
        if (item.type == Material.SKULL_ITEM)
        {
            val meta = item.itemMeta as SkullMeta
            if (meta.hasOwner() && (meta.owner == null || meta.owner.isEmpty()))
            {
                item.itemMeta = Bukkit.getServer().itemFactory.getItemMeta(Material.SKULL_ITEM)
            }
        }

        obj.addProperty("index", index)

        ByteArrayOutputStream().use {
            BukkitObjectOutputStream(it).use {
                it.writeObject(item)
            }

            val encoded = Base64Coder.encodeLines(it.toByteArray())
            obj.addProperty("item", encoded)
        }

        return obj
    }

    /**
     * Get an ItemStack from a JsonObject.
     *
     * @param obj The Json to read
     * @param format The data format being used. Refer to {@link PlayerSerializer#serialize(PWIPlayer)}
     * @return The deserialized item stack
     */
    fun deserialize(obj: JsonObject, format: Int): ItemStack
            = when (format)
            {
                0 -> throw IllegalArgumentException("Old data format is not supported!")
                1, 2 -> {
                    ByteArrayInputStream(Base64Coder.decodeLines(obj.get("item").asString)).use {
                        BukkitObjectInputStream(it).use { return it.readObject() as ItemStack }
                    }
                }
                else -> {
                    throw IllegalArgumentException("Unknown data format '$format'.")
                }
            }
}