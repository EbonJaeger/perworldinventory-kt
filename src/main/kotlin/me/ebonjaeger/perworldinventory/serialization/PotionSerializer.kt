package me.ebonjaeger.perworldinventory.serialization

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.Utils
import org.bukkit.Bukkit
import org.bukkit.Color
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
    fun serialize(effects: MutableCollection<PotionEffect>): JsonArray
    {
        val array = JsonArray()

        effects.forEach {
            val obj = JsonObject()

            obj.addProperty("type", it.type.name)
            obj.addProperty("amp", it.amplifier)
            obj.addProperty("duration", it.duration)
            obj.addProperty("ambient", it.isAmbient)
            obj.addProperty("particles", it.hasParticles())

            // The color augments were added in the 1.9.0 API
            val saveColor = Utils.checkServerVersion(Bukkit.getVersion(), 1, 9, 0)
            if (saveColor)
            {
                obj.addProperty("color", it.color.asRGB())
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
    fun deserialize(array: JsonArray): MutableCollection<PotionEffect>
    {
        val effects = mutableListOf<PotionEffect>()

        for (i in 0 until array.size())
        {
            val obj = array[i].asJsonObject

            val type = PotionEffectType.getByName(obj["type"].asString)
            val amplifier = obj["amp"].asInt
            val duration = obj["duration"].asInt
            val ambient = obj["ambient"].asBoolean
            val particles = obj["particles"].asBoolean

            // The color augments were added in the 1.9.0 API
            val getColor = Utils.checkServerVersion(Bukkit.getVersion(), 1, 9, 0) &&
                    obj.has("color")
            if (getColor)
            {
                val color = Color.fromRGB(obj["color"].asInt)
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
