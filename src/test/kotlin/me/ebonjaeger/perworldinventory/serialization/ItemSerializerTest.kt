package me.ebonjaeger.perworldinventory.serialization

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import net.minidev.json.JSONObject
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.inventory.ItemFactory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.util.io.BukkitObjectOutputStream
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayOutputStream

/**
 * Tests for [ItemSerializer].
 */
@PrepareForTest(Bukkit::class)
@RunWith(PowerMockRunner::class)
class ItemSerializerTest {

    @Before
    fun writeMocksAndInitializeSerializer() {
        mockStatic(Bukkit::class.java)
        val itemFactory = mock(ItemFactory::class.java)
        given(Bukkit.getItemFactory()).willReturn(itemFactory)

        // No implementation of the ItemMeta interface readily available, so we return our own
        given(itemFactory.getItemMeta(any())).willAnswer { ItemMetaTestImpl() }

        // This is required for ItemStack#setItemMeta to be successful
        given(itemFactory.isApplicable(any(ItemMeta::class.java), any(Material::class.java))).willReturn(true)
        given(itemFactory.asMetaFor(any(ItemMeta::class.java), any(Material::class.java))).willAnswer(ReturnsArgumentAt(0))

        // Bukkit's serializer needs to know about our test implementation of ItemMeta or it will fail
        ConfigurationSerialization.registerClass(ItemMetaTestImpl::class.java)
    }

    @Test
    fun verifySerializedItem() {
        // given
        val item = ItemStack(Material.APPLE, 5)

        // when
        val json = ItemSerializer.serialize(item, 1)

        // then
        val result = ItemSerializer.deserialize(json, 3)
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
        val result = ItemSerializer.deserialize(json, 3)
        assertHasSameProperties(result, item)
        assertItemMetaMapsAreEqual(result, item)
    }

    @Test
    fun verifyEncodedDeserialization()
    {
        // given
        val expected = ItemStack(Material.TORCH, 16)
        val obj = JSONObject()
        ByteArrayOutputStream().use {
            BukkitObjectOutputStream(it).use {
                it.writeObject(expected)
            }
            val encoded = Base64Coder.encodeLines(it.toByteArray())
            obj["item"] = encoded
        }
        // when
        val actual = ItemSerializer.deserialize(obj, 2)
        // then
        assertHasSameProperties(expected, actual)
    }

    private fun assertHasSameProperties(given: ItemStack, expected: ItemStack) {
        assertThat(given.type, equalTo(expected.type))
        assertThat(given.amount, equalTo(expected.amount))
        assertThat(given.data, equalTo(expected.data))
        assertThat(given.durability, equalTo(expected.durability))
    }

    /**
     * Sets the given ItemMeta to the provided ItemStack object, and fails if the setter
     * method reports the action as unsuccessful.
     */
    private fun setItemMetaOrFail(item: ItemStack, meta: ItemMeta) {
        val isSuccessful = item.setItemMeta(meta)
        assertThat("Setting ItemMeta to ItemStack was unsuccessful; this is likely a missing mock behavior",
            isSuccessful, equalTo(true))
    }

    private fun assertItemMetaMapsAreEqual(given: ItemStack, expected: ItemStack) {
        if (given.itemMeta is ItemMetaTestImpl) {
            val givenItemMeta = given.itemMeta as ItemMetaTestImpl
            val expectedItemMeta = expected.itemMeta as ItemMetaTestImpl
            if (expectedItemMeta.providedMap.isEmpty()) {
                fail("Map is empty!")
            } else {
                for (entry in expectedItemMeta.providedMap.entries) {
                    assertThat(givenItemMeta.providedMap[entry.key], equalTo(entry.value))
                }
            }
        } else {
            fail("Expected given.itemMeta to be of test impl, but was ${given.itemMeta::class}")
        }
    }
}
