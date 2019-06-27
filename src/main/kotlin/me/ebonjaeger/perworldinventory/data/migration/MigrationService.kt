package me.ebonjaeger.perworldinventory.data.migration

import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.initialization.DataDirectory
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.io.File
import javax.inject.Inject

class MigrationService @Inject constructor(private val groupManager: GroupManager,
                                           private val plugin: PerWorldInventory,
                                           @DataDirectory private val dataDirectory: File) {

    private var migrating = false
    private var sender: CommandSender? = null

    fun isMigrating(): Boolean {
        return migrating
    }

    fun beginMigration(sender: CommandSender) {
        val offlinePlayers = Bukkit.getOfflinePlayers()

        if (migrating) {
            return
        }

        this.sender = sender

        if (sender !is ConsoleCommandSender) { // No need to send a message to console when console did the command
            ConsoleLogger.info("Beginning data migration to new format.")
        }

        migrating = true

        val task = MigrationTask(this, offlinePlayers, dataDirectory, groupManager.groups.values)
        task.runTaskTimerAsynchronously(plugin, 0, 20)
    }

    /**
     * Alerts that the migration completed.
     *
     * @param numMigrated The number of profiles migrated
     */
    fun finishMigration(numMigrated: Int) {
        migrating = false
        ConsoleLogger.info("Data migration has been completed! Migrated $numMigrated profiles.")
        if (sender != null && sender is Player) {
            if ((sender as Player).isOnline) {
                (sender as Player).sendMessage("${ChatColor.GREEN}» ${ChatColor.GRAY}Data migration has been completed!")
            }
        }
    }
}
