package me.ebonjaeger.perworldinventory.data

import com.google.gson.Gson
import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.Group
import me.ebonjaeger.perworldinventory.PerWorldInventory
import me.ebonjaeger.perworldinventory.PlayerInfo
import me.ebonjaeger.perworldinventory.serialization.PlayerSerializer
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.util.*

class FlatFile(private val plugin: PerWorldInventory,
               private val serializer: PlayerSerializer) : DataSource
{

    override fun savePlayer(group: Group, gameMode: GameMode, player: PlayerInfo)
    {
        val file = getFile(group, gameMode, player.uuid)
        ConsoleLogger.debug("Saving data for player '${player.name}' in file '${file.path}'")

        try
        {
            creatFileIfNotExists(file)
        } catch (ex: IOException)
        {
            if (ex !is FileAlreadyExistsException)
            {
                ConsoleLogger.severe("Error creating file '${file.path}':", ex)
                return
            }
        }

        ConsoleLogger.debug("Writing player data for player '${player.name}' to file")
        val data = serializer.serialize(player)
        try
        {
            FileWriter(file).use { it.write(Gson().toJson(data)) }
        } catch (ex: IOException)
        {
            ConsoleLogger.severe("Could not write data to file '$file':", ex)
        }
    }

    override fun saveLogout(player: PlayerInfo)
    {
        TODO("not implemented")
    }

    override fun saveLocation(player: PlayerInfo)
    {
        TODO("not implemented")
    }

    override fun getPlayer(group: Group, gameMode: GameMode, player: Player)
    {
        TODO("not implemented")
    }

    override fun getLogout(player: Player): Location?
    {
        TODO("not implemented")
    }

    override fun getLocation(player: Player, world: String): Location?
    {
        TODO("not implemented")
    }

    /**
     * Creates the given file if it doesn't exist.
     *
     * @param file The file to create if necessary
     * @return The given file (allows inline use)
     * @throws IOException If file could not be created
     */
    @Throws(IOException::class)
    fun creatFileIfNotExists(file: File): File
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
     * @param group The group the player is in
     * @param gameMode The game mode for the group
     * @param uuid The UUID of the player
     * @return The data file to read from or write to
     */
    fun getFile(group: Group, gameMode: GameMode, uuid: UUID): File
    {
        val dir = File(plugin.DATA_DIRECTORY, uuid.toString())
        return when(gameMode)
        {
            GameMode.ADVENTURE -> File(dir, group.name + "_adventure.json")
            GameMode.CREATIVE -> File(dir, group.name + "_creative.json")
            GameMode.SPECTATOR -> File(dir, group.name + "_spectator.json")
            GameMode.SURVIVAL -> File(dir, group.name + ".json")
        }
    }
}
