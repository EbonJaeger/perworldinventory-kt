package me.ebonjaeger.perworldinventory.data

import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.Utils
import org.bukkit.entity.Player

/**
 * Factory for creating PlayerProfile objects.
 *
 * @param plugin PerWorldInventory instance
 */
class ProfileFactory(private val plugin: PerWorldInventory)
{

    fun create(player: Player): PlayerProfile
    {
        var balance = 0.0
        if (plugin.econEnabled)
        {
            val econ = plugin.economy
            balance = econ!!.getBalance(player)
        }

        return if (Utils.checkServerVersion(plugin.server.version, 1, 9, 0))
        {
            PlayerProfile(player, balance, true)
        } else {
            PlayerProfile(player, balance, false)
        }
    }
}
