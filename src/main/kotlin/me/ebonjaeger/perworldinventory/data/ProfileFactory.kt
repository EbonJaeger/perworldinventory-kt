package me.ebonjaeger.perworldinventory.data

import me.ebonjaeger.perworldinventory.service.BukkitService
import org.bukkit.entity.Player

/**
 * Factory for creating PlayerProfile objects.
 *
 * @param bukkitService [BukkitService] instance
 */
class ProfileFactory(private val bukkitService: BukkitService)
{

    fun create(player: Player): PlayerProfile
    {
        var balance = 0.0
        if (bukkitService.isEconEnabled())
        {
            val econ = bukkitService.getEconomy()
            balance = econ!!.getBalance(player)
        }

        return if (bukkitService.shouldUseAttributes())
        {
            PlayerProfile(player, balance, true)
        } else {
            PlayerProfile(player, balance, false)
        }
    }
}
