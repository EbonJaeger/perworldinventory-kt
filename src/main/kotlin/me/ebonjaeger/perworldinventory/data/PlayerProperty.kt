package me.ebonjaeger.perworldinventory.data

import ch.jalu.configme.properties.Property
import me.ebonjaeger.perworldinventory.configuration.PlayerSettings
import me.ebonjaeger.perworldinventory.configuration.Settings
import org.bukkit.entity.Player

enum class PlayerProperty(private val property: Property<Boolean>,
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
        ValueAccessors(Player::setFoodLevel, PlayerProfile::foodLevel));


    fun applyFromProfileToPlayer(profile: PlayerProfile, player: Player, settings: Settings) {
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