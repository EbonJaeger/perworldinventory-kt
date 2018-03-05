package me.ebonjaeger.perworldinventory.conversion

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.atLeastOnce
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.junit.Test
import org.junit.runner.RunWith
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
        reset(convertService)
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

    private fun assertRanConvertWithPlayers(vararg players: OfflinePlayer)
    {
        argumentCaptor<Collection<OfflinePlayer>>().apply {
            verify(convertService, atLeastOnce()).executeConvert(capture())
            assertThat(lastValue.size, equalTo(players.size))
            //lastValue.forEach { assertThat(players, hasItem(it)) }
        }
    }
}
