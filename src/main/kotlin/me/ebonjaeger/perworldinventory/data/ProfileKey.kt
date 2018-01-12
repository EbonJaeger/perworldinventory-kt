package me.ebonjaeger.perworldinventory.data

import org.bukkit.GameMode
import java.util.*

class ProfileKey(val uuid: UUID,
                 val groupName: String,
                 val gameMode: GameMode)
{

    override fun equals(other: Any?): Boolean
    {
        if (this == other) return true
        if (other !is ProfileKey) return false

        return Objects.equals(uuid, other.uuid) &&
                Objects.equals(groupName, other.groupName) &&
                Objects.equals(gameMode, other.gameMode)
    }

    override fun hashCode(): Int
    {
        return Objects.hash(uuid, groupName, gameMode)
    }
}
