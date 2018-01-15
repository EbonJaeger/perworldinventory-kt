package me.ebonjaeger.perworldinventory

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import me.ebonjaeger.perworldinventory.TestHelper.mockGroup
import me.ebonjaeger.perworldinventory.service.BukkitService
import org.bukkit.GameMode
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.File

/**
 * Test for [GroupManager].
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(BukkitService::class)
class GroupManagerTest {

    private val bukkitService = Mockito.mock(BukkitService::class.java)

    private val groupManager = GroupManager(File(""), bukkitService)

    @Test
    fun shouldReturnAbsentValueForNonExistentGroup() {
        assertThat(groupManager.getGroup("test"), absent())
    }

    @Test
    fun addGroupWithLowercaseName()
    {
        // given
        groupManager.groups.clear()
        val name = "test"
        val worlds = mutableSetOf(name)
        val gameMode = GameMode.SURVIVAL

        // when
        groupManager.addGroup(name, worlds, gameMode)

        // then
        val expected = mockGroup(name, worlds, gameMode)
        val actual = groupManager.getGroup(name)

        assertThat(actual, equalTo(expected))
    }

    @Test
    fun addGroupWithUppercaseName()
    {
        // given
        groupManager.groups.clear()
        val name = "TeSt"
        val worlds = mutableSetOf(name)
        val gameMode = GameMode.SURVIVAL

        // when
        groupManager.addGroup(name, worlds, gameMode)

        // then
        val expected = mockGroup(name, worlds, gameMode)
        val actual = groupManager.getGroup(name)

        assertThat(actual, equalTo(expected))
    }

    @Test
    fun addGroupWithUppercaseNameLowercaseGet()
    {
        // given
        groupManager.groups.clear()
        val name = "TeSt"
        val worlds = mutableSetOf(name)
        val gameMode = GameMode.SURVIVAL

        // when
        groupManager.addGroup(name, worlds, gameMode)

        // then
        val expected = mockGroup(name, worlds, gameMode)
        val actual = groupManager.getGroup(name.toLowerCase())

        assertThat(actual, equalTo(expected))
    }

    @Test
    fun getGroupFromWorldWhereExists()
    {
        // given
        groupManager.groups.clear()
        val name = "test"
        val worlds = mutableSetOf(name)
        val gameMode = GameMode.SURVIVAL
        groupManager.addGroup(name, worlds, gameMode)

        // when
        val result = groupManager.getGroupFromWorld(name)

        // then
        val expected = mockGroup(name, worlds, gameMode)

        assertThat(result, equalTo(expected))
    }

    @Test
    fun getGroupFromWorldWhereNotExists()
    {
        // given
        groupManager.groups.clear()
        val name = "test"
        val worlds = mutableSetOf(name, "${name}_nether", "${name}_the_end")
        val gameMode = GameMode.SURVIVAL

        // when
        val result = groupManager.getGroupFromWorld(name)

        // then
        val expected = mockGroup(name, worlds, gameMode)

        assertNotNull(result)
        assertThat(result, equalTo(expected))
    }

    @Test
    fun getGroupAfterCreatedFromGroupFromWorldMethod()
    {
        // given
        groupManager.groups.clear()
        val name = "test"
        val expected = groupManager.getGroupFromWorld(name)

        // when
        val result = groupManager.getGroup(name)

        // then
        assertNotNull(result)
        assertThat(result, equalTo(expected))
    }
}
