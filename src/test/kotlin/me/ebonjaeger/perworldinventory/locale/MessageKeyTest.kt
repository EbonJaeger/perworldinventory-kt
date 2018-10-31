package me.ebonjaeger.perworldinventory.locale

import org.junit.Test
import kotlin.test.fail

/**
 * Tests for [MessageKey].
 */
class MessageKeyTest
{

    private val TAG_PATTERN = Regex("^%[a-z_]+$")

    @Test
    fun shouldHaveUniqueMessageKeys()
    {
        // given
        val messageKeys = MessageKey.values()
        val keys = HashSet<String>()

        // when/then
        for (messageKey in messageKeys)
        {
            val key = messageKey.getKey()

            if (!keys.add(key))
                fail("Found key '${messageKey.getKey()}' twice!")
            else if (key.isEmpty())
                fail("Key for message key '$messageKey' is empty")
        }
    }

    @Test
    fun shouldHaveWellFormedPlaceholders()
    {
        // given
        val messageKeys = MessageKey.values()

        // when/then
        for (key in messageKeys)
        {
            val tags = key.getTags()
            tags.forEach {
                if (!it.matches(TAG_PATTERN))
                    fail("Tag '$it' corresponds to valid format for key '$key'")
            }
        }
    }
}
