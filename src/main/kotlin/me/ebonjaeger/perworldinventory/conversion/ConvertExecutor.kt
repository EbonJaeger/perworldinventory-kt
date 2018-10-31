package me.ebonjaeger.perworldinventory.conversion

import ch.jalu.injector.annotations.NoMethodScan
import com.onarandombox.multiverseinventories.ProfileTypes
import com.onarandombox.multiverseinventories.api.profile.PlayerProfile
import com.onarandombox.multiverseinventories.api.profile.WorldGroupProfile
import com.onarandombox.multiverseinventories.api.share.Sharables
import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.Group
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.initialization.DataDirectory
import me.ebonjaeger.perworldinventory.serialization.InventorySerializer
import me.ebonjaeger.perworldinventory.serialization.PotionSerializer
import net.minidev.json.JSONObject
import net.minidev.json.JSONStyle
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.potion.PotionEffect
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.util.*
import javax.inject.Inject

/**
 * Class that performs converting operations.
 */
@NoMethodScan
class ConvertExecutor @Inject constructor(private val groupManager: GroupManager,
                                          @DataDirectory private val dataDirectory: File)
{

    var mvGroups: List<WorldGroupProfile>? = null

    /**
     * Converts data from the MultiVerse-Inventories format to the PWI format.
     *
     * @param player The player to convert.
     */
    fun executeConvert(player: OfflinePlayer)
    {
        val profileTypes = arrayOf(ProfileTypes.ADVENTURE,
                ProfileTypes.CREATIVE,
                ProfileTypes.SURVIVAL)

        if (mvGroups == null)
        {
            throw IllegalStateException("Trying to convert with no groups from MV-I")
        }

        mvGroups!!.forEach { mvGroup ->
            val ourGroup = groupManager.getGroup(mvGroup.name)
            if (ourGroup == null)
            {
                ConsoleLogger.warning("Trying to convert to a group that doesn't exist! " +
                        "Not converting to group '${mvGroup.name}'!")
                return
            }

            profileTypes.forEach { type ->
                val gameMode = GameMode.valueOf(type.name)

                try
                {
                    val playerProfile = mvGroup.getPlayerData(type, player)
                    if (playerProfile != null)
                    {
                        val data = convertData(playerProfile)
                        val file = getFile(player.uniqueId, gameMode, ourGroup)

                        if (!file.parentFile.exists())
                            Files.createDirectory(file.parentFile.toPath())
                        if (!file.exists())
                            Files.createFile(file.toPath())

                        FileWriter(file).use { it.write(data.toJSONString(JSONStyle.LT_COMPRESS)) }
                    }
                } catch (ex: Exception)
                {
                    ConsoleLogger.severe("Error converting data for '${player.name}' " +
                            "with group '${ourGroup.name}' for GameMode '$gameMode", ex)
                }
            }
        }
    }

    private fun convertData(profile: PlayerProfile): JSONObject
    {
        val obj = JSONObject()
        obj["data-format"] = 2

        // Inventory and armor
        val inventory = JSONObject()
        if (profile[Sharables.INVENTORY] != null)
            inventory["inventory"] = InventorySerializer.serializeInventory(profile[Sharables.INVENTORY])
        if (profile[Sharables.ARMOR] != null)
            inventory["armor"] = InventorySerializer.serializeInventory(profile[Sharables.ARMOR])

        obj["inventory"] = inventory

        // Ender chest
        if (profile[Sharables.ENDER_CHEST] != null)
            obj["ender-chest"] = InventorySerializer.serializeInventory(profile[Sharables.ENDER_CHEST])

        // Player stats
        val stats = JSONObject()
        if (profile[Sharables.EXHAUSTION] != null)
            stats["exhaustion"] = profile[Sharables.EXHAUSTION]
        if (profile[Sharables.EXPERIENCE] != null)
            stats["experience"] = profile[Sharables.EXPERIENCE]
        if (profile[Sharables.FOOD_LEVEL] != null)
            stats["food"] = profile[Sharables.FOOD_LEVEL]
        if (profile[Sharables.HEALTH] != null)
            stats["health"] = profile[Sharables.HEALTH]
        if (profile[Sharables.LEVEL] != null)
            stats["level"] = profile[Sharables.LEVEL]
        if (profile[Sharables.POTIONS] != null)
        {
            val effects = mutableListOf<PotionEffect>()
            effects.addAll(profile[Sharables.POTIONS])
            stats["potion-effects"] = PotionSerializer.serialize(effects)
        }
        if (profile[Sharables.SATURATION] != null)
            stats["saturation"] = profile[Sharables.SATURATION]
        if (profile[Sharables.FALL_DISTANCE] != null)
            stats["fallDistance"] = profile[Sharables.FALL_DISTANCE]
        if (profile[Sharables.FIRE_TICKS] != null)
            stats["fireTicks"] = profile[Sharables.FIRE_TICKS]
        if (profile[Sharables.MAXIMUM_AIR] != null)
            stats["maxAir"] = profile[Sharables.MAXIMUM_AIR]
        if (profile[Sharables.REMAINING_AIR] != null)
            stats["remainingAir"] = profile[Sharables.REMAINING_AIR]

        obj["stats"] = stats

        // Economy stuffs
        if (profile[Sharables.ECONOMY] != null)
        {
            val econ = JSONObject()
            econ["balance"] = profile[Sharables.ECONOMY]
            obj["economy"] = econ
        }

        return obj
    }

    private fun getFile(uuid: UUID, gameMode: GameMode, group: Group): File
    {
        val userDir = File(dataDirectory, uuid.toString())
        return File(userDir, "${group.name}_${gameMode.toString().toLowerCase()}")
    }
}
