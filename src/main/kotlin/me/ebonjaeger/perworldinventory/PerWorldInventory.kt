package me.ebonjaeger.perworldinventory

import ch.jalu.configme.migration.PlainMigrationService
import ch.jalu.configme.resource.YamlFileResource
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.configuration.MetricsSettings
import me.ebonjaeger.perworldinventory.configuration.PlayerSettings
import me.ebonjaeger.perworldinventory.configuration.PluginSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import me.ebonjaeger.perworldinventory.data.FlatFile
import me.ebonjaeger.perworldinventory.listener.player.PlayerChangedWorldListener
import me.ebonjaeger.perworldinventory.listener.player.PlayerTeleportListener
import me.ebonjaeger.perworldinventory.serialization.PlayerSerializer
import net.milkbowl.vault.economy.Economy
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileWriter
import java.nio.file.Files

class PerWorldInventory : JavaPlugin()
{

    /**
     * Economy from Vault, if Vault is present on the server. If Vault is not
     * installed or we failed to hook into it, this will return null.
     */
    var economy: Economy? = null
        private set

    /**
     * If this is true, then Vault has been hooked in to, and the plugin is
     * configured to perform economy operations on players. If either of
     * those is not true, then this will return false.
     */
    var econEnabled = false
        private set

    val DATA_DIRECTORY = File(dataFolder, "data")
    val WORLDS_CONFIG_FILE = File(dataFolder, "worlds.json")

    private val groupManager = GroupManager(this)

    override fun onEnable()
    {
        ConsoleLogger.setLogger(logger)

        // Make data folder
        if (!Files.exists(DATA_DIRECTORY.toPath()))
        {
            Files.createDirectories(DATA_DIRECTORY.toPath())
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


        val settings = Settings(YamlFileResource(File(dataFolder, "config.yml")),
                PlainMigrationService(),
                PluginSettings::class.java,
                MetricsSettings::class.java,
                PlayerSettings::class.java)

        ConsoleLogger.setUseDebug(settings.getProperty(PluginSettings.DEBUG_MODE))

        groupManager.loadGroups(WORLDS_CONFIG_FILE)

        // TODO: Register commands

        // Register Listeners
        server.pluginManager.registerEvents(PlayerChangedWorldListener(groupManager, settings), this)
        server.pluginManager.registerEvents(PlayerTeleportListener(groupManager), this)

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

        econEnabled = economy != null && settings.getProperty(PlayerSettings.USE_ECONOMY)

        // Initialize serializer and data source
        val playerSerializer = PlayerSerializer(this, settings)
        val dataSource = FlatFile(this,
                playerSerializer,
                settings.getProperty(PluginSettings.CACHE_DURATION).toLong(),
                settings.getProperty(PluginSettings.CACHE_MAX_LIMIT).toLong())

        // Start bStats metrics
        if (settings.getProperty(MetricsSettings.ENABLE_METRICS))
        {
            startMetrics(settings)
        }

        ConsoleLogger.debug("PerWorldInventory is enabled and debug-mode is active!");
    }

    override fun onDisable()
    {
        groupManager.groups.clear()
        server.scheduler.cancelTasks(this)
    }

    /**
     * Start sending metrics information to bStats. If so configured, this
     * will send the number of configured groups and the number of worlds on
     * the server.
     *
     * @param settings The settings for sending group and world information
     */
    private fun startMetrics(settings: Settings)
    {
        val bStats = Metrics(this)

        if (settings.getProperty(MetricsSettings.SEND_NUM_GROUPS))
        {
            // Get the total number of configured Groups
            bStats.addCustomChart(Metrics.SimplePie("num_groups", {
                val numGroups = groupManager.groups.size

                return@SimplePie numGroups.toString()
            }))
        }

        if (settings.getProperty(MetricsSettings.SEND_NUM_WORLDS))
        {
            // Get the total number of worlds (configured or not)
            bStats.addCustomChart(Metrics.SimplePie("num_worlds", {
                val numWorlds = Bukkit.getWorlds().size

                when
                {
                    numWorlds <= 5 -> return@SimplePie "1-5"
                    numWorlds <= 10 -> return@SimplePie "6-10"
                    numWorlds <= 15 -> return@SimplePie "11-15"
                    numWorlds <= 20 -> return@SimplePie "16-20"
                    numWorlds <= 25 -> return@SimplePie "21-25"
                    numWorlds <= 30 -> return@SimplePie "26-30"
                    else -> return@SimplePie numWorlds.toString()
                }
            }))
        }
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
