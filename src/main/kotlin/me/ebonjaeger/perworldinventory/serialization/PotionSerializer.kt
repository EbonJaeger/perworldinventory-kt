package me.ebonjaeger.perworldinventory.serialization

import com.dumptruckman.bukkit.configuration.util.SerializationHelper
import me.ebonjaeger.perworldinventory.ConsoleLogger
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object PotionSerializer
{

    /**
     * Serialize a Collection of PotionEffects. The
     * effects are serialized using their ConfigurationSerialization method.
     *
     * @param effects The PotionEffects to serialize
     * @return The serialized PotionEffects
     */
    @Suppress("UNCHECKED_CAST") // We know that #serialize will give us a Map for a ConfigurationSerializable object
    fun serialize(effects: MutableCollection<PotionEffect>): List<Map<String, Any>>
    {
        val list = mutableListOf<Map<String, Any>>()

        effects.forEach { effect ->
            val map = SerializationHelper.serialize(effect) as Map<String, Any>
            list.add(map)
        }

        return list
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

            val effect = if (obj.containsKey("==")) { // Object contains the classname as a key
                val map = obj as Map<String, Any>
                SerializationHelper.deserialize(map) as PotionEffect
            } else { // Likely older data, try to use the old long way
                deserializeLongWay(obj)
            }

            if (effect != null) { // Potion effect de-serialized correctly
                effects.add(effect)
            }
        }

        return effects
    }

    @Deprecated("Kept for backwards compatibility, and may be removed in a later MC version")
    private fun deserializeLongWay(obj: JSONObject): PotionEffect? {
        val type = PotionEffectType.getByName(obj["type"] as String)

        if (type == null) {
            ConsoleLogger.warning("Unable to get potion effect for type: ${obj["type"]}")
            return null
        }

        val amplifier = obj["amp"] as Int
        val duration = obj["duration"] as Int
        val ambient = obj["ambient"] as Boolean
        val particles = obj["particles"] as Boolean

        // Randomly in 1.13, color stopped being a thing, and now icon is. Yay.
        val hasIcon = if (obj.containsKey("hasIcon")) obj["hasIcon"] as Boolean else false

        return PotionEffect(type, duration, amplifier, ambient, particles, hasIcon)
    }
}
