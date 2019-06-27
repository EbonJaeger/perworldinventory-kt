package me.ebonjaeger.perworldinventory

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

/**
 * Tests for [Utils].
 */
class UtilsTest
{

    val VERSION_1_8_8 = "git-Spigot-8a048fe-3c19fef (MC: 1.8.8)"
    val VERSION_1_9 = "git-Spigot-8a048fe-3c19fef (MC: 1.9)"
    val VERSION_1_9_2 = "git-Spigot-8a048fe-3c19fef (MC: 1.9.2)"
    val VERSION_1_9_4 = "git-Spigot-8a048fe-3c19fef (MC: 1.9.4)"
    val VERSION_1_10 = "git-Spigot-8a048fe-3c19fef (MC: 1.10)"
    val VERSION_1_10_2 = "git-Spigot-8a048fe-3c19fef (MC: 1.10.2)"

    @Test
    fun shouldReturnTrueSameMinorVersion()
    {
        // given/when
        val result = Utils.checkServerVersion(VERSION_1_9, 1, 9, 0)

        // then
        assertThat(result, equalTo(true))
    }

    @Test
    fun shouldReturnTrueSameMinorSamePatchVersion()
    {
        // given/when
        val result = Utils.checkServerVersion(VERSION_1_9_2, 1, 9, 2)

        // then
        assertThat(result, equalTo(true))
    }

    @Test
    fun shouldReturnTrueSameMinorGreatorPatchVersion()
    {
        // given/when
        val result = Utils.checkServerVersion(VERSION_1_9_4, 1, 9, 2)

        // then
        assertThat(result, equalTo(true))
    }

    @Test
    fun shouldReturnTrueHigherMinorVersion()
    {
        // given/when
        val result = Utils.checkServerVersion(VERSION_1_10, 1, 9, 2)

        // then
        assertThat(result, equalTo(true))
    }

    @Test
    fun shouldReturnFalseLowerMinorVersion()
    {
        // given/when
        val result = Utils.checkServerVersion(VERSION_1_8_8, 1, 9, 2)

        // then
        assertThat(result, equalTo(false))
    }

    @Test
    fun shouldReturnFalseSameMinorLowerPatchVersion()
    {
        // given/when
        val result = Utils.checkServerVersion(VERSION_1_9_2, 1, 9, 4)

        // then
        assertThat(result, equalTo(false))
    }
}
