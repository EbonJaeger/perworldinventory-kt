package me.ebonjaeger.perworldinventory.service

import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.Utils
import org.bukkit.Bukkit
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

    fun getOfflinePlayers() =
            Bukkit.getOfflinePlayers()

    fun getServerVersion() =
        plugin.server.version

    fun runRepeatingTaskAsynchronously(task: Runnable, delay: Long, period: Long) =
            scheduler.runTaskTimerAsynchronously(plugin, task, delay, period)

    fun runTaskAsynchronously(task: () -> Unit) =
        scheduler.runTaskAsynchronously(plugin, task)

    /**
     * Run a task that may or may not be asynchronous depending on the
     * parameter passed to this function.
     *
     * @param task The task to run
     * @param async If the task should be run asynchronously
     */
    fun runTaskOptionallyAsynchronously(task: () -> Unit, async: Boolean) =
            if (async) { scheduler.runTaskAsynchronously(plugin, task) } else { scheduler.runTask(plugin, task) }

    fun runTask(task: () -> Unit): BukkitTask =
        scheduler.runTask(plugin, task)

    fun shouldUseAttributes() =
            Utils.checkServerVersion(getServerVersion(), 1, 11, 0)
}
