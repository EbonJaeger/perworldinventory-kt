package me.ebonjaeger.perworldinventory.locale

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.Utils
import me.ebonjaeger.perworldinventory.configuration.PluginSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import javax.annotation.PostConstruct
import javax.inject.Inject

class MessageFileHandler @Inject constructor(private val plugin: PerWorldInventory,
                                             private val settings: Settings)
{

    private val DEFAULT_LANGUAGE = "en"

    private val defaultFile = createFilePath(DEFAULT_LANGUAGE)
    private var configuration: YamlConfiguration? = null

    @PostConstruct
    fun reload()
    {
        val language = settings.getProperty(PluginSettings.LOCALE)
        val fileName = createFilePath(language)
        val file = initializeFile(fileName)
        configuration = YamlConfiguration.loadConfiguration(file)
    }

    /**
     * Returns the message for the given key.
     *
     * @param key The key to retrieve the message for
     * @return The message
     */
    fun getMessage(key: String): String
    {
        val message = configuration?.getString(key)
        return if (message == null)
        {
            "Error retrieving message '$key'"
        } else
        {
            message
        }
    }

    /**
     * Creates the path to the messages file for the given language code.
     *
     * @param language The language code
     * @return Path to the message file for the given language
     */
    fun createFilePath(language: String): String = "messages/messages_$language.yml"

    /**
     * Copies the messages file from the JAR to the local "messages/" folder if it doesn't exist.
     *
     * @param filePath path to the messages file to use
     * @return the messages file to use
     */
    fun initializeFile(filePath: String): File?
    {
        val file = File(plugin.dataFolder, filePath)

        // Check that JAR file exists to avoid logging an error
        if (Utils.getResourceFromJar(filePath) != null && Utils.copyFileFromResource(file, filePath))
        {
            return file
        }

        if (Utils.copyFileFromResource(file, defaultFile))
        {
            return file
        } else
        {
            ConsoleLogger.warning("Wanted to copy default messages file '" + defaultFile
                    + "' from JAR but it didn't exist")
            return null
        }
    }
}
