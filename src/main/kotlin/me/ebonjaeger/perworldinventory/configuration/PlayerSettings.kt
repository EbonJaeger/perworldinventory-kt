package me.ebonjaeger.perworldinventory.configuration

import ch.jalu.configme.Comment
import ch.jalu.configme.SectionComments
import ch.jalu.configme.SettingsHolder
import ch.jalu.configme.properties.PropertyInitializer.newProperty

/**
 * Object to hold settings about what to change on a player when they
 * teleport to a different world group.
 */
object PlayerSettings : SettingsHolder
{

    @JvmField
    @Comment("Save and load players' economy balances. Requires Vault!")
    val USE_ECONOMY = newProperty("player.economy", false)

    @JvmField
    @Comment("Load players' ender chests")
    val LOAD_ENDER_CHEST = newProperty("player.ender-chest", true)

    @JvmField
    @Comment("Load players' inventory")
    val LOAD_INVENTORY = newProperty("player.inventory", true)

    @JvmField
    @Comment("Load if a player is able to fly")
    val LOAD_ALLOW_FLIGHT = newProperty("player.stats.can-fly", true)

    @JvmField
    @Comment("Load the player's display name")
    val LOAD_DISPLAY_NAME = newProperty("player.stats.display-name", false)

    @JvmField
    @Comment("Load a player's exhaustion level")
    val LOAD_EXHAUSTION = newProperty("player.stats.exhaustion", true)

    @JvmField
    @Comment("Load how much exp a player has")
    val LOAD_EXP = newProperty("player.stats.exp", true)

    @JvmField
    @Comment("Load a player's hunger level")
    val LOAD_HUNGER = newProperty("player.stats.food", true)

    @JvmField
    @Comment("Load if a player is flying")
    val LOAD_FLYING = newProperty("player.stats.flying", true)

    @JvmField
    @Comment(
        "Load what gamemode a player is in. This is shadow-set to false if",
        "'manage-gamemodes' is true, to stop infinite loop")
    val LOAD_GAMEMODE = newProperty("player.stats.gamemode", false)

    @JvmField
    @Comment("Load how much health a player has")
    val LOAD_HEALTH = newProperty("player.stats.health", true)

    @JvmField
    @Comment("Load what level the player is")
    val LOAD_LEVEL = newProperty("player.stats.level", true)

    @JvmField
    @Comment("Load all the potion effects of the player")
    val LOAD_POTION_EFFECTS = newProperty("player.stats.potion-effects", true)

    @JvmField
    @Comment("Load the saturation level of the player")
    val LOAD_SATURATION = newProperty("player.stats.saturation", true)

    @JvmField
    @Comment("Load a player's fall distance")
    val LOAD_FALL_DISTANCE = newProperty("player.stats.fall-distance", true)

    @JvmField
    @Comment("Load the fire ticks a player has")
    val LOAD_FIRE_TICKS = newProperty("player.stats.fire-ticks", true)

    @JvmField
    @Comment("Load the maximum amount of air a player can have")
    val LOAD_MAX_AIR = newProperty("player.stats.max-air", true)

    @JvmField
    @Comment("Load the current remaining air a player has")
    val LOAD_REMAINING_AIR = newProperty("player.stats.remaining-air", true)

    @JvmStatic
    @SectionComments
    fun buildSectionComments(): MutableMap<String, Array<out String>>
    {
        val comments = HashMap<String, Array<out String>>()
        comments.put("player", arrayOf("All settings for players are here:"))
        comments.put("player.stats", arrayOf("All options for player stats are here:"))

        return comments
    }
}
