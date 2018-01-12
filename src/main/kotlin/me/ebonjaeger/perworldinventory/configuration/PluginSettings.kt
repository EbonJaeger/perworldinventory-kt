package me.ebonjaeger.perworldinventory.configuration

import ch.jalu.configme.Comment
import ch.jalu.configme.SettingsHolder
import ch.jalu.configme.properties.PropertyInitializer.newProperty

/**
 * Object to hold settings for general plugin operation.
 */
object PluginSettings : SettingsHolder
{

    @JvmField
    @Comment("Print out debug messages to the console for every event that happens in PWI")
    val DEBUG_MODE = newProperty("debug-mode", false)

    @JvmField
    @Comment(
            "Configure the amount of time between saves, in seconds",
            "Default is 5 minutes (300 seconds)")
    val SAVE_INTERVAL = newProperty("save-interval", 300)

    @JvmField
    @Comment(
        "If true, the server will change player's gamemodes when entering a world",
        "The gamemode set is configured in the worlds.yml file")
    val MANAGE_GAMEMODES = newProperty("manage-gamemodes", false)

    @JvmField
    @Comment("If true, players will have different inventories for each gamemode")
    val SEPERATE_GM_INVENTORIES = newProperty("separate-gamemode-inventories", true)

    @JvmField
    @Comment("If true, any worlds that are not in the worlds.yml configuration file will share the same inventory")
    val SHARE_IF_UNCONFIGURED = newProperty("share-if-unconfigured", false)

    @JvmField
    @Comment(
        "Attempt to figure out which world a player last logged off in",
        "and save/load the correct data if that world is different.",
        "REQUIRES MC 1.9.2 OR NEWER")
    val LOAD_DATA_ON_JOIN = newProperty("load-data-on-join", false)

    @JvmField
    @Comment(
        "Disables bypass regardless of permission",
        "Defaults to false")
    val DISABLE_BYPASS = newProperty("disable-bypass", false)

    @JvmField
    @Comment("Set the duration in minutes for player information loaded from the disk to be cached")
    val CACHE_DURATION = newProperty("cache-duration", 10)

    @JvmField
    @Comment("Set the maximum number of player profiles that can be cached at any given time",
            "This only applies to data loaded from the disk")
    val CACHE_MAX_LIMIT = newProperty("cache-maximum-limit", 1000)
}
