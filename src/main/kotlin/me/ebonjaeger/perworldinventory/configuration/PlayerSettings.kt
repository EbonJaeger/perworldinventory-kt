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

    @Comment("Save and load players' economy balances. Requires Vault!")
    val USE_ECONOMY = newProperty("player.economy", false)

    @Comment("Load players' ender chests")
    val LOAD_ENDER_CHEST = newProperty("player.ender-chest", true)

    @Comment("Load players' inventory")
    val LOAD_INVENTORY = newProperty("player.inventory", true)

    @Comment("Load if a player is able to fly")
    val LOAD_ALLOW_FLIGHT = newProperty("player.stats.can-fly", true)

    @Comment("Load the player's display name")
    val LOAD_DISPLAY_NAME = newProperty("player.stats.display-name", false)

    @Comment("Load a player's exhaustion level")
    val LOAD_EXHAUSTION = newProperty("player.stats.exhaustion", true)

    @Comment("Load how much exp a player has")
    val LOAD_EXP = newProperty("player.stats.exp", true)

    @Comment("Load a player's hunger level")
    val LOAD_HUNGER = newProperty("player.stats.food", true)

    @Comment("Load if a player is flying")
    val LOAD_FLYING = newProperty("player.stats.flying", true)

    @Comment(
        "Load what gamemode a player is in. This is shadow-set to false if",
        "'manage-gamemodes' is true, to stop infinite loop")
    val LOAD_GAMEMODE = newProperty("player.stats.gamemode", false)

    @Comment("Load how much health a player has")
    val LOAD_HEALTH = newProperty("player.stats.health", true)

    @Comment("Load what level the player is")
    val LOAD_LEVEL = newProperty("player.stats.level", true)

    @Comment("Load all the potion effects of the player")
    val LOAD_POTION_EFFECTS = newProperty("player.stats.potion-effects", true)

    @Comment("Load the saturation level of the player")
    val LOAD_SATURATION = newProperty("player.stats.saturation", true)

    @Comment("Load a player's fall distance")
    val LOAD_FALL_DISTANCE = newProperty("player.stats.fall-distance", true)

    @Comment("Load the fire ticks a player has")
    val LOAD_FIRE_TICKS = newProperty("player.stats.fire-ticks", true)

    @Comment("Load the maximum amount of air a player can have")
    val LOAD_MAX_AIR = newProperty("player.stats.max-air", true)

    @Comment("Load the current remaining air a player has")
    val LOAD_REMAINING_AIR = newProperty("player.stats.remaining-air", true)

    @SectionComments
    fun buildSectionComments(): MutableMap<String, Array<out String>>
    {
        val comments = HashMap<String, Array<out String>>()
        comments.put("player", arrayOf("All settings for players are here:"))
        comments.put("player.stats", arrayOf("All options for player stats are here:"))

        return comments
    }
}
