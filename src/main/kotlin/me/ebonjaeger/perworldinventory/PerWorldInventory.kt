package me.ebonjaeger.perworldinventory

import co.aikar.commands.PaperCommandManager
import me.ebonjaeger.perworldinventory.api.PerWorldInventoryAPI
import me.ebonjaeger.perworldinventory.command.*
import me.ebonjaeger.perworldinventory.configuration.MetricsSettings
import me.ebonjaeger.perworldinventory.configuration.PluginSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import me.ebonjaeger.perworldinventory.data.DataSource
import me.ebonjaeger.perworldinventory.data.DataSourceProvider
import me.ebonjaeger.perworldinventory.data.PlayerProfile
import me.ebonjaeger.perworldinventory.initialization.DataDirectory
import me.ebonjaeger.perworldinventory.initialization.Injector
import me.ebonjaeger.perworldinventory.initialization.InjectorBuilder
import me.ebonjaeger.perworldinventory.initialization.PluginFolder
import me.ebonjaeger.perworldinventory.listener.entity.EntityPortalEventListener
import me.ebonjaeger.perworldinventory.listener.player.*
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File
import java.nio.file.Files
import java.util.*

class PerWorldInventory : JavaPlugin
{

    /**
     * Get whether or not the server is currently shutting down.
     */
    var isShuttingDown = false
        private set

    /**
     * Get an API class for other plugins to more easily
     * integrate with PerWorldInventory.
     */
    var api: PerWorldInventoryAPI? = null
        private set

    val timeouts = hashMapOf<UUID, Int>()
    var updateTimeoutsTaskId = -1

    private val DATA_DIRECTORY = File(dataFolder, "data")
    val SLOT_TIMEOUT = 5
    val WORLDS_CONFIG_FILE = File(dataFolder, "worlds.yml")

    constructor(): super()

    /* Constructor used for tests. */
    internal constructor(loader: JavaPluginLoader, description: PluginDescriptionFile, dataFolder: File, file: File)
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

        /* Injector initialization */
        val injector = InjectorBuilder().addDefaultHandlers("me.ebonjaeger.perworldinventory").create()
        injector.register(PerWorldInventory::class, this)
        injector.register(Server::class, server)
        injector.register(PluginManager::class, server.pluginManager)
        injector.provide(PluginFolder::class, dataFolder)
        injector.provide(DataDirectory::class, DATA_DIRECTORY)
        injector.registerProvider(DataSource::class, DataSourceProvider::class)
        val settings = Settings.create(File(dataFolder, "config.yml"))
        injector.register(Settings::class, settings)

        ConsoleLogger.setLogLevel(settings.getProperty(PluginSettings.LOGGING_LEVEL))

        // Inject and register all the things
        setupGroupManager(injector)
        injectServices(injector)
        registerCommands(injector, injector.getSingleton(GroupManager::class))

        // Start bStats metrics
        if (settings.getProperty(MetricsSettings.ENABLE_METRICS))
        {
            startMetrics(settings, injector.getSingleton(GroupManager::class))
        }

        // Start task to prevent item duping across worlds
        updateTimeoutsTaskId = server.scheduler.scheduleSyncRepeatingTask(
                this, UpdateTimeoutsTask(this), 1L, 1L
        )

        // ConfigurationSerializable classes must be registered as such
        ConfigurationSerialization.registerClass(PlayerProfile::class.java)

        ConsoleLogger.fine("PerWorldInventory is enabled with logger level '${settings.getProperty(PluginSettings.LOGGING_LEVEL).name}'")
    }

    override fun onDisable()
    {
        isShuttingDown = true
        updateTimeoutsTaskId = -1
        timeouts.clear()
        server.scheduler.cancelTasks(this)
    }

    private fun setupGroupManager(injector: Injector)
    {
        val groupManager = injector.getSingleton(GroupManager::class)

        if (!Files.exists(WORLDS_CONFIG_FILE.toPath()))
        {
            saveResource("worlds.yml", false)
        }

        groupManager.loadGroups()
    }

    internal fun injectServices(injector: Injector)
    {
        server.pluginManager.registerEvents(injector.getSingleton(InventoryCreativeListener::class), this)
        server.pluginManager.registerEvents(injector.getSingleton(PlayerChangedWorldListener::class), this)
        server.pluginManager.registerEvents(injector.getSingleton(PlayerGameModeChangeListener::class), this)
        server.pluginManager.registerEvents(injector.getSingleton(PlayerQuitListener::class), this)
        server.pluginManager.registerEvents(injector.getSingleton(PlayerTeleportListener::class), this)
        server.pluginManager.registerEvents(injector.getSingleton(EntityPortalEventListener::class), this)
        server.pluginManager.registerEvents(injector.getSingleton(PlayerDeathListener::class), this)
        server.pluginManager.registerEvents(injector.getSingleton(PlayerRespawnListener::class), this)

        // The PlayerSpawnLocationEvent is only fired in Spigot
        // As of version 1.9.2
        if (Bukkit.getVersion().contains("Spigot") && Utils.checkServerVersion(Bukkit.getVersion(), 1, 9, 2))
        {
            server.pluginManager.registerEvents(injector.getSingleton(PlayerSpawnLocationListener::class), this)
        }

        api = injector.getSingleton(PerWorldInventoryAPI::class)
    }

    private fun registerCommands(injector: Injector, groupManager: GroupManager)
    {
        val commandManager = PaperCommandManager(this)

        commandManager.commandCompletions.registerAsyncCompletion(
                "@groups") { groupManager.groups.keys }

        // CommandHelp#showHelp() uses an unstable method internally
        commandManager.enableUnstableAPI("help")

        commandManager.registerCommand(HelpCommand(this))
        commandManager.registerCommand(injector.getSingleton(ReloadCommand::class))
        commandManager.registerCommand(injector.getSingleton(ConvertCommand::class))
        commandManager.registerCommand(injector.getSingleton(GroupCommands::class))
        commandManager.registerCommand(injector.getSingleton(MigrateCommand::class))
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
            bStats.addCustomChart(Metrics.SimplePie("num_groups") {
                val numGroups = groupManager.groups.size

                return@SimplePie numGroups.toString()
            })
        }

        if (settings.getProperty(MetricsSettings.SEND_NUM_WORLDS))
        {
            // Get the total number of worlds (configured or not)
            bStats.addCustomChart(Metrics.SimplePie("num_worlds") {
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
            })
        }
    }
}
