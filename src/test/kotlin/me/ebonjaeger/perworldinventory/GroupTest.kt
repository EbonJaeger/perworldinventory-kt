package me.ebonjaeger.perworldinventory

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.bukkit.GameMode
import org.junit.jupiter.api.Test

/**
 * Test for [Group].
 */
class GroupTest {

    @Test
    fun shouldCreateNewGroup() {
        // given
        val group = Group("test", mutableSetOf("world1", "world2"), GameMode.SURVIVAL, null)

        // when / then
        assertThat(group.containsWorld("world2"), equalTo(true))
        assertThat(group.containsWorld("other"), equalTo(false))
    }

    @Test
    fun shouldAddNewWorlds() {
        // given
        val group = Group("my_group", mutableSetOf("world1"), GameMode.ADVENTURE, null)

        // when
        group.addWorld("other")
        group.addWorlds(setOf("one", "two"))

        // then
        val existingWorlds = setOf("world1", "other", "one", "two")
        existingWorlds.forEach { world ->
            assertThat("World $world should be included", group.containsWorld(world), equalTo(true))
        }
        assertThat(group.containsWorld("bogus"), equalTo(false))
    }
}
