package me.ebonjaeger.perworldinventory.data

import me.ebonjaeger.perworldinventory.Group
import org.bukkit.GameMode
import java.util.*

class ProfileKey(val uuid: UUID,
                 val group: Group,
                 val gameMode: GameMode)
{

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (other !is ProfileKey) return false

        return Objects.equals(uuid, other.uuid) &&
                Objects.equals(group, other.group) &&
                Objects.equals(gameMode, other.gameMode)
    }

    override fun hashCode(): Int
    {
        var result = uuid.hashCode()
        result = 31 * result + group.hashCode()
        result = 31 * result + gameMode.hashCode()
        return result
    }

    override fun toString(): String
    {
        return "ProfileKey{" +
                "uuid='$uuid'" +
                ", group='${group.name}'" +
                ", gameMode='${gameMode.toString().toLowerCase()}'" +
                "}"
    }
}
