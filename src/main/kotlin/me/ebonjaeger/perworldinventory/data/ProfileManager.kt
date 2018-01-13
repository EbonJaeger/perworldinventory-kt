package me.ebonjaeger.perworldinventory.data

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.Group
import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.configuration.PluginSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit

class ProfileManager(private val plugin: PerWorldInventory,
                     private val dataSource: DataSource,
                     private val settings: Settings)
{

    private val profileCache: Cache<ProfileKey, PlayerProfile> = CacheBuilder.newBuilder()
            .expireAfterAccess(settings.getProperty(PluginSettings.CACHE_DURATION).toLong(), TimeUnit.MINUTES)
            .maximumSize(settings.getProperty(PluginSettings.CACHE_MAX_LIMIT).toLong())
            .build()

    private val profileFactory = ProfileFactory(plugin)
    private val separateGameModes = settings.getProperty(PluginSettings.SEPARATE_GM_INVENTORIES)

    /**
     * Save a player in the database. Their profile will be cached for a period of time in order to save on disk I/O,
     * but they will still be saved immediately. Profiles will be saved asynchronously unless the server is shutting
     * down, in which case they wont be as plugins cannot schedule tasks in this state.
     *
     * @param player The player to store
     * @param group The group the player was in
     * @param gameMode The GameMode the player was in
     */
    fun addPlayerProfile(player: Player, group: Group, gameMode: GameMode)
    {
        val key = ProfileKey(player.uniqueId, group, if (separateGameModes) gameMode else GameMode.SURVIVAL)
        val profile = profileFactory.create(player)
        profileCache.put(key, profile)

        ConsoleLogger.debug("Saving player '${player.name}' to database with key: '$key'")

        if (!plugin.isShuttingDown)
        {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, { dataSource.savePlayer(key, profile) })
            profile.saved = true
        } else
        {
            dataSource.savePlayer(key, profile)
            profile.saved = true
        }
    }

    /**
     * Get a player's data for a given [Group] and [GameMode], and set it to the player.
     *
     * @param player The player to get stuff for
     * @param group The world group to load from
     * @param gameMode Which GameMode inventory to load
     */
    // TODO: This should return a PlayerProfile instead of just setting stuff
    fun getPlayerData(player: Player, group: Group, gameMode: GameMode)
    {
        val key = ProfileKey(player.uniqueId, group, if (separateGameModes) gameMode else GameMode.SURVIVAL)

        ConsoleLogger.debug("Checking cache for player data for '${player.name}' with key: $key")
        val cached = profileCache.getIfPresent(key)
        if (cached != null)
        {
            applyToPlayer(player, cached)
            return
        }

        ConsoleLogger.debug("Player '${player.name}' not in cache, loading from disk")
        Bukkit.getScheduler().runTaskAsynchronously(plugin, {
            val data = dataSource.getPlayer(key, player)
            Bukkit.getScheduler().runTask(plugin, {
                if (data != null)
                {
                    // TODO: Set player data from Json
                } else
                {
                    applyDefaults(player)
                }
            })
        })
    }

    private fun applyToPlayer(player: Player, profile: PlayerProfile)
    {
        // TODO: Apply stuff to the player
    }

    private fun applyDefaults(player: Player)
    {
        // TODO: Apply defaults to the player
    }
}
