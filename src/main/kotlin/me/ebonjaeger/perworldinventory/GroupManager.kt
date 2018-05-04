package me.ebonjaeger.perworldinventory

import me.ebonjaeger.perworldinventory.initialization.PluginFolder
import me.ebonjaeger.perworldinventory.service.BukkitService
import org.bukkit.GameMode
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import javax.inject.Inject

class GroupManager @Inject constructor(@PluginFolder pluginFolder: File,
                                       private val bukkitService: BukkitService)
{

    private val WORLDS_CONFIG_FILE = File(pluginFolder, "worlds.yml")

    val groups = mutableMapOf<String, Group>()

    /**
     * Add a Group.
     *
     * @param name The name of the group
     * @param worlds A Set of world names
     * @param gameMode The default GameMode for this group
     * @param configured If the group was configured (true) or created on the fly (false)
     */
    fun addGroup(name: String, worlds: MutableSet<String>, gameMode: GameMode, configured: Boolean)
    {
        val group = Group(name, worlds, gameMode)
        group.configured = configured
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
        var group = groups.values.firstOrNull { it.containsWorld(world) }
        if (group != null) return group

        // If we reach this point, the group doesn't yet exist.
        val worlds = mutableSetOf(world, "${world}_nether", "${world}_the_end")
        group = Group(world, worlds, GameMode.SURVIVAL)

        addGroup(world, worlds, GameMode.SURVIVAL, false)
        ConsoleLogger.warning("Creating a new group on the fly for '$world'." +
                " Please double check your `worlds.yml` file configuration!")

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
     * Load the groups configured in the file `worlds.yml` into memory.
     */
    fun loadGroups()
    {
        groups.clear()

        bukkitService.runTaskAsynchronously({
            val yaml = YamlConfiguration.loadConfiguration(WORLDS_CONFIG_FILE)
            bukkitService.runTask {
                yaml.getConfigurationSection("groups.").getKeys(false).forEach { key ->
                    val worlds = yaml.getStringList("groups.$key.worlds").toMutableSet()
                    val gameMode = GameMode.valueOf( (yaml.getString("groups.$key.default-gamemode") ?: "SURVIVAL").toUpperCase() )
                    addGroup(key, worlds, gameMode, true)
                }
            }
        })
    }

    /**
     * Save all of the groups currently in memory to the disk.
     */
    fun saveGroups()
    {
        val config = YamlConfiguration.loadConfiguration(WORLDS_CONFIG_FILE)
        config.options().header(Utils.getWorldsConfigHeader())
        config.set("groups", null)

        groups.values.forEach { toYaml(it, config) }

        try
        {
            config.save(WORLDS_CONFIG_FILE)
        } catch (ex: IOException)
        {
            ConsoleLogger.warning("Could not save the groups config to disk:", ex)
        }
    }

    private fun toYaml(group: Group, config: YamlConfiguration)
    {
        val key = "groups.${group.name}"
        config.set(key, null)
        config.set("$key.worlds", group.worlds)
        config.set("$key.default-gamemode", group.defaultGameMode.toString())
    }
}
