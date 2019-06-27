package me.ebonjaeger.perworldinventory

import org.bukkit.GameMode
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
    fun mockGroup(name: String, worlds: MutableSet<String>, gameMode: GameMode): Group
    {
        return Group(name, worlds, gameMode, null)
    }
}
