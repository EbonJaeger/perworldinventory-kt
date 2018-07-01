package me.ebonjaeger.perworldinventory.permission

enum class PlayerPermission (private val node: String) : PermissionNode
{

    BYPASS_WORLDS("perworldinventory.bypass.world"),

    BYPASS_GAMEMODE("perworldinventory.bypass.gamemode"),

    BYPASS_ENFORCE_GAMEMODE("perworldinventory.bypass.enforcegamemode");

    override fun getNode(): String
    {
        return node
    }
}
