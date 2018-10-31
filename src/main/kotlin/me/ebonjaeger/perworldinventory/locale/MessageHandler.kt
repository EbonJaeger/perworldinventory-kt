package me.ebonjaeger.perworldinventory.locale

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.configuration.Settings
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import javax.inject.Inject

class MessageHandler @Inject constructor(private val plugin: PerWorldInventory,
                                         private val fileHandler: MessageFileHandler,
                                         private val settings: Settings)
{

    fun sendMessage(sender: CommandSender, type: MessageType, key: MessageKey)
    {
        val prefix = type.getPrefix()
        val message = retrieveMessage(key)
        sender.sendMessage(prefix + message)
    }

    fun sendMessage(sender: CommandSender, type: MessageType, key: MessageKey, vararg replacements: String)
    {
        val prefix = type.getPrefix()
        val message = retrieveWithReplacements(key, *replacements)
        sender.sendMessage(prefix + message)
    }

    /**
     * Retrieve the message from the text file.
     *
     * @param key The message key to retrieve
     * @return The message from the file
     */
    private fun retrieveMessage(key: MessageKey): String
    {
        val message = fileHandler.getMessage(key.getKey())
        return ChatColor.translateAlternateColorCodes('&', message)
    }

    /**
     * Retrieve the given message code with the given tag replacements. Note that this method
     * logs a warning if the number of supplied replacements doesn't correspond to the number
     * of tags the message key contains.
     *
     * @param key The key of the message to send
     * @param replacements The replacements to apply for the tags
     * @return The message from the file with replacements
     */
    private fun retrieveWithReplacements(key: MessageKey, vararg replacements: String): String
    {
        var message = retrieveMessage(key)
        val tags = key.getTags()

        if (tags.size == replacements.size)
        {
            for (i in 0 until tags.size)
            {
                message = message.replace(tags[i], replacements[i])
            }
        } else
        {
            ConsoleLogger.warning("Invalid number of replacements for message key '$key'")
        }

        return ChatColor.translateAlternateColorCodes('&', message)
    }
}
