package me.ebonjaeger.perworldinventory

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import org.bukkit.GameMode
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class GroupManager(private val plugin: PerWorldInventory)
{

    val groups = mutableMapOf<String, Group>()

    /**
     * Add a Group.
     *
     * @param name The name of the group
     * @param worlds A Set of world names
     * @param gameMode The default GameMode for this group
     */
    fun addGroup(name: String, worlds: MutableSet<String>, gameMode: GameMode)
    {
        ConsoleLogger.debug("Adding group to memory. Group: $name Worlds: " +
                "$worlds Gamemode: ${gameMode.name}")

        val group = Group(name, worlds, gameMode)
        groups.put(name.decapitalize(), group)
    }

    /**
     * Get a group by name. This will return null if no group with the given name
     * exists.
     *
     * @param group The name of the Group
     * @return The Group
     */
    fun getGroup(name: String): Group?
            = groups[name.decapitalize()]

    /**
     * Get the group that contains a specific world. This method iterates
     * through the groups and checks if each one contains the name of the
     * given world. If no groups contain the world, a new group will be
     * created and returned.
     *
     * @param world The name of the world in the group
     * @return The group that contains the given world
     */
    fun getGroupFromWorld(world: String): Group
    {
        groups.values.forEach {
            if (it.containsWorld(world))
            {
                return it
            }
        }

        // If we reach this point, the group doesn't yet exist.
        val worlds = mutableSetOf(world, "${world}_nether", "${world}_the_end")
        val group = Group(world, worlds, GameMode.SURVIVAL)
        groups.put(world.decapitalize(), group)
        ConsoleLogger.warning("Creating a new group on the fly for '$world'." +
                " Please double check your `worlds.json` file configuration.!")

        return group
    }

    /**
     * Load the groups configured in the file `worlds.json` into memory.
     *
     * @param file The file to load the groups from
     */
    fun loadGroups(file: File)
    {
        plugin.server.scheduler.runTaskAsynchronously(plugin, {
            JsonReader(FileReader(file)).use {
                val parser = JsonParser()
                val data = parser.parse(it).asJsonObject
                val root = data["groups"].asJsonObject

                plugin.server.scheduler.runTask(plugin, {
                    val gson = Gson()
                    for (jsonGroup in root.entrySet())
                    {
                        val jsonObject = root[jsonGroup.key].asJsonObject
                        val group = gson.fromJson(jsonObject, Group::class.java)
                        group.configured = true

                        groups.put(group.name.decapitalize(), group)
                    }
                })
            }
        })
    }

    /**
     * Save all of the groups currently in memory to the disk.
     */
    fun saveGroups()
    {
        val gson = Gson()
        val root = JsonObject()
        val groups = JsonObject()

        this.groups.values.forEach {
            val group = JsonObject()
            group.addProperty("name", it.name)

            val worlds = JsonArray()
            it.worlds.forEach { worlds.add(it) }
            group.add("worlds", worlds)
            group.addProperty("default-gamemode", it.defaultGameMode.name)

            groups.add(it.name, group)
        }

        root.add("groups", groups)

        FileWriter(plugin.WORLDS_CONFIG_FILE).use {
            it.write(gson.toJson(root))
        }
    }
}
