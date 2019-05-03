package me.ebonjaeger.perworldinventory.serialization

import com.dumptruckman.bukkit.configuration.util.SerializationHelper
import net.minidev.json.JSONObject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.NumberConversions

object LocationSerializer
{

    /**
     * Serialize a [Location] into a JsonObject.
     *
     * @param location The location to serialize
     * @return A JsonObject with the world name and location properties
     */
    @Suppress("UNCHECKED_CAST") // We know that #serialize will give us a Map for a ConfigurationSerializable object
    fun serialize(location: Location): JSONObject
    {
        val map = SerializationHelper.serialize(location) as Map<String, Any>
        return JSONObject(map)
    }

    /**
     * Deserialize a [Location] from a JsonObject.
     *
     * @param obj The JsonObject to deserialize
     * @return A new location from the properties of the JsonObject
     */
    fun deserialize(obj: JSONObject): Location
    {
        return if (obj.containsKey("==")) { // Location was serialized using ConfigurationSerializable method
            SerializationHelper.deserialize(obj as Map<String, Any>) as Location
        } else { // Location was serialized by hand
            val world = Bukkit.getWorld(obj["world"] as String)
            val x = NumberConversions.toDouble(obj["x"])
            val y = NumberConversions.toDouble(obj["y"])
            val z = NumberConversions.toDouble(obj["z"])
            val pitch = NumberConversions.toFloat(obj["pitch"])
            val yaw = NumberConversions.toFloat(obj["yaw"])

            Location(world, x, y, z, yaw, pitch)
        }
    }
}
