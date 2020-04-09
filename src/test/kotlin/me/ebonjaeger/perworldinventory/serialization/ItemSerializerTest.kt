package me.ebonjaeger.perworldinventory.serialization

import io.mockk.every
import io.mockk.mockkClass
import me.ebonjaeger.perworldinventory.TestHelper
import net.minidev.json.JSONObject
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.UnsafeValues
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.util.io.BukkitObjectOutputStream
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.fail
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

/**
 * Tests for [ItemSerializer].
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemSerializerTest {

    private lateinit var unsafe: UnsafeValues

    @BeforeAll
    fun writeMocksAndInitializeSerializer() {
        TestHelper.mockBukkit()
        val itemFactory = TestHelper.mockItemFactory()
        every { Bukkit.getItemFactory() } returns itemFactory

        // Bukkit's serializer needs to know about our test implementation of ItemMeta or it will fail
        ConfigurationSerialization.registerClass(ItemMetaTestImpl::class.java)

        // As of 1.13, Bukkit has a compatibility layer, and serializing an item
        // now checks the data version to see what Material name to use.
        unsafe = mockkClass(UnsafeValues::class, relaxed = true)
        every { Bukkit.getUnsafe() } returns unsafe
        every { unsafe.dataVersion } returns 1513
        every { unsafe.getMaterial("APPLE", 1513) } returns Material.APPLE
        every { unsafe.getMaterial("TORCH", 1513) } returns Material.TORCH
    }

    @Test
    fun verifySerializedItem() {
        // given
        val item = ItemStack(Material.APPLE, 5)

        // when
        val json = ItemSerializer.serialize(item, 1)

        // then
        val result = ItemSerializer.deserialize(JSONObject(json), 3)
        assertHasSameProperties(item, result)
    }

    @Test
    fun verifySerializedItemWithMeta() {
        // given
        val item = ItemStack(Material.APPLE, 3)
        val meta = ItemMetaTestImpl(mutableMapOf(Pair("test", "test"), Pair("values", 5)))
        setItemMetaOrFail(item, meta)

        // when
        val json = ItemSerializer.serialize(item, 0)

        // then
        // deserialize item and test for match
        val result = ItemSerializer.deserialize(JSONObject(json), 3)
        assertHasSameProperties(item, result)
        assertItemMetaMapsAreEqual(result, item)
    }

    @Test
    fun verifyEncodedDeserialization()
    {
        // given
        val expected = ItemStack(Material.TORCH, 16)

        val obj = JSONObject()
        ByteArrayOutputStream().use { os ->
            BukkitObjectOutputStream(os).use {
                it.writeObject(expected)
            }

            val encoded = Base64Coder.encodeLines(os.toByteArray())
            obj["item"] = encoded
        }

        // when
        val actual = ItemSerializer.deserialize(obj, 2)

        // then
        assertHasSameProperties(expected, actual)
    }

    private fun assertHasSameProperties(expected: ItemStack, actual: ItemStack) {
        assertEquals(expected.type, actual.type)
        assertEquals(expected.amount, actual.amount)
        assertEquals(expected.data, actual.data)
    }

    /**
     * Sets the given ItemMeta to the provided ItemStack object, and fails if the setter
     * method reports the action as unsuccessful.
     */
    private fun setItemMetaOrFail(item: ItemStack, meta: ItemMeta) {
        val isSuccessful = item.setItemMeta(meta)
        assertEquals(isSuccessful, true, "Setting ItemMeta to ItemStack was unsuccessful; this is likely a missing mock behavior")
    }

    private fun assertItemMetaMapsAreEqual(given: ItemStack, expected: ItemStack) {
        if (given.itemMeta is ItemMetaTestImpl) {
            val givenItemMeta = given.itemMeta as ItemMetaTestImpl
            val expectedItemMeta = expected.itemMeta as ItemMetaTestImpl
            if (expectedItemMeta.providedMap.isEmpty()) {
                fail("Map is empty!")
            } else {
                for (entry in expectedItemMeta.providedMap.entries) {
                    assertEquals(givenItemMeta.providedMap[entry.key], entry.value)
                }
            }
        } else {
            fail("Expected given.itemMeta to be of test impl, but was ${given.itemMeta!!::class}")
        }
    }
}
