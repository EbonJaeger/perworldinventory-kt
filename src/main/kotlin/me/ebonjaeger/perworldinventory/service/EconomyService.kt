package me.ebonjaeger.perworldinventory.service

import ch.jalu.injector.annotations.NoFieldScan
import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.configuration.PlayerSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import net.milkbowl.vault.economy.Economy
import org.bukkit.Server
import org.bukkit.entity.Player
import javax.annotation.PostConstruct
import javax.inject.Inject

@NoFieldScan
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

    /**
     * Set a player's balance by calculating the difference of the old and new amounts.
     * This avoids having to set a player's balance to 0 each time. If for some reason
     * the difference ends up being a negative number and the economy plugin in use
     * does not permit negative balances, the player's balance will be set to 0.
     *
     * @param player The [Player] in the transaction.
     * @param newBalance The end balance that the player should end up with.
     */
    fun setNewBalance(player: Player, newBalance: Double) {
        val econ = economy
        if (econ != null)
        {
            val oldBalance = econ.getBalance(player)

            if (newBalance < oldBalance)
            {
                // If the new bal is less than old bal, withdraw the difference
                val response = econ.withdrawPlayer(player, (oldBalance - newBalance))
                if (!response.transactionSuccess())
                {
                    if (response.errorMessage.equals("Loan was not permitted", true))
                    {
                        ConsoleLogger.warning("[ECON] Negative balances are not permitted. Setting balance for '${player.name}'" +
                                " to 0 in '${player.location.world.name}'")
                        econ.withdrawPlayer(player, oldBalance)
                    }
                }
            } else
            {
                // If the new bal is greater than old bal, deposit the difference
                econ.depositPlayer(player, (newBalance - oldBalance))
            }
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
