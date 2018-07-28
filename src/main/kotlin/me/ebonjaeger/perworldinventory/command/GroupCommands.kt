package me.ebonjaeger.perworldinventory.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.ebonjaeger.perworldinventory.Group
import me.ebonjaeger.perworldinventory.GroupManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import javax.inject.Inject

@CommandAlias("perworldinventory|pwi")
class GroupCommands @Inject constructor(private val groupManager: GroupManager) : BaseCommand()
{

    @Subcommand("group list")
    @CommandPermission("perworldinventory.command.groups.list")
    @Description("Shows all of the current group names")
    fun onListGroups(sender: CommandSender)
    {
        sender.sendMessage("${ChatColor.DARK_GRAY}                [ ${ChatColor.BLUE}PerWorldInventory Groups ${ChatColor.DARK_GRAY}]")
        for (i in 0 until groupManager.groups.keys.size)
        {
            val key = groupManager.groups.keys.elementAt(i)
            sender.sendMessage("${ChatColor.BLUE} $i${ChatColor.GRAY}: $key")
        }
    }

    @Subcommand("group info")
    @CommandPermission("perworldinventory.command.groups.info")
    @Description("Display information about a group")
    @CommandCompletion("@groups")
    fun onGroupInfo(sender: CommandSender, @Optional groupName: String)
    {
        var group: Group? = null
        if (groupName !== null)
        {
            group = groupManager.getGroup(groupName)

        }

        if (group === null)
        {
            if (sender is Player)
            {
                group = groupManager.getGroupFromWorld(sender.location.world.name)
            } else
            {
                sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}Unknown group '$groupName'!")
                return
            }
        }

        sender.sendMessage("${ChatColor.BLUE}Group name: ${ChatColor.GRAY}${group.name}")
        sender.sendMessage("${ChatColor.BLUE}Number of worlds: ${ChatColor.GRAY}${group.worlds.count()}")
        sender.sendMessage("${ChatColor.BLUE}Worlds:")
        group.worlds.forEach { sender.sendMessage("${ChatColor.GRAY} - $it") }
        sender.sendMessage("${ChatColor.BLUE}Default GameMode: ${ChatColor.GRAY}${group.defaultGameMode.name}")
        if (group.respawnWorld != null)
        {
            sender.sendMessage("${ChatColor.BLUE}Default respawn world: ${group.respawnWorld}")
        }
    }

    @Subcommand("group create")
    @CommandPermission("perworldinventory.command.groups.add")
    @Description("Create a new world group")
    fun onAddGroup(sender: CommandSender, name: String, @Default("SURVIVAL") defaultGameMode: String, vararg worlds: String)
    {
        // Check if this group already exists
        if (groupManager.getGroup(name) != null)
        {
            sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}A group called '$name' already exists!")
            return
        }

        // Get the GameMode
        val gameMode = try
        {
            GameMode.valueOf(defaultGameMode.toUpperCase())
        } catch (ex: IllegalArgumentException)
        {
            sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}'$defaultGameMode' is not a valid GameMode!")
            return
        }

        groupManager.addGroup(name, worlds.toMutableSet(), gameMode, true)
        groupManager.saveGroups()
        sender.sendMessage("${ChatColor.GREEN}» ${ChatColor.GRAY}Group created successfully!")
    }

    @Subcommand("group addworld|aw")
    @CommandPermission("perworldinventory.command.groups.modify")
    @Description("Add a world to a group")
    @CommandCompletion("@groups @worlds")
    fun onAddWorld(sender: CommandSender, groupName: String, @Optional world: String)
    {
        val group = groupManager.getGroup(groupName)

        // Check if the group exists
        if (group == null)
        {
            sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}No group with that name exists!")
            return
        }

        var worldName = world

        // Check if the sender specified a world, and if that world exists
        if (world != null && Bukkit.getWorld(world) == null)
        {
            // User specified a world, but it doesn't exist
            sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}No world with that name exists!")
            return
        } else if (world == null) // Else, get the world from the sender's current location
        {
            if (sender !is Player)
            {
                sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}Must be a player to execute this command without a world!")
                return
            }

            worldName = sender.location.world.name
        }

        group.addWorld(worldName)
        groupManager.saveGroups()
        sender.sendMessage("${ChatColor.GREEN}» ${ChatColor.GRAY}Added the world '$worldName' to group '$groupName'!")
    }

    @Subcommand("group delete")
    @CommandPermission("perworldinventory.command.groups.remove")
    @Description("Remove a group")
    @CommandCompletion("@groups")
    fun onRemoveGroup(sender: CommandSender, group: String)
    {
        if (groupManager.getGroup(group) == null)
        {
            sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}No group with that name exists!")
            return
        }

        groupManager.removeGroup(group)
        groupManager.saveGroups()
        sender.sendMessage("${ChatColor.GREEN}» ${ChatColor.GRAY}Group removed successfully!")
    }

    @Subcommand("group removeworld|rw")
    @CommandPermission("perworldinventory.command.groups.modify")
    @Description("Remove a world from a group")
    @CommandCompletion("@groups @worlds")
    fun onRemoveWorld(sender: CommandSender, groupName: String, @Optional world: String)
    {
        val group = groupManager.getGroup(groupName)

        // Check if the group exists
        if (group == null)
        {
            sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}No group with that name exists!")
            return
        }

        var worldName = world

        // Check if the sender specified a world, and if that world exists
        if (world != null && Bukkit.getWorld(world) == null)
        {
            // User specified a world, but it doesn't exist
            sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}No world with that name exists!")
            return
        } else if (world == null) // Else, get the world from the sender's current location
        {
            if (sender !is Player)
            {
                sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}Must be a player to execute this command without a world!")
                return
            }

            worldName = sender.location.world.name
        }

        // Make sure the group actually has the world in it
        if (!group.containsWorld(worldName))
        {
            sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}No world in group '$groupName' called '$worldName'!")
            return
        }

        group.removeWorld(worldName)
        groupManager.saveGroups()
        sender.sendMessage("${ChatColor.GREEN}» ${ChatColor.GRAY}Removed the world '$worldName' from group '$groupName'!")
    }

    @Subcommand("group setrespawn|sw")
    @CommandPermission("perworldinventory.command.groups.modify")
    @Description("Set the default spawn world for a group")
    @CommandCompletion("@groups @worlds")
    fun onSetRespawnWorld(sender: CommandSender, groupName: String, @Optional world: String)
    {
        val group = groupManager.getGroup(groupName)

        // Check if the group exists
        if (group == null)
        {
            sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}No group with that name exists!")
            return
        }

        var worldName = world

        // Check if the sender specified a world, and if that world exists
        if (world != null && Bukkit.getWorld(world) == null)
        {
            // User specified a world, but it doesn't exist
            sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}No world with that name exists!")
            return
        } else if (world == null) // Else, get the world from the sender's current location
        {
            if (sender !is Player)
            {
                sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}Must be a player to execute this command without a world!")
                return
            }

            worldName = sender.location.world.name
        }

        // Make sure the group actually has the world in it
        if (!group.containsWorld(worldName))
        {
            sender.sendMessage("${ChatColor.DARK_RED}» ${ChatColor.GRAY}Respawn world must be in the group!")
            return
        }

        group.respawnWorld = worldName
        groupManager.saveGroups()
        sender.sendMessage("${ChatColor.GREEN}» ${ChatColor.GRAY}Respawn world for group '${group.name}' set!")
    }
}
