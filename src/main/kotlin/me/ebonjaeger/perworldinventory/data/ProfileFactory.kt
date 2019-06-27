package me.ebonjaeger.perworldinventory.data

import me.ebonjaeger.perworldinventory.service.BukkitService
import me.ebonjaeger.perworldinventory.service.EconomyService
import org.bukkit.entity.Player
import javax.inject.Inject

/**
 * Factory for creating PlayerProfile objects.
 *
 * @param bukkitService [BukkitService] instance
 * @param economyService [EconomyService] instance
 */
class ProfileFactory @Inject constructor(private val bukkitService: BukkitService,
                                         private val economyService: EconomyService)
{

    fun create(player: Player): PlayerProfile
    {
        val balance = economyService.getBalance(player)
        return PlayerProfile(player, balance)
    }
}
