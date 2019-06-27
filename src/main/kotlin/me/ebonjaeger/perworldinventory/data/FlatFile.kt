package me.ebonjaeger.perworldinventory.data

import com.dumptruckman.bukkit.configuration.util.SerializationHelper
import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.initialization.DataDirectory
import me.ebonjaeger.perworldinventory.serialization.LocationSerializer
import me.ebonjaeger.perworldinventory.serialization.PlayerSerializer
import net.minidev.json.JSONObject
import net.minidev.json.JSONStyle
import net.minidev.json.parser.JSONParser
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import javax.inject.Inject

class FlatFile @Inject constructor(@DataDirectory private val dataDirectory: File) : DataSource
{

    @Suppress("UNCHECKED_CAST") // We know we serialize to a Map<String, Any> since we made it
    override fun savePlayer(key: ProfileKey, player: PlayerProfile)
    {
        val file = getFile(key)
        ConsoleLogger.fine("Saving data for player '${player.displayName}'")
        ConsoleLogger.debug("Data being saved in file '${file.path}'")

        try
        {
            createFileIfNotExists(file)
        } catch (ex: IOException)
        {
            if (ex !is FileAlreadyExistsException)
            {
                ConsoleLogger.severe("Error creating file '${file.path}':", ex)
                return
            }
        }

        ConsoleLogger.fine("Writing player data for player '${player.displayName}' to file")
        val map = SerializationHelper.serialize(player)
        val json = JSONObject(map as Map<String, *>)
        try
        {
            FileWriter(file).use { it.write(json.toJSONString(JSONStyle.LT_COMPRESS)) }
        } catch (ex: IOException)
        {
            ConsoleLogger.severe("Could not write data to file '$file':", ex)
        }
    }

    override fun saveLogout(player: Player)
    {
        val dir = File(dataDirectory, player.uniqueId.toString())
        val file = File(dir, "last-logout.json")

        try
        {
            createFileIfNotExists(file)
            val data = LocationSerializer.serialize(player.location)
            FileWriter(file).use { it.write(data.toJSONString(JSONStyle.LT_COMPRESS)) }
        } catch (ex: IOException)
        {
            if (ex !is FileAlreadyExistsException)
            {
                ConsoleLogger.severe("Error writing logout location for '${player.name}':", ex)
            }
        }
    }

    override fun saveLocation(player: Player, location: Location)
    {
        val dir = File(dataDirectory, player.uniqueId.toString())
        val file = File(dir, "last-locations.json")

        try
        {
            createFileIfNotExists(file)
            val data = LocationSerializer.serialize(location)
            val key = location.world!!.name // The server will never provide a null world in a Location

            // Get any existing data
            val parser = JSONParser(JSONParser.USE_INTEGER_STORAGE)
            FileReader(file).use { reader ->
                val root = parser.parse(reader) as JSONObject
                val locations = if (root.containsKey("locations"))
                {
                    root["locations"] as JSONObject
                } else
                {
                    JSONObject()
                }

                // If a location for this world already exists, remove it.
                if (locations.containsKey(key))
                {
                    locations.remove(key)
                }

                // Write the latest data to disk
                locations[key] = data
                root["locations"] = locations
                FileWriter(file).use { writer -> writer.write(root.toJSONString(JSONStyle.LT_COMPRESS)) }
            }
        } catch (ex: IOException)
        {
            if (ex !is FileAlreadyExistsException)
            {
                ConsoleLogger.severe("Error writing last location for '${player.name}':", ex)
            }
        }
    }

    override fun getPlayer(key: ProfileKey, player: Player): PlayerProfile?
    {
        val file = getFile(key)

        // If the file does not exist, the player hasn't been to this group before
        if (!file.exists())
        {
            return null
        }

        FileReader(file).use { reader ->
            val parser = JSONParser(JSONParser.USE_INTEGER_STORAGE)
            val data = parser.parse(reader) as JSONObject

            return if (data.containsKey("==")) { // Data is from ConfigurationSerialization
                SerializationHelper.deserialize(data) as PlayerProfile
            } else { // Old data format and methods
                PlayerSerializer.deserialize(data, player.name, player.inventory.size, player.enderChest.size)
            }
        }
    }

    override fun getLogout(player: Player): Location?
    {
        val dir = File(dataDirectory, player.uniqueId.toString())
        val file = File(dir, "last-logout.json")

        // This player is likely logging in for the first time
        if (!file.exists())
        {
            return null
        }

        FileReader(file).use {
            val parser = JSONParser(JSONParser.USE_INTEGER_STORAGE)
            val data = parser.parse(it) as JSONObject

            return LocationSerializer.deserialize(data)
        }
    }

    override fun getLocation(player: Player, world: String): Location?
    {
        val dir = File(dataDirectory, player.uniqueId.toString())
        val file = File(dir, "last-locations.json")

        // Clearly they haven't visited any other worlds yet
        if (!file.exists())
        {
            return null
        }

        FileReader(file).use {
            val parser = JSONParser(JSONParser.USE_INTEGER_STORAGE)
            val root = parser.parse(it) as JSONObject
            if (!root.containsKey("locations"))
            {
                // Somehow the file exists, but still no locations
                return null
            }

            val locations = root["locations"] as JSONObject
            return if (locations.containsKey(world))
            {
                LocationSerializer.deserialize(locations["world"] as JSONObject)
            } else
            {
                // They haven't been to this world before, so no data
                null
            }
        }
    }

    /**
     * Creates the given file if it doesn't exist.
     *
     * @param file The file to create if necessary
     * @return The given file (allows inline use)
     * @throws IOException If file could not be created
     */
    @Throws(IOException::class)
    private fun createFileIfNotExists(file: File): File
    {
        if (!file.exists())
        {
            if (!file.parentFile.exists())
            {
                Files.createDirectories(file.parentFile.toPath())
            }

            Files.createFile(file.toPath())
        }

        return file
    }

    /**
     * Get the data file for a player.
     *
     * @param key The [ProfileKey] to get the right file
     * @return The data file to read from or write to
     */
    private fun getFile(key: ProfileKey): File
    {
        val dir = File(dataDirectory, key.uuid.toString())
        return when(key.gameMode)
        {
            GameMode.ADVENTURE -> File(dir, key.group.name + "_adventure.json")
            GameMode.CREATIVE -> File(dir, key.group.name + "_creative.json")
            GameMode.SPECTATOR -> File(dir, key.group.name + "_spectator.json")
            GameMode.SURVIVAL -> File(dir, key.group.name + ".json")
        }
    }
}
