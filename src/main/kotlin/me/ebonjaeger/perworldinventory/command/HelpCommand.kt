package me.ebonjaeger.perworldinventory.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import co.aikar.commands.annotation.HelpCommand
import me.ebonjaeger.perworldinventory.PerWorldInventory
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

@CommandAlias("perworldinventory|pwi")
class HelpCommand (private val plugin: PerWorldInventory) : BaseCommand()
{

    @HelpCommand
    fun onHelp(sender: CommandSender, help: CommandHelp)
    {
        sender.sendMessage("${ChatColor.DARK_GRAY}${ChatColor.STRIKETHROUGH}-----------------------------------------------------")
        sender.sendMessage("${ChatColor.DARK_GRAY}                [ ${ChatColor.BLUE}PerWorldInventoryCommands ${ChatColor.DARK_GRAY}]")
        help.showHelp()
        sender.sendMessage("${ChatColor.DARK_GRAY}${ChatColor.STRIKETHROUGH}-----------------------------------------------------")
    }

    @Subcommand("version")
    @CommandPermission("perworldinventory.version")
    @Description("View the installed version of PerWorldInventory")
    fun onVersion(sender: CommandSender)
    {
        val version = plugin.description.version
        val authors = plugin.description.authors
                .toString()
                .replace("[", "")
                .replace("]", "")

        sender.sendMessage("${ChatColor.BLUE}» ${ChatColor.GRAY}Version: ${ChatColor.BLUE}$version")
        sender.sendMessage("${ChatColor.BLUE}» ${ChatColor.GRAY}Authors: ${ChatColor.BLUE}$authors")
    }
}
