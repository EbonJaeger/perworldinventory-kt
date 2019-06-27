package me.ebonjaeger.perworldinventory.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import me.ebonjaeger.perworldinventory.data.migration.MigrationService
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import javax.inject.Inject

@CommandAlias("perworldinventory|pwi")
class MigrateCommand @Inject constructor(private val migrationService: MigrationService) : BaseCommand() {

    @Subcommand("migrate")
    @CommandPermission("perworldinventory.command.migrate")
    @Description("Migrate old data to the latest data format")
    fun onMigrate(sender: CommandSender) {
        if (migrationService.isMigrating()) {
            sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}A data migration is already in progress!")
            return
        }

        sender.sendMessage("${ChatColor.BLUE}» ${ChatColor.GRAY}Beginning data migration to new format!")
        migrationService.beginMigration(sender)
    }
}
