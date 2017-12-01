package me.ebonjaeger.perworldinventory.serialization

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFactory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.powermock.api.mockito.PowerMockito.*
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * Tests for [ItemSerializer].
 */
@RunWith(PowerMockRunner::class)
class ItemSerializerTest
{

    @Test
    @PrepareForTest(Bukkit::class)
    fun verifySerializedItem()
    {
        // given
        mockStatic(Bukkit::class.java)
        val itemFactory = mock(ItemFactory::class.java)
        `when`(Bukkit.getItemFactory()).thenReturn(itemFactory)
        `when`(itemFactory.getItemMeta(any())).thenReturn(mock(ItemMeta::class.java))

        val item = ItemStack(Material.APPLE, 5)

        // when
        val json = ItemSerializer.serialize(item, 1)

        // then
        val result = ItemSerializer.deserialize(json, 2)
        assertThat(result, equalTo(item))
    }

    @Test
    @PrepareForTest(Bukkit::class)
    fun verifySerializedItemWithMeta()
    {
        // given
        mockStatic(Bukkit::class.java)
        val itemFactory = mock(ItemFactory::class.java)
        `when`(Bukkit.getItemFactory()).thenReturn(itemFactory)
        `when`(itemFactory.getItemMeta(any())).thenReturn(mock(ItemMeta::class.java))

        val item = ItemStack(Material.APPLE, 3)
        val meta = Bukkit.getItemFactory().getItemMeta(Material.APPLE)
        meta.displayName = "Testing Apple"
        meta.addEnchant(Enchantment.DURABILITY, 1 ,false)
        meta.lore.add("Very Strong Apple")
        item.itemMeta = meta

        // when
        val json = ItemSerializer.serialize(item, 0)

        // then
        // deserialize item and test for match
        val result = ItemSerializer.deserialize(json, 2)
        assertThat(result, equalTo(item))
    }
}