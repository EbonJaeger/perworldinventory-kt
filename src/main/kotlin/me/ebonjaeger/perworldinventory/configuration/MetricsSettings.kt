package me.ebonjaeger.perworldinventory.configuration

import ch.jalu.configme.Comment
import ch.jalu.configme.SettingsHolder
import ch.jalu.configme.properties.PropertyInitializer.newProperty

/**
 * Object to hold settings for sending plugin metrics.
 */
object MetricsSettings : SettingsHolder
{

    @JvmField
    @Comment(
        "Choose whether or not to enable metrics sending.",
        "See https://bstats.org/getting-started for details."
    )
    val ENABLE_METRICS = newProperty("metrics.enable", true)

    @JvmField
    @Comment(
        "Send the number of configured groups.",
        "No group names will be sent!"
    )
    val SEND_NUM_GROUPS = newProperty("metrics.send-number-of-groups", true)

    @JvmField
    @Comment("Send the total number of worlds on the server.")
    val SEND_NUM_WORLDS = newProperty("metrics.send-number-of-worlds", true)
}
