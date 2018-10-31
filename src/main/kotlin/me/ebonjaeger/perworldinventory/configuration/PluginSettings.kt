package me.ebonjaeger.perworldinventory.configuration

import ch.jalu.configme.Comment
import ch.jalu.configme.SettingsHolder
import ch.jalu.configme.properties.Property
import ch.jalu.configme.properties.PropertyInitializer.newProperty
import me.ebonjaeger.perworldinventory.LogLevel

/**
 * Object to hold settings for general plugin operation.
 */
object PluginSettings : SettingsHolder
{

    @JvmField
    @Comment("Set the level of debug messages shown by PWI.",
            "INFO: Print general messages",
            "FINE: Print more detailed messages about what the plugin is doing",
            "DEBUG: Print detailed messages about everything")
    val LOGGING_LEVEL: Property<LogLevel>? = newProperty(LogLevel::class.java, "logging-level", LogLevel.INFO)

    @JvmField
    @Comment("Set the language used for plugin messages.",
            "List of languages supported can be found here: <link>")
    val LOCALE: Property<String>? = newProperty("messages-locale", "en")

    @JvmField
    @Comment(
        "If true, the server will change player's gamemodes when entering a world",
        "The gamemode set is configured in the worlds.yml file")
    val MANAGE_GAMEMODES: Property<Boolean>? = newProperty("manage-gamemodes", false)

    @JvmField
    @Comment("If true, players will have different inventories for each gamemode")
    val SEPARATE_GM_INVENTORIES: Property<Boolean>? = newProperty("separate-gamemode-inventories", true)

    @JvmField
    @Comment("If true, any worlds that are not in the worlds.yml configuration file will share the same inventory")
    val SHARE_IF_UNCONFIGURED: Property<Boolean>? = newProperty("share-if-unconfigured", false)

    @JvmField
    @Comment("True if PWI should set the respawn world when a player dies")
    val MANAGE_DEATH_RESPAWN: Property<Boolean>? = newProperty("manage-death-respawn", false)

    @JvmField
    @Comment(
        "Attempt to figure out which world a player last logged off in",
        "and save/load the correct data if that world is different.",
        "REQUIRES MC 1.9.2 OR NEWER")
    val LOAD_DATA_ON_JOIN: Property<Boolean>? = newProperty("load-data-on-join", false)

    @JvmField
    @Comment(
        "Disables bypass regardless of permission",
        "Defaults to false")
    val DISABLE_BYPASS: Property<Boolean>? = newProperty("disable-bypass", false)

    @JvmField
    @Comment("Set the duration in minutes for player profile information to be cached")
    val CACHE_DURATION: Property<Int>? = newProperty("cache-duration", 10)

    @JvmField
    @Comment("Set the maximum number of player profiles that can be cached at any given time")
    val CACHE_MAX_LIMIT: Property<Int>? = newProperty("cache-maximum-limit", 1000)

    @JvmField
    @Comment("Disables the nagging message when a world is created on the fly",
            "Intended for users who know what their doing, and don't need to have worlds configured")
    val DISABLE_NAG: Property<Boolean>? = newProperty("disable-nag-message", false)
}
