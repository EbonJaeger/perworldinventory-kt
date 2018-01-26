package me.ebonjaeger.perworldinventory.command

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.configuration.PluginSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import javax.inject.Inject

@CommandAlias("perworldinventory|pwi")
class ReloadCommand @Inject constructor(private val plugin: PerWorldInventory,
                                        private val groupManager: GroupManager,
                                        private val settings: Settings) : PWIBaseCommand()
{
    @Subcommand("reload")
    @CommandPermission("perworldinventory.reload")
    @Description("Reload Configurations")
    fun onReload(sender: CommandSender)
    {
        settings.reload()
        ConsoleLogger.setUseDebug(settings.getProperty(PluginSettings.DEBUG_MODE))
        groupManager.loadGroups()

        sender.sendMessage("${ChatColor.BLUE}» ${ChatColor.GRAY}Configuration files reloaded!")
    }
}
