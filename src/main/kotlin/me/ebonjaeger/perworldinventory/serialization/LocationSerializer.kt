package me.ebonjaeger.perworldinventory.serialization

import com.google.gson.JsonObject
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
    fun serialize(location: Location): JsonObject
    {
        val obj = JsonObject()
        obj.addProperty("world", location.world.name)
        obj.addProperty("x", location.x)
        obj.addProperty("y", location.y)
        obj.addProperty("z", location.z)
        obj.addProperty("pitch", location.pitch)
        obj.addProperty("yaw", location.yaw)

        return obj
    }

    /**
     * Deserialize a [Location] from a JsonObject.
     *
     * @param obj The JsonObject to deserialize
     * @return A new location from the properties of the JsonObject
     */
    fun deserialize(obj: JsonObject): Location
    {
        val world = Bukkit.getWorld(obj["world"].asString)
        val x = obj["x"].asDouble
        val y = obj["y"].asDouble
        val z = obj["z"].asDouble
        val pitch = obj["pitch"].asFloat
        val yaw = obj["yaw"].asFloat

        return Location(world, x, y, z, yaw, pitch)
    }
}
