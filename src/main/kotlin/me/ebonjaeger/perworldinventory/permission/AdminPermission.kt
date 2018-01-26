package me.ebonjaeger.perworldinventory.permission

enum class AdminPermission (private val node: String,
                            private val defaultPermission: DefaultPermission) : PermissionNode
{

    CONVERT("perworldinventory.convert", DefaultPermission.OP_ONLY),

    HELP("perworldinventory.help", DefaultPermission.OP_ONLY),

    RELOAD("perworldinventory.reload", DefaultPermission.OP_ONLY),

    VERSION("perworldinventory.version", DefaultPermission.OP_ONLY);

    override fun getNode(): String
    {
        return node
    }

    override fun getDefaultPermission(): DefaultPermission
    {
        return defaultPermission
    }
}