package me.ebonjaeger.perworldinventory

import org.bukkit.entity.Player

/**
 * Factory for creating PlayerInfo objects.
 *
 * @param plugin PerWorldInventory instance
 */
class PlayerFactory(private val plugin: PerWorldInventory)
{

    fun create(player: Player, group: Group): PlayerInfo
    {
        var balance = 0.0
        if (plugin.econEnabled)
        {
            val econ = plugin.economy
            balance = econ!!.getBalance(player)
        }

        return if (Utils.checkServerVersion(plugin.server.version, 1, 9, 0))
        {
            PlayerInfo(player, group, balance, true)
        } else {
            PlayerInfo(player, group, balance, false)
        }
    }
}