package me.ebonjaeger.perworldinventory.data

import ch.jalu.configme.properties.Property
import me.ebonjaeger.perworldinventory.configuration.PlayerSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import org.bukkit.entity.Player

/**
 * Properties that can be conditionally transferred from a [PlayerProfile] to a [Player],
 * depending on a configurable setting.
 */
enum class PlayerProperty(private val property: Property<Boolean>?,
                          private val accessors: ValueAccessors<out Any>) {

    ALLOW_FLIGHT(PlayerSettings.LOAD_ALLOW_FLIGHT,
        ValueAccessors(Player::setAllowFlight, PlayerProfile::allowFlight)),

    DISPLAY_NAME(PlayerSettings.LOAD_DISPLAY_NAME,
        ValueAccessors(Player::setDisplayName, PlayerProfile::displayName)),

    EXHAUSTION(PlayerSettings.LOAD_EXHAUSTION,
        ValueAccessors(Player::setExhaustion, PlayerProfile::exhaustion)),

    EXPERIENCE(PlayerSettings.LOAD_EXP,
        ValueAccessors(Player::setExp, PlayerProfile::experience)),

    FLYING(PlayerSettings.LOAD_FLYING,
        ValueAccessors(Player::setFlying, PlayerProfile::isFlying)),

    FOOD_LEVEL(PlayerSettings.LOAD_HUNGER,
        ValueAccessors(Player::setFoodLevel, PlayerProfile::foodLevel)),

    LEVEL(PlayerSettings.LOAD_LEVEL,
        ValueAccessors(Player::setLevel, PlayerProfile::level)),

    SATURATION(PlayerSettings.LOAD_SATURATION,
        ValueAccessors(Player::setSaturation, PlayerProfile::saturation)),

    FALL_DISTANCE(PlayerSettings.LOAD_FALL_DISTANCE,
        ValueAccessors(Player::setFallDistance, PlayerProfile::fallDistance)),

    FIRE_TICKS(PlayerSettings.LOAD_FIRE_TICKS,
        ValueAccessors(Player::setFireTicks, PlayerProfile::fireTicks)),

    MAXIMUM_AIR(PlayerSettings.LOAD_MAX_AIR,
        ValueAccessors(Player::setMaximumAir, PlayerProfile::maximumAir)),

    REMAINING_AIR(PlayerSettings.LOAD_REMAINING_AIR,
        ValueAccessors(Player::setRemainingAir, PlayerProfile::remainingAir));


    fun applyFromProfileToPlayerIfConfigured(profile: PlayerProfile, player: Player, settings: Settings) {
        if (settings.getProperty(property)) {
            accessors.applyFromProfileToPlayer(profile, player)
        }
    }

    private class ValueAccessors<T>(val playerSetter: (Player, T) -> Unit,
                                    val profileGetter: (PlayerProfile) -> T) {

        fun applyFromProfileToPlayer(profile: PlayerProfile, player: Player) {
            val value = profileGetter(profile)
            playerSetter(player, value)
        }
    }
}
