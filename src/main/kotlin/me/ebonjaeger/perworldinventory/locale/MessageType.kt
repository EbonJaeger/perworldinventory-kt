package me.ebonjaeger.perworldinventory.locale

import org.bukkit.ChatColor

/**
 * Represents the types of messages that the plugin sends.
 *
 * Each message type as a different colored prefix.
 */
enum class MessageType(private val prefix: String)
{

    /**
     * General informational message.
     */
    INFO("${ChatColor.BLUE}» ${ChatColor.GRAY}"),

    /**
     * Message indicating an action was successful.
     */
    SUCCESS("${ChatColor.GREEN}» ${ChatColor.GRAY}"),

    /**
     * Message indicating that something resulted in an error, and/or was not successful.
     */
    ERROR("${ChatColor.DARK_RED}» ${ChatColor.GRAY}");

    /**
     * Get the prefix color for the message type.
     *
     * @return The color as a [ChatColor] string.
     */
    fun getPrefix(): String
    {
        return prefix
    }
}
