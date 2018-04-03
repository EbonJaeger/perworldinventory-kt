package me.ebonjaeger.perworldinventory.command

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import me.ebonjaeger.perworldinventory.Group
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.service.BukkitService
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.File
import kotlin.test.assertNull

/**
 * Tests for [GroupCommands].
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(Bukkit::class, BukkitService::class)
class GroupCommandsTest
{

    private val bukkitService = mock(BukkitService::class.java)
    private val groupManager = GroupManager(File(""), bukkitService)
    private val commands = GroupCommands(groupManager)

    @Before
    fun createMocks()
    {
        PowerMockito.mockStatic(Bukkit::class.java)
    }

    @Test
    fun addGroupButAlreadyExists()
    {
        // given
        val sender = mock(CommandSender::class.java)
        groupManager.addGroup("test", mutableSetOf("test"), GameMode.SURVIVAL)

        // when
        commands.onAddGroup(sender, "test", "survival", "test", "test_nether")

        // then
        verify(sender).sendMessage("§4» §7A group called 'test' already exists!")
        assertThat(groupManager.groups.size, equalTo(1))
    }

    @Test
    fun addGroupButInvalidGameMode()
    {
        // given
        val sender = mock(CommandSender::class.java)

        // when
        commands.onAddGroup(sender, "test", "invalid", "creative")

        // then
        verify(sender).sendMessage("§4» §7'invalid' is not a valid GameMode!")
        assertNull(groupManager.getGroup("test"))
    }

    @Test
    fun addGroupSuccessfully()
    {
        // given
        val sender = mock(CommandSender::class.java)

        // when
        commands.onAddGroup(sender, "test", "creative", "creative")

        // then
        val expected = Group("test", mutableSetOf("creative"), GameMode.CREATIVE)
        assertThat(groupManager.getGroup("test"), equalTo(expected))
    }

    @Test
    fun addWorldInvalidGroup()
    {
        // given
        val sender = mock(CommandSender::class.java)

        // when
        commands.onAddWorld(sender, "bob", "bobs_world")

        // then
        verify(sender).sendMessage("§4» §7No group with that name exists!")
    }

    @Test
    fun addWorldInvalidWorld()
    {
        // given
        val sender = mock(CommandSender::class.java)
        groupManager.addGroup("test", mutableSetOf("test"), GameMode.SURVIVAL)

        // when
        commands.onAddWorld(sender, "test", "invalid")

        // then
        verify(sender).sendMessage("§4» §7No world with that name exists!")
    }

    @Test
    fun addWorldSuccessfully()
    {
        // given
        val sender = mock(CommandSender::class.java)
        val world = mock(World::class.java)
        given(Bukkit.getWorld("bob")).willReturn(world)
        given(world.name).willReturn("bob")
        groupManager.addGroup("test", mutableSetOf("test"), GameMode.SURVIVAL)

        // when
        commands.onAddWorld(sender, "test", "bob")

        // then
        val expected = Group("test", mutableSetOf("test", "bob"), GameMode.SURVIVAL)
        assertThat(groupManager.getGroup("test"), equalTo(expected))
    }

    @Test
    fun removeGroupInvalidName()
    {
        // given
        val sender = mock(CommandSender::class.java)

        // when
        commands.onRemoveGroup(sender, "invalid")

        // then
        verify(sender).sendMessage("§4» §7No group with that name exists!")
    }

    @Test
    fun removeGroupSuccessfully()
    {
        // given
        val sender = mock(CommandSender::class.java)
        groupManager.addGroup("test", mutableSetOf("bob"), GameMode.ADVENTURE)

        // when
        commands.onRemoveGroup(sender, "test")

        // then
        assertNull(groupManager.getGroup("test"))
    }

    @Test
    fun removeWorldInvalidGroup()
    {
        // given
        val sender = mock(CommandSender::class.java)

        // when
        commands.onRemoveWorld(sender, "bob", "bobs_world")

        // then
        verify(sender).sendMessage("§4» §7No group with that name exists!")
    }

    @Test
    fun removeWorldInvalidWorld()
    {
        // given
        val sender = mock(CommandSender::class.java)
        groupManager.addGroup("test", mutableSetOf("test"), GameMode.SURVIVAL)

        // when
        commands.onRemoveWorld(sender, "test", "invalid")

        // then
        verify(sender).sendMessage("§4» §7No world with that name exists!")
    }

    @Test
    fun removeWorldSuccessfully()
    {
        // given
        val sender = mock(CommandSender::class.java)
        val world = mock(World::class.java)
        given(Bukkit.getWorld("bob")).willReturn(world)
        given(world.name).willReturn("bob")
        groupManager.addGroup("test", mutableSetOf("test", "bob"), GameMode.SURVIVAL)

        // when
        commands.onRemoveWorld(sender, "test", "bob")

        // then
        val expected = Group("test", mutableSetOf("test"), GameMode.SURVIVAL)
        assertThat(groupManager.getGroup("test"), equalTo(expected))
    }
}
