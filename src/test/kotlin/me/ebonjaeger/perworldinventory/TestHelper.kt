package me.ebonjaeger.perworldinventory

import java.io.File
import java.net.URI
import java.net.URISyntaxException

/**
 * Test utilities.
 */
object TestHelper
{

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
}
