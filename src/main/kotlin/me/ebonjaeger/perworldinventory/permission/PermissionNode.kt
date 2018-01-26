package me.ebonjaeger.perworldinventory.permission

interface PermissionNode
{

    fun getNode(): String

    fun getDefaultPermission(): DefaultPermission
}