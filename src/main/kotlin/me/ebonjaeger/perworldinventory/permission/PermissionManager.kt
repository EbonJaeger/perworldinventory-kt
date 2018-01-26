package me.ebonjaeger.perworldinventory.permission

import org.bukkit.command.CommandSender

class PermissionManager {

    /**
     * Evaluate whether the sender has permission to perform an action.
     *
     * @param sender The sender to evaluate.
     * @param node The permission node.
     * @return If the sender has permission.
     */
    fun hasPermission(sender: CommandSender, node: PermissionNode?): Boolean
    {
        return node == null || sender.hasPermission(node.getNode())
    }
}