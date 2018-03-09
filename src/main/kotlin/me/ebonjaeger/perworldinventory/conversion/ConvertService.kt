package me.ebonjaeger.perworldinventory.conversion

import ch.jalu.injector.annotations.NoMethodScan
import com.onarandombox.multiverseinventories.MultiverseInventories
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.service.BukkitService
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.plugin.PluginManager
import javax.inject.Inject

/**
 * Initiates conversion tasks.
 */
@NoMethodScan
class ConvertService @Inject constructor(private val bukkitService: BukkitService,
                                         private val convertExecutor: ConvertExecutor,
                                         private val groupManager: GroupManager,
                                         private val pluginManager: PluginManager)
{

    /**
     * Conversion status.
     *
     * If this is true, then a conversion is currently in progress,
     * and another conversion cannot be started.
     */
    var converting = false

    fun runConversion(sender: CommandSender, mvinventory: MultiverseInventories)
    {
        val offlinePlayers = bukkitService.getOfflinePlayers()
        convertPlayers(sender, offlinePlayers, mvinventory)
    }

    private fun convertPlayers(sender: CommandSender,
                               offlinePlayers: Array<out OfflinePlayer>,
                               mvinventory: MultiverseInventories)
    {
        if (converting)
        {
            sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}" +
                    "A conversion is already in progress!")
            return
        }

        converting = true
        val mvGroups = mvinventory.groupManager.groups
        convertExecutor.mvGroups = mvGroups

        mvGroups.forEach {
            // Ensure that the group exists first, otherwise you get nulls down the road
            val pwiGroup = groupManager.getGroup(it.name)
            val worlds = it.worlds

            if (pwiGroup == null)
            {
                groupManager.addGroup(it.name, worlds, GameMode.SURVIVAL)
            } else
            {
                pwiGroup.addWorlds(worlds)
            }
        }

        val task = ConvertTask(this, sender, offlinePlayers)
        bukkitService.runRepeatingTaskAsynchronously(task, 0, 1)
    }

    fun disableMVI()
    {
        val mvinventory = pluginManager.getPlugin("Multiverse-Inventories")
        if (mvinventory != null && pluginManager.isPluginEnabled(mvinventory))
        {
            pluginManager.disablePlugin(mvinventory)
        }
    }

    fun executeConvert(batch: Collection<OfflinePlayer>)
    {
        batch.forEach(convertExecutor::executeConvert)
    }
}
