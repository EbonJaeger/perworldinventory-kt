package me.ebonjaeger.perworldinventory

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.milkbowl.vault.economy.Economy
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileWriter
import java.nio.file.Files

class PerWorldInventory : JavaPlugin()
{

    var economy: Economy? = null
        private set

    val WORLDS_CONFIG_FILE = File(dataFolder, "worlds.json")

    private val groupManager = GroupManager(this)

    override fun onEnable()
    {
        ConsoleLogger.setLogger(logger)

        // Make data folders
        val defaultsDir = File(dataFolder.path + File.separator + "data", "defaults").toPath()
        if (Files.exists(defaultsDir))
        {
            Files.createDirectories(defaultsDir)
        }

        // Check if `worlds.yml` exists. If it does, convert it to JSON.
        // Otherwise, save it if it doesn't exist.
        if (Files.exists(File(dataFolder, "worlds.yml").toPath()))
        {
            val configuration = YamlConfiguration.loadConfiguration(File(dataFolder, "worlds.yml"))
            convertYamlToJson(configuration)
        } else
        {
            saveResource("worlds.json", false)
        }

        // TODO: Main config stuff

        groupManager.loadGroups(WORLDS_CONFIG_FILE)

        // TODO: Register commands

        // TODO: Register Listeners

        // Register Vault if present
        if (server.pluginManager.getPlugin("Vault") != null)
        {
            ConsoleLogger.info("Vault found! Hooking into it...")
            val rsp = server.servicesManager.getRegistration(Economy::class.java)
            if (rsp != null)
            {
                economy = rsp.provider
                ConsoleLogger.info("Hooked into Vault!")
            } else
            {
                ConsoleLogger.warning("Unable to hook into Vault!")
            }
        }

        // TODO: Initialize bStats telemetry
    }

    override fun onDisable()
    {
        groupManager.groups.clear()
        server.scheduler.cancelTasks(this)
    }

    /**
     * Converts an existing YAML worlds configuration to its representation
     * in JSON.
     *
     * The old file will be renamed (not deleted!), and a new json file
     * created and written to.
     *
     * @param config The Yaml worlds configuration
     */
    private fun convertYamlToJson(config: FileConfiguration)
    {
        val root = JsonObject()
        val groups = JsonObject()

        config.getConfigurationSection("groups.").getKeys(false).forEach {
            val group = JsonObject()

            // Convert the list of worlds
            val worlds = config.getStringList("groups.$it.worlds")
            val worldsArray = JsonArray()
            worlds.forEach { worldsArray.add(it) }
            group.add("worlds", worldsArray)

            // Convert the default gamemode
            group.addProperty("default-gamemode", config.getString("groups" +
                    ".$it.default-gamemode"))

            groups.add(it, group)
        }

        root.add("groups", groups)

        // Rename old .yml file, and create new json file
        Files.move(File(dataFolder, "worlds.yml").toPath(), File(dataFolder,
                "worlds.old.yml").toPath())
        Files.createFile(WORLDS_CONFIG_FILE.toPath())

        // Save to the new json file
        val gson = Gson()
        server.scheduler.runTaskAsynchronously(this, {
            FileWriter(WORLDS_CONFIG_FILE).use {
                it.write(gson.toJson(root))
            }
        })
    }
}
