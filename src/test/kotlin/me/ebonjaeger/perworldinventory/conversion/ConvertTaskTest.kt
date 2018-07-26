package me.ebonjaeger.perworldinventory.conversion

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.*
import me.ebonjaeger.perworldinventory.ReflectionUtils
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.hamcrest.MockitoHamcrest.argThat
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * Tests for [ConvertTask].
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(ConvertService::class)
class ConvertTaskTest
{

    private val convertService = mock(ConvertService::class.java)

    @Test
    fun shouldRunTask()
    {
        // given
        val players = arrayOf(
                mock(OfflinePlayer::class.java), mock(OfflinePlayer::class.java), mock(OfflinePlayer::class.java),
                mock(OfflinePlayer::class.java), mock(OfflinePlayer::class.java), mock(OfflinePlayer::class.java))

        val task = ConvertTask(convertService, mock(CommandSender::class.java), players)

        // when (1 - first run, 5 players per run)
        task.run()

        // then (1)
        // In the first run, only the first five should be converted
        assertRanConvertWithPlayers(players[0], players[1], players[2], players[3], players[4])

        // when (2)
        reset(convertService)
        task.run()

        // then (2)
        // The last player should now be converted
        assertRanConvertWithPlayers(players[5])
    }

    @Test
    fun shouldStopAndInformOnComplete()
    {
        // given
        val sender = mock(CommandSender::class.java)
        val players = emptyArray<OfflinePlayer>()
        val task = ConvertTask(convertService, sender, players)
        val bTask = mock(BukkitTask::class.java)

        ReflectionUtils.setField(BukkitRunnable::class, task, "task", bTask)
        val server = mock(Server::class.java)
        val scheduler = mock(BukkitScheduler::class.java)
        given(server.scheduler).willReturn(scheduler)
        ReflectionUtils.setField(Bukkit::class, null, "server", server)

        // when
        task.run()

        // then
        verify(scheduler).cancelTask(task.taskId)
        verify(sender).sendMessage(argThat(containsString("Conversion has been completed!")))
    }

    private fun assertRanConvertWithPlayers(vararg players: OfflinePlayer)
    {
        argumentCaptor<Collection<OfflinePlayer>>().apply {
            verify(convertService, atLeastOnce()).executeConvert(capture())
            assertThat(lastValue.size, equalTo(players.size))
            lastValue.forEach { assert(players.contains(it)) }
        }
    }
}
