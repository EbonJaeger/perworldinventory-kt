package me.ebonjaeger.perworldinventory.permission


enum class PermissionSystem (val longName: String,
                             val pluginName: String)
{

    B_PERMISSIONS("bPermissions", "bPermissions"),

    ESSENTIALS_GROUP_MANAGER("Essentials Group Manager", "GroupManager"),

    LUCKPERMS("LuckPerms", "LuckPerms"),

    PERMISSIONS_BUKKIT("Permissions Bukkit", "PermissionsBukkit"),

    PERMISSIONS_EX("PermissionsEx", "PermissionsEx"),

    VAULT("Vault", "Vault"),

    Z_PERMISSIONS("zPermissions", "zPermissions");

    override fun toString(): String
    {
        return longName
    }
}
