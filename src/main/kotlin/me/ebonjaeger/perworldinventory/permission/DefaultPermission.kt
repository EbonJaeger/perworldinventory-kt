package me.ebonjaeger.perworldinventory.permission

import org.bukkit.command.CommandSender

enum class DefaultPermission
{

    NOT_ALLOWED
    {

        override fun evaluate(sender: CommandSender?): Boolean
        {
            return false
        }
    },

    OP_ONLY
    {

        override fun evaluate(sender: CommandSender?): Boolean
        {
            return sender!!.isOp
        }
    },

    ALLOWED
    {

        override fun evaluate(sender: CommandSender?): Boolean
        {
            return true
        }
    };

    abstract fun evaluate(sender: CommandSender?): Boolean
}