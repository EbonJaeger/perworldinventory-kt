package me.ebonjaeger.perworldinventory.serialization

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.given
import me.ebonjaeger.perworldinventory.data.PlayerProfile
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.inventory.ItemFactory
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * Tests for [PlayerSerializer].
 */
@PrepareForTest(Bukkit::class)
@RunWith(PowerMockRunner::class)
class PlayerSerializerTest
{

    @Before
    fun prepareTestingStuff()
    {
        PowerMockito.mockStatic(Bukkit::class.java)
        val itemFactory = PowerMockito.mock(ItemFactory::class.java)
        given(Bukkit.getItemFactory()).willReturn(itemFactory)

        // No implementation of the ItemMeta interface readily available, so we return our own
        given(itemFactory.getItemMeta(ArgumentMatchers.any())).willAnswer({ ItemMetaTestImpl() })

        // Bukkit's serializer needs to know about our test implementation of ItemMeta or it will fail
        ConfigurationSerialization.registerClass(ItemMetaTestImpl::class.java)
    }

    @Test
    fun verifyCorrectSerialization()
    {
        // given
        val armor = arrayOf(ItemStack(Material.AIR), ItemStack(Material.DIAMOND_CHESTPLATE),
                ItemStack(Material.IRON_LEGGINGS), ItemStack(Material.AIR))
        val enderChest = arrayOf(ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND))
        val inventory = arrayOf(ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND), ItemStack(Material.AIR),
                ItemStack(Material.DIAMOND))
        val profile = PlayerProfile(armor, enderChest, inventory, false, "Bob",
                5.0F, 50.5F, false, 20, 20.0, 14.3, GameMode.SURVIVAL, 5, 4.86F,
                mutableSetOf<PotionEffect>(), 0.0F, 0, 500, 500, 0.0)

        // when
        val json = PlayerSerializer.serialize(profile)

        // then
        val result = PlayerSerializer.deserialize(json)
        assertThat(profile, equalTo(result))
    }
}
