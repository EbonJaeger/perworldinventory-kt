package me.ebonjaeger.perworldinventory

import org.bukkit.GameMode
import java.util.*

/**
 * A group of worlds, typically defined in the worlds.json file.
 * Each Group has a name, and should have a list of worlds in that group, as well as
 * a default GameMode.
 *
 * This data class can be serialized/deserialized to and from Json directly.
 *
 * @param name The name of the group
 * @param worlds A Set of world names
 * @param defaultGameMode The default GameMode for players in this group
 */
data class Group(
        val name: String,
        val worlds: MutableSet<String>,
        val defaultGameMode: GameMode
        )
{

    /**
     * If this is true, then this group was configured in the `worlds.json`
     * file. If the group was created on the fly due to a world not being in
     * a group, then this will be false.
     */
    var configured: Boolean = false

    /**
     * Get whether this group contains a world with a given name.
     *
     * @param toCheck The name of the world to check for
     *
     * @return True if the world is in this group
     */
    fun containsWorld(toCheck: String): Boolean
            = worlds.contains(toCheck)

    /**
     * Add a list of worlds to this group.
     *
     * @param worlds A list of the worlds to add
     */
    fun addWorlds(worlds: Collection<String>)
    {
        this.worlds.addAll(worlds)
    }

    /**
     * Add a world to this group.
     *
     * @param world The name of the world to add
     */
    fun addWorld(world: String)
    {
        worlds.add(world)
    }

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (other !is Group) return false

        return Objects.equals(name, other.name) &&
                Objects.equals(worlds, other.worlds) &&
                Objects.equals(defaultGameMode, other.defaultGameMode)
    }

    override fun hashCode(): Int
    {
        return Objects.hash(name, worlds.toString(), defaultGameMode)
    }
}
