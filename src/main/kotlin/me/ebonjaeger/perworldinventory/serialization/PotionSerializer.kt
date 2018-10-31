package me.ebonjaeger.perworldinventory.serialization

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.Utils
import org.bukkit.Bukkit
import org.bukkit.Color
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object PotionSerializer
{

    /**
     * Serialize a Collection of PotionEffects into a JsonArray of JsonObjects. Each
     * JsonObject contains the type, amplifier, duration and color of a potion effect.
     * The color is saved in the RGB format.
     *
     * @param effects The PotionEffects to serialize
     * @return The serialized PotionEffects
     */
    fun serialize(effects: MutableCollection<PotionEffect>): JSONArray
    {
        val array = JSONArray()

        effects.forEach {
            val obj = JSONObject()

            obj["type"] = it.type.name
            obj["amp"] = it.amplifier
            obj["duration"] = it.duration
            obj["ambient"] = it.isAmbient
            obj["particles"] = it.hasParticles()

            // The color augments were added in the 1.9.0 API
            val saveColor = Utils.checkServerVersion(Bukkit.getVersion(), 1, 9, 0)
            if (saveColor && it.hasParticles() && it.color != null)
            {
                obj["color"] = it.color.asRGB()
            }

            array.add(obj)
        }

        return array
    }

    /**
     * Return a Collection of PotionEffects from a given JsonArray.
     *
     * @param array The serialized PotionEffects
     * @return The PotionEffects
     */
    fun deserialize(array: JSONArray): MutableCollection<PotionEffect>
    {
        val effects = mutableListOf<PotionEffect>()

        for (i in 0 until array.size)
        {
            val obj = array[i] as JSONObject

            val type = PotionEffectType.getByName(obj["type"] as String)
            val amplifier = obj["amp"] as Int
            val duration = obj["duration"] as Int
            val ambient = obj["ambient"] as Boolean
            val particles = obj["particles"] as Boolean

            // The color augments were added in the 1.9.0 API
            val getColor = Utils.checkServerVersion(Bukkit.getVersion(), 1, 9, 0) &&
                    obj.containsKey("color")
            if (getColor)
            {
                val color = Color.fromRGB(obj["color"] as Int)
                val effect = PotionEffect(type, duration, amplifier, ambient, particles, color)
                effects.add(effect)
                continue
            }

            val effect = PotionEffect(type, duration, amplifier, ambient, particles)
            effects.add(effect)
        }

        return effects
    }
}
