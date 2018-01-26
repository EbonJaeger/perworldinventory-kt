package me.ebonjaeger.perworldinventory.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandIssuer
import co.aikar.commands.RegisteredCommand
import org.bukkit.command.CommandSender

open class PWIBaseCommand : BaseCommand()
{

    override fun canExecute(issuer: CommandIssuer?, cmd: RegisteredCommand<*>?): Boolean
    {
        val sender = issuer?.getIssuer() as CommandSender
        return true
    }
}
