package me.ebonjaeger.perworldinventory.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import com.onarandombox.multiverseinventories.MultiverseInventories
import me.ebonjaeger.perworldinventory.conversion.ConvertService
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.PluginManager
import javax.inject.Inject

@CommandAlias("perworldinventory|pwi")
class ConvertCommand @Inject constructor(private val pluginManager: PluginManager,
                                         private val convertService: ConvertService) : BaseCommand()
{

    @Subcommand("convert")
    @CommandPermission("perworldinventory.convert")
    @Description("Convert inventory data from another plugin")
    fun onConvert(sender: CommandSender)
    {
        if (!pluginManager.isPluginEnabled("Multiverse-Inventories"))
        {
            sender.sendMessage(
                    "${ChatColor.DARK_RED}» ${ChatColor.GRAY}Multiverse-Inventories is not installed! Cannot convert data!")
            return
        }

        val mvi = pluginManager.getPlugin("Multiverse-Inventories")
        if (mvi == null)
        {
            sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}Unable to get Multiverse-Inventories instance!")
            return
        }

        convertService.runConversion(sender, mvi as MultiverseInventories)
    }
}
