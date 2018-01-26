package me.ebonjaeger.perworldinventory.service

import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.Utils
import org.bukkit.scheduler.BukkitTask
import javax.inject.Inject

/**
 * Service for functions around the server the plugin is running on (scheduling tasks, etc.).
 *
 * @param plugin the plugin instance
 */
class BukkitService @Inject constructor(private val plugin: PerWorldInventory)
{

    private val scheduler = plugin.server.scheduler

    fun isShuttingDown() =
        plugin.isShuttingDown

    fun getServerVersion() =
        plugin.server.version

    fun runTaskAsynchronously(task: () -> Unit) =
        scheduler.runTaskAsynchronously(plugin, task)

    fun runTask(task: () -> Unit): BukkitTask =
        scheduler.runTask(plugin, task)

    fun shouldUseAttributes() =
            Utils.checkServerVersion(getServerVersion(), 1, 9, 0)
}
