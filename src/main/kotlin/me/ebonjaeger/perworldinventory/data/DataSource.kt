package me.ebonjaeger.perworldinventory.data

import com.google.gson.JsonObject
import org.bukkit.Location
import org.bukkit.entity.Player

interface DataSource
{

    /**
     * Saves a player's information to the database.
     *
     * @param key The [ProfileKey] for this profile
     * @param player The [PlayerProfile] with the data
     */
    fun savePlayer(key: ProfileKey, player: PlayerProfile)

    /**
     * Save the location of a player when they log out or are kicked from the
     * server.
     *
     * @param player The player who logged out
     */
    fun saveLogout(player: Player)

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
     * @param key The [ProfileKey] to get the data for
     * @param player The [Player] that the data will be applied to
     * @return A [JsonObject] with all of the player's information
     */
    fun getPlayer(key: ProfileKey, player: Player): PlayerProfile?

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
