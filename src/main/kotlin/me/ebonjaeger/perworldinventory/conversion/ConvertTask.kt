package me.ebonjaeger.perworldinventory.conversion

import com.onarandombox.multiverseinventories.WorldGroup
import com.onarandombox.multiverseinventories.profile.PlayerProfile
import com.onarandombox.multiverseinventories.profile.ProfileTypes
import com.onarandombox.multiverseinventories.share.Sharables
import me.ebonjaeger.perworldinventory.ConsoleLogger
import me.ebonjaeger.perworldinventory.GroupManager
import me.ebonjaeger.perworldinventory.data.PlayerDefaults
import me.ebonjaeger.perworldinventory.data.ProfileKey
import me.ebonjaeger.perworldinventory.data.migration.ENDER_CHEST_SLOTS
import me.ebonjaeger.perworldinventory.data.migration.INVENTORY_SLOTS
import me.ebonjaeger.perworldinventory.serialization.InventoryHelper
import me.ebonjaeger.perworldinventory.serialization.PotionSerializer
import net.minidev.json.JSONObject
import net.minidev.json.JSONStyle
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.util.*

const val MAX_CONVERSIONS_PER_TICK = 10

/**
 * Task to convert player data from MultiVerse-Inventories to PWI.
 *
 * @property convertService The [ConvertService] running this task.
 * @property groupManager The PerWorldInventory [GroupManager].
 * @property sender The [CommandSender] that started the conversion.
 * @property offlinePlayers All [OfflinePlayer]s on the server.
 * @property multiVerseGroups Groups from MultiVerse-Inventories.
 * @property dataDirectory The directory where player data is stored.
 */
class ConvertTask (private val convertService: ConvertService,
                   private val groupManager: GroupManager,
                   private val sender: CommandSender,
                   private val offlinePlayers: Array<out OfflinePlayer>,
                   private val multiVerseGroups: List<WorldGroup>,
                   private val dataDirectory: File) : BukkitRunnable() {

    private val queue: Queue<OfflinePlayer> = LinkedList<OfflinePlayer>()

    private var index = 0
    private var converted = 0

    override fun run() {
        // Calculate our stopping index for this run
        val stopIndex =  if (index + MAX_CONVERSIONS_PER_TICK < offlinePlayers.size) { // Use index + constant if the result isn't more than the total number of players
            index + MAX_CONVERSIONS_PER_TICK
        } else { // Index would be greater than number of players, so just use the size of the array
            offlinePlayers.size
        }

        if (index >= offlinePlayers.size) { // No more players to migrate
            convertService.finishConversion(converted)
            cancel()
        }

        // Add players to a queue to be migrated
        while (index < stopIndex) {
            queue.offer(offlinePlayers[index])
            index++
        }

        while (queue.isNotEmpty()) { // Iterate over the queue
            val player = queue.poll()
            convert(player)
        }

        if (index % 100 == 0) { // Print migration status every 100 players (about every 5 seconds)
            ConsoleLogger.info("Conversion progress: $index/${offlinePlayers.size}")
        }
    }

    /**
     * Converts data from the MultiVerse-Inventories format to the PWI format.
     *
     * @param player The player to convert.
     */
    private fun convert(player: OfflinePlayer) {
        val profileTypes = arrayOf(ProfileTypes.ADVENTURE,
                ProfileTypes.CREATIVE,
                ProfileTypes.SURVIVAL)

        multiVerseGroups.forEach { mvGroup ->
            val ourGroup = groupManager.getGroup(mvGroup.name)
            if (ourGroup == null) {
                ConsoleLogger.warning("Trying to convert to a group that doesn't exist! Not converting to group '${mvGroup.name}'!")
                return
            }

            profileTypes.forEach { type ->
                val gameMode = GameMode.valueOf(type.name)

                try {
                    val playerProfile = mvGroup.groupProfileContainer.getPlayerData(type, player)
                    if (playerProfile != null) {
                        val key = ProfileKey(player.uniqueId, ourGroup, gameMode)
                        val file = getFile(key)

                        if (!file.parentFile.exists()) {
                            Files.createDirectory(file.parentFile.toPath())
                        }
                        if (!file.exists()) {
                            Files.createFile(file.toPath())
                        }

                        val data = mvToPwi(playerProfile, gameMode)
                        FileWriter(file).use { writer -> writer.write(data.toJSONString(JSONStyle.LT_COMPRESS)) }
                    }
                } catch (ex: Exception) {
                    ConsoleLogger.severe("Error converting data for '${player.name}' in group '${ourGroup.name}' for GameMode '$gameMode", ex)
                }
            }
        }
    }

    private fun mvToPwi(profile: PlayerProfile, gameMode: GameMode): JSONObject {
        val map = linkedMapOf<String, Any>()

        // Inventory and armor
        val contents = arrayOf<ItemStack>()
        val armor = arrayOf<ItemStack>()
        if (profile[Sharables.INVENTORY] == null || profile[Sharables.ARMOR] == null) { // No inventory or armor data saved my MV-I
            contents.fill(ItemStack(Material.AIR), toIndex = INVENTORY_SLOTS)
            armor.fill(ItemStack(Material.AIR), toIndex = 4)
        } else { // Get the inventory contents from MV-I
            contents.plus(profile[Sharables.INVENTORY])
            armor.plus(profile[Sharables.ARMOR])
        }

        val inventory = linkedMapOf(Pair("contents", InventoryHelper.serializeInventory(contents)), Pair("armor", InventoryHelper.serializeInventory(armor)))

        // Ender chest
        val enderChestContents = arrayOf<ItemStack>()
        if (profile[Sharables.ENDER_CHEST] == null) {
            enderChestContents.fill(ItemStack(Material.AIR), toIndex = ENDER_CHEST_SLOTS)
        } else {
            enderChestContents.plus(profile[Sharables.ENDER_CHEST])
        }
        val enderChest = InventoryHelper.serializeInventory(enderChestContents)

        // Player stats
        val stats = linkedMapOf<String, Any>()

        stats["can-fly"] = !(gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE) // Only sets to true for Creative
        stats["display-name"] = profile.player.name ?: "Unknown"
        stats["exhaustion"] = profile[Sharables.EXHAUSTION] ?: PlayerDefaults.EXHAUSTION
        stats["experience"] = profile[Sharables.EXPERIENCE] ?: PlayerDefaults.EXPERIENCE
        stats["flying"] = !(gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE) // Only sets to true for Creative
        stats["food"] = profile[Sharables.FOOD_LEVEL] ?: PlayerDefaults.FOOD_LEVEL
        stats["gamemode"] = gameMode.toString()
        val health = profile[Sharables.HEALTH] ?: PlayerDefaults.HEALTH
        stats["max-health"] = if (health > PlayerDefaults.HEALTH) health else PlayerDefaults.HEALTH
        stats["health"] = health
        stats["level"] = profile[Sharables.LEVEL] ?: PlayerDefaults.LEVEL
        stats["saturation"] = profile[Sharables.SATURATION] ?: PlayerDefaults.SATURATION
        stats["fallDistance"] = profile[Sharables.FALL_DISTANCE] ?: PlayerDefaults.FALL_DISTANCE
        stats["fireTicks"] = profile[Sharables.FIRE_TICKS] ?: PlayerDefaults.FIRE_TICKS
        stats["maxAir"] = profile[Sharables.MAXIMUM_AIR] ?: PlayerDefaults.MAXIMUM_AIR
        stats["remainingAir"] = profile[Sharables.REMAINING_AIR] ?: PlayerDefaults.REMAINING_AIR

        val potionEffects = mutableListOf<PotionEffect>()
        if (profile[Sharables.POTIONS] != null) {
            potionEffects.addAll(profile[Sharables.POTIONS])
        }

        // Economy
        val balance = profile[Sharables.ECONOMY] ?: 0.0

        map["=="] = ConfigurationSerialization.getAlias(me.ebonjaeger.perworldinventory.data.PlayerProfile::class.java)
        map["data-format"] = 4
        map["inventory"] = inventory
        map["ender-chest"] = enderChest
        map["stats"] = stats
        map["potion-effects"] = PotionSerializer.serialize(potionEffects)
        map["balance"] = balance

        return JSONObject(map)
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
