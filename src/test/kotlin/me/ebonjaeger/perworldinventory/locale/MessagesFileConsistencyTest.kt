package me.ebonjaeger.perworldinventory.locale

import me.ebonjaeger.perworldinventory.TestHelper
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.test.Test
import kotlin.test.fail

/**
 * Tests that the project's default language file contains a text for all message keys.
 * <p>
 * Translators can change the file name in [MESSAGES_FILE] to validate their translation.
 */
class MessagesFileConsistencyTest
{

    private val MESSAGES_FILE = "/messages/messages_en.yml"

    @Test
    fun shouldHaveAllMessages()
    {
        val file = TestHelper.getFromJar(MESSAGES_FILE)
        val configuration = YamlConfiguration.loadConfiguration(file)
        val errors = mutableListOf<String>()

        for (messageKey in MessageKey.values())
        {
            validateMessage(messageKey, configuration, errors)
        }

        if (!errors.isEmpty())
        {
            fail("Validation errors in $MESSAGES_FILE:\n- " + errors.joinToString("\n- "))
        }
    }

    private fun validateMessage(messageKey: MessageKey, configuration: FileConfiguration, errors: MutableList<String>)
    {
        val key = messageKey.getKey()
        val message = configuration.getString(key)

        if (message.isEmpty())
        {
            errors.add("Messages file should have message for key '$key'")
            return
        }

        val missingTags = mutableListOf<String>()
        for (tag in messageKey.getTags())
        {
            if (!message.contains(tag)) missingTags.add(tag)
        }

        if (!missingTags.isEmpty())
        {
            val pluralS = if (missingTags.size > 1) "s" else ""
            errors.add("Message with key '%s' missing tag%s: %s".format(key, pluralS, missingTags.joinToString(", ")))
        }
    }
}
