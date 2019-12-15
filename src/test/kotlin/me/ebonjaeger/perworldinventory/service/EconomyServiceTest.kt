package me.ebonjaeger.perworldinventory.service

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.present
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkClass
import io.mockk.verify
import me.ebonjaeger.perworldinventory.configuration.PlayerSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import me.ebonjaeger.perworldinventory.data.PlayerProfile
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.ServicesManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Test for [EconomyService].
 */
@ExtendWith(MockKExtension::class)
class EconomyServiceTest {

    @InjectMockKs
    private lateinit var economyService: EconomyService

    @MockK
    private lateinit var server: Server
    @MockK
    private lateinit var settings: Settings
    @MockK
    private lateinit var pluginManager: PluginManager
    @MockK
    private lateinit var servicesManager: ServicesManager
    @MockK
    private lateinit var registeredServiceProvider: RegisteredServiceProvider<Economy>
    @MockK
    private lateinit var economy: Economy
    @MockK
    private lateinit var playerProfile: PlayerProfile

    @BeforeEach
    fun wireUpMocks() {
        MockKAnnotations.init(this)

        every { server.servicesManager } returns servicesManager
        every { server.pluginManager } returns pluginManager
        every { pluginManager.getPlugin("Vault") } returns mockkClass(Plugin::class)
        every { servicesManager.getRegistration(Economy::class.java) } returns registeredServiceProvider
        every { registeredServiceProvider.provider } returns economy
    }

    @Test
    fun shouldInitializeEconomy() {
        // given
        every { settings.getProperty(PlayerSettings.USE_ECONOMY) } returns true

        // when
        economyService.tryLoadEconomy()

        // then
        assertThat(getEconomyField(), present())
    }

    @Test
    fun shouldNotInitializeEconomyIfSoConfigured() {
        // given
        every { settings.getProperty(PlayerSettings.USE_ECONOMY) } returns false

        // when
        economyService.tryLoadEconomy()

        // then
        //assertThat(getEconomyField(), absent())
        verify {
            servicesManager wasNot called
            registeredServiceProvider wasNot called
        }
    }

    @Test
    fun shouldWithdrawFunds() {
        // given
        every { settings.getProperty(PlayerSettings.USE_ECONOMY) } returns true
        economyService.tryLoadEconomy()

        val player = mockkClass(Player::class)
        every { player.name } returns "Bob"
        every { economy.getBalance(player) } returns 320.0
        val er = EconomyResponse(320.0, 0.0, EconomyResponse.ResponseType.SUCCESS, "Woo?")
        every { economy.withdrawPlayer(player, 320.0) } returns er

        // when
        economyService.withdrawFromPlayer(player)

        // then
        verify { economy.withdrawPlayer(player, 320.0) }
    }

    @Test
    fun newBalanceGreaterThanOldBalance() {
        // given
        every { settings.getProperty(PlayerSettings.USE_ECONOMY) } returns true
        economyService.tryLoadEconomy()

        val player = mockkClass(Player::class)
        val oldBalance = 2.81
        val newBalance = 1332.49
        every { player.name } returns "Bob"
        every { economy.getBalance(player) } returns oldBalance
        every { playerProfile.balance } returns newBalance
        val er = EconomyResponse((newBalance - oldBalance), newBalance, EconomyResponse.ResponseType.SUCCESS, "Woo?")
        every { economy.depositPlayer(player, (newBalance - oldBalance)) } returns er

        // when
        economyService.setNewBalance(player, playerProfile.balance)

        // then
        verify { economy.depositPlayer(player, (newBalance - oldBalance)) }
    }

    @Test
    fun newBalanceLessThanOldBalance() {
        // given
        every { settings.getProperty(PlayerSettings.USE_ECONOMY) } returns true
        economyService.tryLoadEconomy()

        val player = mockkClass(Player::class)
        val oldBalance = 1332.49
        val newBalance = 2.81
        val er = EconomyResponse((oldBalance - newBalance), newBalance, EconomyResponse.ResponseType.SUCCESS, "Woo?")
        every { economy.getBalance(player) } returns oldBalance
        every { playerProfile.balance } returns newBalance
        every { player.name } returns "Bob"
        every { economy.withdrawPlayer(player, (oldBalance - newBalance)) } returns er

        // when
        economyService.setNewBalance(player, playerProfile.balance)

        // then
        verify { economy.withdrawPlayer(player, (oldBalance - newBalance)) }
    }

    private fun getEconomyField(): Economy? {
        return economyService::class.java.getDeclaredField("economy").let {
            it.isAccessible = true
            return@let it.get(economyService) as Economy?
        }
    }
}
