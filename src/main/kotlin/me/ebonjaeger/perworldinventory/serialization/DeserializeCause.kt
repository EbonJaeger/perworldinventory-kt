package me.ebonjaeger.perworldinventory.serialization

/**
 * Enum with all of the possible causes for a player's data to be
 * deserialized and applied.
 */
enum class DeserializeCause
{

    /**
     * The player changed worlds.
     */
    WORLD_CHANGE,

    /**
     * The player changed their GameMode.
     */
    GAMEMODE_CHANGE
}