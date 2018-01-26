package me.ebonjaeger.perworldinventory

import co.aikar.commands.BukkitCommandManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.command.HelpCommand
import me.ebonjaeger.perworldinventory.command.PWIBaseCommand
import me.ebonjaeger.perworldinventory.configuration.MetricsSettings
import me.ebonjaeger.perworldinventory.configuration.PlayerSettings
import me.ebonjaeger.perworldinventory.configuration.PluginSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import me.ebonjaeger.perworldinventory.data.DataSource
import me.ebonjaeger.perworldinventory.data.DataSourceProvider
import me.ebonjaeger.perworldinventory.data.ProfileManager
import me.ebonjaeger.perworldinventory.initialization.DataDirectory
import me.ebonjaeger.perworldinventory.initialization.Injector
import me.ebonjaeger.perworldinventory.initialization.InjectorBuilder
import me.ebonjaeger.perworldinventory.initialization.PluginFolder
import me.ebonjaeger.perworldinventory.listener.player.InventoryCreativeListener
import me.ebonjaeger.perworldinventory.listener.player.PlayerChangedWorldListener
import me.ebonjaeger.perworldinventory.listener.player.PlayerQuitListener
import me.ebonjaeger.perworldinventory.listener.player.PlayerTeleportListener
import me.ebonjaeger.perworldinventory.permission.PermissionManager
import net.milkbowl.vault.economy.Economy
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.util.*

class PerWorldInventory : JavaPlugin
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

    /**
     * Get whether or not the server is currently shutting down.
     */
    var isShuttingDown = false
        private set

    val timeouts = hashMapOf<UUID, Int>()
    var updateTimeoutsTaskId = -1

    private val DATA_DIRECTORY = File(dataFolder, "data")
    val SLOT_TIMEOUT = 5
    val WORLDS_CONFIG_FILE = File(dataFolder, "worlds.json")

    constructor(): super()

    /* Constructor used for tests. */
    internal constructor(loader: JavaPluginLoader, description: PluginDescriptionFile, dataFolder: File, file: File?)
            : super(loader, description, dataFolder, file)

    override fun onEnable()
    {
        ConsoleLogger.setLogger(logger)

        // Make data folder
        if (!Files.exists(DATA_DIRECTORY.toPath()))
        {
            Files.createDirectories(DATA_DIRECTORY.toPath())
        }

        // Create config file if it does not exist
        if (!Files.exists(File(dataFolder, "config.yml").toPath()))
        {
            saveResource("config.yml", false)
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

        /* Injector initialization */
        val injector = InjectorBuilder().addDefaultHandlers("me.ebonjaeger.perworldinventory").create()
        injector.register(PerWorldInventory::class, this)
        injector.register(Server::class, server)
        injector.provide(PluginFolder::class, dataFolder)
        injector.provide(DataDirectory::class, DATA_DIRECTORY)
        injector.registerProvider(DataSource::class, DataSourceProvider::class)
        val settings = Settings.create(File(dataFolder, "config.yml"))
        injector.register(Settings::class, settings)
        injectServices(injector)

        ConsoleLogger.setUseDebug(settings.getProperty(PluginSettings.DEBUG_MODE))

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

        val commandManager = BukkitCommandManager(this)
        commandManager.registerCommand(PWIBaseCommand())
        commandManager.registerCommand(HelpCommand(this))

        // Start bStats metrics
        if (settings.getProperty(MetricsSettings.ENABLE_METRICS))
        {
            startMetrics(settings, injector.getSingleton(GroupManager::class))
        }

        // Start task to prevent item duping across worlds
        updateTimeoutsTaskId = server.scheduler.scheduleSyncRepeatingTask(
                this, UpdateTimeoutsTask(this), 1L, 1L
        )

        ConsoleLogger.debug("PerWorldInventory is enabled and debug-mode is active!");
    }

    override fun onDisable()
    {
        isShuttingDown = true
        updateTimeoutsTaskId = -1
        timeouts.clear()
        server.scheduler.cancelTasks(this)
    }

    internal fun injectServices(injector: Injector)
    {
        injector.getSingleton(PermissionManager::class)
        injector.getSingleton(GroupManager::class)
        injector.getSingleton(ProfileManager::class)

        server.pluginManager.registerEvents(injector.getSingleton(InventoryCreativeListener::class), this)
        server.pluginManager.registerEvents(injector.getSingleton(PlayerChangedWorldListener::class), this)
        server.pluginManager.registerEvents(injector.getSingleton(PlayerQuitListener::class), this)
        server.pluginManager.registerEvents(injector.getSingleton(PlayerTeleportListener::class), this)
    }

    /**
     * Start sending metrics information to bStats. If so configured, this
     * will send the number of configured groups and the number of worlds on
     * the server.
     *
     * @param settings The settings for sending group and world information
     */
    private fun startMetrics(settings: Settings, groupManager: GroupManager)
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
        val gson = GsonBuilder().setPrettyPrinting().create()
        server.scheduler.runTaskAsynchronously(this, {
            FileWriter(WORLDS_CONFIG_FILE).use {
                it.write(gson.toJson(root))
            }
        })
    }
}
