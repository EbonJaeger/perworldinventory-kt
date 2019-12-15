package me.ebonjaeger.perworldinventory

import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkStatic
import io.mockk.slot
import me.ebonjaeger.perworldinventory.serialization.ItemMetaTestImpl
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.inventory.ItemFactory
import org.bukkit.inventory.meta.ItemMeta
import java.io.File
import java.net.URI
import java.net.URISyntaxException

/**
 * Test utilities.
 */
object TestHelper
{

    val PROJECT_PACKAGE = "me.ebonjaeger.perworldinventory"

    /**
     * Return a [File] to a file in the JAR's resources (main or test).
     *
     * @param path The absolute path to the file
     * @return The project file
     */
    fun getFromJar(path: String): File
    {
        val uri = getUriOrThrow(path)
        return File(uri.path)
    }

    private fun getUriOrThrow(path: String): URI
    {
        val url = TestHelper.javaClass.getResource(path) ?:
                throw IllegalStateException("File '$path' could not be loaded")

        try
        {
            return URI(url.toString())
        } catch (ex: URISyntaxException)
        {
            throw IllegalStateException("File '$path' cannot be converted to URI")
        }
    }

    /**
     * Make a new [Group] for testing.
     *
     * @param name The name of the group
     * @return A new group with the given name
     */
    fun mockGroup(name: String): Group
    {
        val worlds = mutableSetOf(name, "${name}_nether", "${name}_the_end")
        return mockGroup(name, worlds)
    }

    /**
     * Make a new [Group] for testing, with a provided list of worlds.
     *
     * @param name The name of the group
     * @param worlds The world names in the group
     * @return A new group with the given name and worlds
     */
    fun mockGroup(name: String, worlds: MutableSet<String>): Group
    {
        return mockGroup(name, worlds, GameMode.SURVIVAL)
    }

    /**
     * Make a new [Group] for testing, with a provided list of worlds and a default GameMode.
     *
     * @param name The name of the group
     * @param worlds The world names in the group
     * @param gameMode The default GameMode
     * @return A new group with the given name, worlds and default GameMode
     */
    fun mockGroup(name: String, worlds: MutableSet<String>, gameMode: GameMode): Group {
        return Group(name, worlds, gameMode, null)
    }

    fun mockBukkit() {
        mockkStatic(Bukkit::class)

        @Suppress("SENSELESS_COMPARISON") // There is exactly one time where this is not false: the first time
        if (Bukkit.getServer() == null) {
            val server = mockkClass(Server::class, relaxed = true)
            Bukkit.setServer(server)
        }
    }

    fun mockItemFactory(): ItemFactory {
        val itemFactory = mockkClass(ItemFactory::class)

        // No implementation of the ItemMeta interface readily available, so we return our own
        every { itemFactory.getItemMeta(any()) } answers { ItemMetaTestImpl() }

        // This is required for ItemStack#setItemMeta to be successful
        val itemMeta = ItemMetaTestImpl()
        val metaArg = slot<ItemMeta>()
        val materialArg = slot<Material>()
        every { itemFactory.getItemMeta(any()) } answers { itemMeta }
        every { itemFactory.equals(any(), isNull()) } returns false
        every { itemFactory.isApplicable(ofType(ItemMeta::class), ofType(Material::class)) } returns true
        every { itemFactory.asMetaFor(capture(metaArg), ofType(Material::class)) } answers { metaArg.captured }
        every { itemFactory.updateMaterial(ofType(ItemMeta::class), capture(materialArg)) } answers { materialArg.captured }

        return itemFactory
    }
}
