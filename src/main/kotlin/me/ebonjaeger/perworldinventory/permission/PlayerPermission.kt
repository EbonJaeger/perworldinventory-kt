package me.ebonjaeger.perworldinventory.permission

enum class PlayerPermission (private val node: String,
                             private val defaultPermission: DefaultPermission) : PermissionNode
{

    BYPASS_WORLDS("perworldinventory.bypass.world", DefaultPermission.NOT_ALLOWED),

    BYPASS_GAMEMODE("perworldinventory.bypass.gamemode", DefaultPermission.NOT_ALLOWED),

    BYPASS_ENFORCEGAMEMODE("perworldinventory.bypass.enforcegamemode", DefaultPermission.NOT_ALLOWED);

    override fun getNode(): String
    {
        return node
    }

    override fun getDefaultPermission(): DefaultPermission
    {
        return defaultPermission
    }
}