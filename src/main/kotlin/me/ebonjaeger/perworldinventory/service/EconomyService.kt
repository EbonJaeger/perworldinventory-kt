package me.ebonjaeger.perworldinventory.service

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.configuration.PlayerSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import me.ebonjaeger.perworldinventory.data.PlayerProfile
import net.milkbowl.vault.economy.Economy
import org.bukkit.Server
import org.bukkit.entity.Player
import javax.annotation.PostConstruct
import javax.inject.Inject

class EconomyService @Inject constructor(private val server: Server,
                                         private val settings: Settings) {

    /**
     * Economy from Vault, if Vault is present on the server. Null if Vault is not
     * installed, we failed to hook into it, or PWI is configured not to use economy.
     */
    private var economy: Economy? = null

    fun getBalance(player: Player): Double {
        return if (economy != null) {
            economy!!.getBalance(player)
        } else {
            0.0
        }
    }

    fun overridePlayerBalanceFromProfile(player: Player, profile: PlayerProfile) {
        val econ = economy
        if (econ != null) {
            econ.withdrawPlayer(player, econ.getBalance(player))
            econ.depositPlayer(player, profile.balance)
        }
    }

    fun withDrawMoneyFromPlayer(player: Player) {
        if (economy != null) {
            val amount = economy?.getBalance(player) ?: 0.0
            economy?.withdrawPlayer(player, amount)
        }
    }

    @PostConstruct
    fun tryLoadEconomy() {
        if (settings.getProperty(PlayerSettings.USE_ECONOMY) && server.pluginManager.getPlugin("Vault") != null) {
            ConsoleLogger.info("Vault found! Hooking into it...")

            val rsp = server.servicesManager.getRegistration(Economy::class.java)
            if (rsp != null) {
                economy = rsp.provider
                ConsoleLogger.info("Hooked into Vault!")
            } else {
                ConsoleLogger.warning("Unable to hook into Vault!")
            }
        }
    }
}