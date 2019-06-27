package me.ebonjaeger.perworldinventory.command

import com.nhaarman.mockito_kotlin.given
import io.mockk.*
import me.ebonjaeger.perworldinventory.Group
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.TestHelper.mockGroup
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

/**
 * Tests for [GroupCommands].
 */
@PrepareForTest(Bukkit::class)
@RunWith(PowerMockRunner::class)
class GroupCommandsTest
{

    private var groupManager = classMockk(GroupManager::class)
    private val commands = GroupCommands(groupManager)

    @BeforeTest
    fun setupMocks()
    {
        mockStatic(Bukkit::class.java)

        every { groupManager.addGroup(any(), any(), any(), any()) } just runs
        every { groupManager.removeGroup(any()) } just runs
        every { groupManager.saveGroups() } just runs
    }

    @Test
    fun addGroupButAlreadyExists()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        val group = Group("test", mutableSetOf(), GameMode.SURVIVAL, null)
        every { groupManager.getGroup("test") } returns group

        // when
        commands.onAddGroup(sender, "test", "survival", "test", "test_nether")

        // then
        verify(inverse = true) { groupManager.addGroup(any(), any(), any(), any()) }
    }

    @Test
    fun addGroupButInvalidGameMode()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("test") } returns null

        // when
        commands.onAddGroup(sender, "test", "invalid", "creative")

        // then
        verify(inverse = true) { groupManager.addGroup(any(), any(), any(), any()) }
    }

    @Test
    fun addGroupSuccessfully()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("test") } returns null

        // when
        commands.onAddGroup(sender, "test", "creative", "creative")

        // then
        verify { groupManager.addGroup("test", mutableSetOf("creative"), GameMode.CREATIVE, true) }
    }

    @Test
    fun addWorldInvalidGroup()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("bob") } returns null

        // when
        commands.onAddWorld(sender, "bob", "bobs_world")

        // then
        verify(inverse = true) { groupManager.saveGroups() }
    }

    @Test
    fun addWorldInvalidWorld()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("test") } returns mockGroup("test")
        given(Bukkit.getWorld("invalid")).willReturn(null)

        // when
        commands.onAddWorld(sender, "test", "invalid")

        // then
        verify(inverse = true) { groupManager.saveGroups() }
    }

    @Test
    fun addWorldSuccessfully()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        val world = mockk<World>(relaxed = true)
        val group = mockGroup("test", mutableSetOf("test"))

        given(Bukkit.getWorld("bob")).willReturn(world)

        every { world.name } returns "bob"
        every { groupManager.getGroup("test") } returns group

        // when
        commands.onAddWorld(sender, "test", "bob")

        // then
        val expected = Group("test", mutableSetOf("test", "bob"), GameMode.SURVIVAL, null)
        assertEquals(expected, group)
        verify { groupManager.saveGroups() }
    }

    @Test
    fun removeGroupInvalidName()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("invalid") } returns null

        // when
        commands.onRemoveGroup(sender, "invalid")

        // then
        verify(inverse = true) { groupManager.removeGroup(any()) }
        verify(inverse = true) { groupManager.saveGroups() }
    }

    @Test
    fun removeGroupSuccessfully()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("test") } returns mockGroup("test")

        // when
        commands.onRemoveGroup(sender, "test")

        // then
        verify { groupManager.removeGroup("test") }
        verify { groupManager.saveGroups() }
    }

    @Test
    fun removeWorldInvalidGroup()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("bob") } returns null

        // when
        commands.onRemoveWorld(sender, "bob", "bobs_world")

        // then
        verify(inverse = true) { groupManager.saveGroups() }
    }

    @Test
    fun removeWorldInvalidWorld()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("test") } returns mockGroup("test")
        given(Bukkit.getWorld("invalid")).willReturn(null)

        // when
        commands.onRemoveWorld(sender, "test", "invalid")

        // then
        verify(inverse = true) { groupManager.saveGroups() }
    }

    @Test
    fun removeWorldSuccessfully()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        val world = mockk<World>(relaxed = true)
        val group = mockGroup("test", mutableSetOf("test", "bob"))

        given(Bukkit.getWorld("bob")).willReturn(world)

        every { world.name } returns "bob"
        every { groupManager.getGroup("test") } returns group

        // when
        commands.onRemoveWorld(sender, "test", "bob")

        // then
        val expected = Group("test", mutableSetOf("test"), GameMode.SURVIVAL, null)
        assertEquals(expected, group)
        verify { groupManager.saveGroups() }
    }

    @Test
    fun setRespawnInvalidGroup()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("bob") } returns null

        // when
        commands.onSetRespawnWorld(sender, "bob", "bobs_world")

        // then
        verify(inverse = true) { groupManager.saveGroups() }
    }

    @Test
    fun setRespawnInvalidWorld()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("test") } returns mockGroup("test")
        given(Bukkit.getWorld("invalid")).willReturn(null)

        // when
        commands.onSetRespawnWorld(sender, "test", "invalid")

        // then
        verify(inverse = true) { groupManager.saveGroups() }
    }

    @Test
    fun setRespawnSuccessfully()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        val world = mockk<World>(relaxed = true)
        val group = mockGroup("test", mutableSetOf("test", "bob"))

        given(Bukkit.getWorld("bob")).willReturn(world)

        every { world.name } returns "bob"
        every { groupManager.getGroup("test") } returns group

        // when
        commands.onSetRespawnWorld(sender, "test", "bob")

        // then
        val expected = Group("test", mutableSetOf("test", "bob"), GameMode.SURVIVAL, "bob")
        assertEquals(expected, group)
        verify { groupManager.saveGroups() }
    }
}
