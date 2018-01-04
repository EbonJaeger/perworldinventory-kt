package me.ebonjaeger.perworldinventory.data

import com.google.gson.JsonObject
import me.ebonjaeger.perworldinventory.Group
import me.ebonjaeger.perworldinventory.PlayerInfo
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player

interface DataSource
{

    /**
     * Saves a player's information to the database.
     *
     * @param group The [Group] the player was in
     * @param gameMode The [GameMode] the player was in
     * @param player The [PlayerInfo] with the data
     */
    fun savePlayer(group: Group, gameMode: GameMode, player: PlayerInfo)

    /**
     * Save the location of a player when they log out or are kicked from the
     * server.
     *
     * @param player The player who logged out
     */
    fun saveLogout(player: PlayerInfo)

    /**
     * Save the location of a player when they teleport to a different world.
     *
     * @param player The player that teleported
     * @param location The location of the player on teleport
     */
    fun saveLocation(player: Player, location: Location)

    /**
     * Retrieves a player's data from the database.
     *
     * @param group The [Group] the player was in
     * @param gameMode The [GameMode] the player was in
     * @param player The [Player] that the data will be applied to
     * @return A [JsonObject] with all of the player's information
     */
    // TODO: Find a better way of doing this
    fun getPlayer(group: Group, gameMode: GameMode, player: Player): JsonObject?

    /**
     * Get the name of the world that a player logged out in.
     * If this is their first time logging in, this method will return null
     * instead of a location.
     *
     * @param player The player to get the last logout for
     * @return The location of the player when they last logged out
     */
    fun getLogout(player: Player): Location?

    /**
     * Get a player's last location in a world. If a player has never been to
     * the world before, this method will return null.
     *
     * @param player The player to get the last location of
     * @param world The name of the world the player is going to
     * @return The last location in the world where the player was standing
     */
    fun getLocation(player: Player, world: String): Location?
}
