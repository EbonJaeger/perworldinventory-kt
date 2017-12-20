package me.ebonjaeger.perworldinventory.serialization

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
class PotionSerializerTest
{

    @Before
    fun registerPotionEffects()
    {
        PotionEffectType.registerPotionEffectType(PotionEffectType.ABSORPTION)
        PotionEffectType.registerPotionEffectType(PotionEffectType.GLOWING)
    }

    @Test
    fun verifySerializedPotions()
    {
        // given
        val effects = mutableListOf<PotionEffect>()
        print(PotionEffectType.values())

        val effect1 = PotionEffect(PotionEffectType.ABSORPTION, 5, 2, true, false)
        val effect2 = PotionEffect(PotionEffectType.GLOWING, 27, 1)

        effects.add(effect1)
        effects.add(effect2)

        // when
        val json = PotionSerializer.serialize(effects)

        // then
        val result = PotionSerializer.deserialize(json)
        assertHasSameProperties(result.first(), effect1)
        assertHasSameProperties(result.last(), effect2)
    }

    private fun assertHasSameProperties(given: PotionEffect, expected: PotionEffect)
    {
        assertThat(given.type, equalTo(expected.type))
        assertThat(given.duration, equalTo(expected.duration))
        assertThat(given.amplifier, equalTo(expected.amplifier))
        assertThat(given.isAmbient, equalTo(expected.isAmbient))
        assertThat(given.hasParticles(), equalTo(expected.hasParticles()))
    }
}
