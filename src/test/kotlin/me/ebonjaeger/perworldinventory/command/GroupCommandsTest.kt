package me.ebonjaeger.perworldinventory.command

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.runs
import io.mockk.verify
import me.ebonjaeger.perworldinventory.Group
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.TestHelper
import me.ebonjaeger.perworldinventory.TestHelper.mockGroup
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Tests for [GroupCommands].
 */
class GroupCommandsTest
{

    private var groupManager = mockkClass(GroupManager::class)
    private val commands = GroupCommands(groupManager)

    @BeforeEach
    fun setupMocks()
    {
        TestHelper.mockBukkit()

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
        verify(exactly = 0) { groupManager.addGroup(any(), any(), any(), any()) }
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
        verify(exactly = 0) { groupManager.addGroup(any(), any(), any(), any()) }
    }

    @Test
    fun addGroupSuccessfully() {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("test") } returns null
        every { groupManager.addGroup(any(), any(), any(), any()) } just runs
        every { groupManager.saveGroups() } just runs

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
        verify(exactly = 0) { groupManager.saveGroups() }
    }

    @Test
    fun addWorldInvalidWorld() {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("test") } returns mockGroup("test")
        every { Bukkit.getWorld("invalid") } returns null

        // when
        commands.onAddWorld(sender, "test", "invalid")

        // then
        verify(exactly = 0) {
            groupManager.saveGroups()
        }
    }

    @Test
    fun addWorldSuccessfully()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        val world = mockk<World>(relaxed = true)
        val group = mockGroup("test", mutableSetOf("test"))

        every { Bukkit.getWorld("bob") } returns world
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
    fun removeGroupInvalidName() {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("invalid") } returns null

        // when
        commands.onRemoveGroup(sender, "invalid")

        // then
        verify(exactly = 0) {
            groupManager.removeGroup(any())
            groupManager.saveGroups()
        }
    }

    @Test
    fun removeGroupSuccessfully() {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("test") } returns mockGroup("test")
        every { groupManager.removeGroup(any()) } just runs
        every { groupManager.saveGroups() } just runs

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
        verify(exactly = 0) { groupManager.saveGroups() }
    }

    @Test
    fun removeWorldInvalidWorld()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("test") } returns mockGroup("test")
        every { Bukkit.getWorld("invalid") } returns null

        // when
        commands.onRemoveWorld(sender, "test", "invalid")

        // then
        verify(exactly = 0) { groupManager.saveGroups() }
    }

    @Test
    fun removeWorldSuccessfully()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        val world = mockk<World>(relaxed = true)
        val group = mockGroup("test", mutableSetOf("test", "bob"))

        every { Bukkit.getWorld("bob") } returns world
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
        verify(exactly = 0) { groupManager.saveGroups() }
    }

    @Test
    fun setRespawnInvalidWorld()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        every { groupManager.getGroup("test") } returns mockGroup("test")
        every { Bukkit.getWorld("invalid") } returns null

        // when
        commands.onSetRespawnWorld(sender, "test", "invalid")

        // then
        verify(exactly = 0) { groupManager.saveGroups() }
    }

    @Test
    fun setRespawnSuccessfully()
    {
        // given
        val sender = mockk<CommandSender>(relaxed = true)
        val world = mockk<World>(relaxed = true)
        val group = mockGroup("test", mutableSetOf("test", "bob"))

        every { Bukkit.getWorld("bob") } returns world
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
