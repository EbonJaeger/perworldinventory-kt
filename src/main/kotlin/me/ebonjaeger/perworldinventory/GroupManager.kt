package me.ebonjaeger.perworldinventory

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import me.ebonjaeger.perworldinventory.initialization.PluginFolder
import me.ebonjaeger.perworldinventory.service.BukkitService
import org.bukkit.GameMode
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import javax.inject.Inject

class GroupManager @Inject constructor(@PluginFolder pluginFolder: File,
                                       private val bukkitService: BukkitService)
{

    private val WORLDS_CONFIG_FILE = File(pluginFolder, "worlds.json")

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
        val group = Group(name, worlds, gameMode)
        ConsoleLogger.debug("Adding group to memory: $group")
        groups[name.toLowerCase()] = group
    }

    /**
     * Get a group by name. This will return null if no group with the given name
     * exists.
     *
     * @param name The name of the Group
     * @return The Group
     */
    fun getGroup(name: String): Group?
            = groups[name.toLowerCase()]

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
        groups[world.toLowerCase()] = group
        ConsoleLogger.warning("Creating a new group on the fly for '$world'." +
                " Please double check your `worlds.json` file configuration!")

        return group
    }

    /**
     * Remove a world group.
     *
     * @param group The name of the group to remove
     */
    fun removeGroup(group: String)
    {
        groups.remove(group.toLowerCase())
        ConsoleLogger.debug("Removed group '$group'")
    }

    /**
     * Load the groups configured in the file `worlds.json` into memory.
     */
    fun loadGroups()
    {
        groups.clear()

        bukkitService.runTaskAsynchronously({
            JsonReader(FileReader(WORLDS_CONFIG_FILE)).use {
                val parser = JsonParser()
                val data = parser.parse(it).asJsonObject
                val root = data["groups"].asJsonObject

                bukkitService.runTask({
                    root.entrySet().forEach { jsonGroup ->
                        val jsonObject = root[jsonGroup.key].asJsonObject
                        val name = jsonGroup.key

                        val worlds = mutableSetOf<String>()
                        jsonObject["worlds"].asJsonArray.forEach { worlds.add(it.asString) }

                        val defaultGameMode = GameMode.valueOf(jsonObject["default-gamemode"].asString.toUpperCase())
                        val group = Group(name, worlds, defaultGameMode)
                        group.configured = true

                        groups[group.name.toLowerCase()] = group
                        ConsoleLogger.debug("Loaded group into memory: $group")
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
        val gson = GsonBuilder().setPrettyPrinting().create()
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

        FileWriter(WORLDS_CONFIG_FILE).use {
            it.write(gson.toJson(root))
        }
    }
}
