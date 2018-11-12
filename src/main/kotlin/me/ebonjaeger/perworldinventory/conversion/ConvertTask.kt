package me.ebonjaeger.perworldinventory.conversion

import me.ebonjaeger.perworldinventory.ConsoleLogger
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

/**
 * Task to convert player data from MultiVerse-Inventories to PWI.
 *
 * @property convertService The [ConvertService] running this task.
 * @property sender The [CommandSender] that started the conversion.
 * @property offlinePlayers All [OfflinePlayer]s on the server.
 */
class ConvertTask (private val convertService: ConvertService,
                   private val sender: CommandSender,
                   private val offlinePlayers: Array<out OfflinePlayer>) : BukkitRunnable()
{

    private val CONVERTS_PER_TICK = 5

    private var currentPage = 0

    override fun run()
    {
        val stopIndex = currentPage * CONVERTS_PER_TICK + CONVERTS_PER_TICK
        var currentIndex = currentPage * CONVERTS_PER_TICK

        if (currentIndex >= offlinePlayers.size)
        {
            finish()
            return
        }

        val playersInPage = mutableSetOf<OfflinePlayer>()
        while (currentIndex < stopIndex && currentIndex < offlinePlayers.size)
        {
            playersInPage.add(offlinePlayers[currentIndex])
            currentIndex++
        }

        currentPage++

        convertService.executeConvert(playersInPage)
        if (currentPage % 20 == 0)
        {
            ConsoleLogger.info(
                    "${ChatColor.BLUE}» ${ChatColor.GRAY}Convert progress: $stopIndex/${offlinePlayers.size}")
        }
    }

    private fun finish()
    {
        cancel()

        if (sender !is ConsoleCommandSender) {
            ConsoleLogger.info(
                    "${ChatColor.BLUE}» ${ChatColor.GRAY}Conversion has been completed! Disabling MultiVerse-Inventories!")
        }
        if (sender is Player && sender.isOnline) {
            sender.sendMessage(
                    "${ChatColor.BLUE}» ${ChatColor.GRAY}Conversion has been completed! Disabling MultiVerse-Inventories!")
        }

        convertService.finish()

        convertService.converting = false
    }
}
