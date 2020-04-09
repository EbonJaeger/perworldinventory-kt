package me.ebonjaeger.perworldinventory.conversion

import ch.jalu.injector.annotations.NoMethodScan
import com.onarandombox.multiverseinventories.MultiverseInventories
import com.onarandombox.multiverseinventories.WorldGroup
import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.initialization.DataDirectory
import me.ebonjaeger.perworldinventory.service.BukkitService
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.PluginManager
import java.io.File
import javax.inject.Inject

/**
 * Initiates conversion tasks.
 */
@NoMethodScan
class ConvertService @Inject constructor(private val plugin: PerWorldInventory,
                                         private val bukkitService: BukkitService,
                                         private val groupManager: GroupManager,
                                         private val pluginManager: PluginManager,
                                         @DataDirectory private val dataDirectory: File) {

    private var converting = false
    private var sender: CommandSender? = null

    fun isConverting(): Boolean {
        return converting
    }

    fun beginConverting(sender: CommandSender, mvInventory: MultiverseInventories) {
        val offlinePlayers = bukkitService.getOfflinePlayers()

        if (isConverting()) {
            return
        }

        this.sender = sender

        if (sender !is ConsoleCommandSender) { // No need to send a message to console when console did the command
            ConsoleLogger.info("Beginning conversion from MultiVerse-Inventories.")
        }

        converting = true

        val groups = mvInventory.groupManager.groups
        convertGroups(groups)

        val task = ConvertTask(this, groupManager, sender, offlinePlayers, groups, dataDirectory)
        task.runTaskTimerAsynchronously(plugin, 0, 20)
    }

    fun finishConversion(converted: Int) {
        converting = false

        val mvInventory = pluginManager.getPlugin("Multiverse-Inventories")
        if (mvInventory != null && pluginManager.isPluginEnabled(mvInventory)) {
            pluginManager.disablePlugin(mvInventory)
        }

        ConsoleLogger.info("Data conversion has been completed! Converted $converted profiles.")
        if (sender != null && sender is Player) {
            if ((sender as Player).isOnline) {
                (sender as Player).sendMessage("${ChatColor.GREEN}» ${ChatColor.GRAY}Data conversion has been completed!")
            }
        }
    }

    private fun convertGroups(groups: List<WorldGroup>) {
        groups.forEach { group ->
            // Ensure that the group exists first, otherwise you get nulls down the road
            val pwiGroup = groupManager.getGroup(group.name)
            val worlds = group.worlds

            if (pwiGroup == null) {
                groupManager.addGroup(group.name, worlds, GameMode.SURVIVAL, true)
            } else {
                pwiGroup.addWorlds(worlds)
            }
        }

        groupManager.saveGroups()
    }
}
