package me.ebonjaeger.perworldinventory.serialization

import net.minidev.json.JSONObject
import org.bukkit.Bukkit
import org.bukkit.Location

object LocationSerializer
{

    /**
     * Serialize a [Location] into a JsonObject.
     *
     * @param location The location to serialize
     * @return A JsonObject with the world name and location properties
     */
    fun serialize(location: Location): JSONObject
    {
        val obj = JSONObject()
        obj["world"] = location.world.name
        obj["x"] = location.x
        obj["y"] = location.y
        obj["z"] = location.z
        obj["pitch"] = location.pitch
        obj["yaw"] = location.yaw

        return obj
    }

    /**
     * Deserialize a [Location] from a JsonObject.
     *
     * @param obj The JsonObject to deserialize
     * @return A new location from the properties of the JsonObject
     */
    fun deserialize(obj: JSONObject): Location
    {
        val world = Bukkit.getWorld(obj["world"] as String)
        val x = obj["x"] as Float
        val y = obj["y"] as Float
        val z = obj["z"] as Float
        val pitch = obj["pitch"] as Float
        val yaw = obj["yaw"] as Float

        return Location(world, x.toDouble(), y.toDouble(), z.toDouble(), yaw, pitch)
    }
}
