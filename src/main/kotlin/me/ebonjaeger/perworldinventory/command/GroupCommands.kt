package me.ebonjaeger.perworldinventory.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.ebonjaeger.perworldinventory.Group
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.locale.MessageHandler
import me.ebonjaeger.perworldinventory.locale.MessageKey
import me.ebonjaeger.perworldinventory.locale.MessageType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import javax.inject.Inject

@CommandAlias("perworldinventory|pwi")
class GroupCommands @Inject constructor(private val groupManager: GroupManager,
                                        private val messageHandler: MessageHandler) : BaseCommand()
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
                messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.UNKNOWN_GROUP, groupName)
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
            messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.NAME_EXISTS, name)
            return
        }

        // Get the GameMode
        val gameMode = try
        {
            GameMode.valueOf(defaultGameMode.toUpperCase())
        } catch (ex: IllegalArgumentException)
        {
            messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.INVALID_GAMEMODE, defaultGameMode)
            return
        }

        groupManager.addGroup(name, worlds.toMutableSet(), gameMode, true)
        groupManager.saveGroups()
        messageHandler.sendMessage(sender, MessageType.SUCCESS, MessageKey.CREATED_SUCCESSFULLY)
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
            messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.UNKNOWN_GROUP, groupName)
            return
        }

        var worldName = world

        // Check if the sender specified a world, and if that world exists
        if (world != null && Bukkit.getWorld(world) == null)
        {
            // User specified a world, but it doesn't exist
            messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.UNKNOWN_WORLD, worldName)
            return
        } else if (world == null) // Else, get the world from the sender's current location
        {
            if (sender !is Player)
            {
                messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.MUST_BE_PLAYER)
                return
            }

            worldName = sender.location.world.name
        }

        group.addWorld(worldName)
        groupManager.saveGroups()
        messageHandler.sendMessage(sender, MessageType.SUCCESS, MessageKey.ADDED_WORLD, worldName, groupName)
    }

    @Subcommand("group delete")
    @CommandPermission("perworldinventory.command.groups.remove")
    @Description("Remove a group")
    @CommandCompletion("@groups")
    fun onRemoveGroup(sender: CommandSender, group: String)
    {
        if (groupManager.getGroup(group) == null)
        {
            messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.UNKNOWN_GROUP, group)
            return
        }

        groupManager.removeGroup(group)
        groupManager.saveGroups()
        messageHandler.sendMessage(sender, MessageType.SUCCESS, MessageKey.REMOVED_GROUP)
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
            messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.UNKNOWN_GROUP, groupName)
            return
        }

        var worldName = world

        // Check if the sender specified a world, and if that world exists
        if (world != null && Bukkit.getWorld(world) == null)
        {
            // User specified a world, but it doesn't exist
            messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.UNKNOWN_WORLD, worldName)
            return
        } else if (world == null) // Else, get the world from the sender's current location
        {
            if (sender !is Player)
            {
                messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.MUST_BE_PLAYER)
                return
            }

            worldName = sender.location.world.name
        }

        // Make sure the group actually has the world in it
        if (!group.containsWorld(worldName))
        {
            messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.WORLD_NOT_IN_GROUP, groupName, worldName)
            return
        }

        group.removeWorld(worldName)
        groupManager.saveGroups()
        messageHandler.sendMessage(sender, MessageType.SUCCESS, MessageKey.WORLD_REMOVED, worldName, groupName)
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
            messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.UNKNOWN_GROUP, groupName)
            return
        }

        var worldName = world

        // Check if the sender specified a world, and if that world exists
        if (world != null && Bukkit.getWorld(world) == null)
        {
            // User specified a world, but it doesn't exist
            messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.UNKNOWN_WORLD, worldName)
            return
        } else if (world == null) // Else, get the world from the sender's current location
        {
            if (sender !is Player)
            {
                messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.MUST_BE_PLAYER)
                return
            }

            worldName = sender.location.world.name
        }

        // Make sure the group actually has the world in it
        if (!group.containsWorld(worldName))
        {
            messageHandler.sendMessage(sender, MessageType.ERROR, MessageKey.RESPAWN_NOT_IN_GROUP)
            return
        }

        group.respawnWorld = worldName
        groupManager.saveGroups()
        messageHandler.sendMessage(sender, MessageType.SUCCESS, MessageKey.RESPAWN_SET, groupName)
    }
}
