package me.ebonjaeger.perworldinventory.data.migration

import com.dumptruckman.bukkit.configuration.util.SerializationHelper
import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.Group
import me.ebonjaeger.perworldinventory.data.ProfileKey
import me.ebonjaeger.perworldinventory.serialization.PlayerSerializer
import net.minidev.json.JSONObject
import net.minidev.json.JSONStyle
import net.minidev.json.parser.JSONParser
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.*

const val ENDER_CHEST_SLOTS = 27
const val INVENTORY_SLOTS = 41
const val MAX_MIGRATIONS_PER_TICK = 10

class MigrationTask (private val migrationService: MigrationService,
                     private val offlinePlayers: Array<out OfflinePlayer>,
                     private val dataDirectory: File,
                     private val groups: Collection<Group>) : BukkitRunnable() {

    private val migrateQueue: Queue<OfflinePlayer> = LinkedList<OfflinePlayer>()

    private var index = 0
    private var migrated = 0

    override fun run() {
        // Calculate our stopping index for this run
        val stopIndex =  if (index + MAX_MIGRATIONS_PER_TICK < offlinePlayers.size) { // Use index + constant if the result isn't more than the total number of players
            index + MAX_MIGRATIONS_PER_TICK
        } else { // Index would be greater than number of players, so just use the size of the array
            offlinePlayers.size
        }

        if (index >= offlinePlayers.size) { // No more players to migrate
            migrationService.finishMigration(migrated)
            cancel()
        }

        // Add players to a queue to be migrated
        while (index < stopIndex) {
            migrateQueue.offer(offlinePlayers[index])
            index++
        }

        while (migrateQueue.isNotEmpty()) { // Iterate over the queue
            val player = migrateQueue.poll()
            migrate(player)
        }

        if (index % 100 == 0) { // Print migration status every 100 players (about every 5 seconds)
            ConsoleLogger.info("Migration progress: $index/${offlinePlayers.size}")
        }
    }

    @Suppress("UNCHECKED_CAST") // Safe to assume our own Map types
    private fun migrate(player: OfflinePlayer) {
        val name = player.name

        if (!player.hasPlayedBefore() || name == null) { // It is likely that this player has never actually joined the server
            return
        }

        for (group in groups) { // Loop through all groups
            for (gameMode in GameMode.values()) { // Loop through all GameMode's
                if (gameMode == GameMode.SPECTATOR) { // Spectator mode doesn't have an inventory
                    continue
                }

                val key = ProfileKey(player.uniqueId, group, gameMode)
                val file = getFile(key)

                if (!file.exists()) { // Player hasn't been in this group or GameMode before
                    continue
                }

                FileReader(file).use { reader -> // Read the old data from the file
                    val parser = JSONParser(JSONParser.USE_INTEGER_STORAGE)
                    val data = parser.parse(reader) as JSONObject

                    if (data.containsKey("==")) { // This profile has already been migrated
                        return@use
                    } else if (!data.containsKey("data-format") || (data["data-format"] as Int) < 2) { // Profile is way too old to migrate
                        return@use
                    }

                    val profile = PlayerSerializer.deserialize(data, name, INVENTORY_SLOTS, ENDER_CHEST_SLOTS)
                    val map = SerializationHelper.serialize(profile)
                    val json = JSONObject(map as Map<String, *>)

                    try { // Write the newly-serialized data back to the file
                        FileWriter(file).use { writer -> writer.write(json.toJSONString(JSONStyle.LT_COMPRESS)) }
                        migrated++
                    } catch (ex: IOException) {
                        ConsoleLogger.severe("Could not write data to file '$file' during migration:", ex)
                    }
                }
            }
        }
    }

    /**
     * Get the data file for a player.
     *
     * @param key The [ProfileKey] to get the right file
     * @return The data file to read from or write to
     */
    private fun getFile(key: ProfileKey): File {
        val dir = File(dataDirectory, key.uuid.toString())
        return when(key.gameMode) {
            GameMode.ADVENTURE -> File(dir, key.group.name + "_adventure.json")
            GameMode.CREATIVE -> File(dir, key.group.name + "_creative.json")
            GameMode.SPECTATOR -> File(dir, key.group.name + "_spectator.json")
            GameMode.SURVIVAL -> File(dir, key.group.name + ".json")
        }
    }
}
