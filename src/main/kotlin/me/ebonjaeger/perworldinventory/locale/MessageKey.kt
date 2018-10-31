package me.ebonjaeger.perworldinventory.locale

enum class MessageKey(private val key: String, vararg tags: String)
{

    /*
     * General command messages
     */


    /*
     * Group command messages
     */
    UNKNOWN_GROUP("groups.unknown_group", "%group"),
    NAME_EXISTS("groups.name_exists", "%name"),
    INVALID_GAMEMODE("groups.invalid_gamemode", "%game_mode"),
    CREATED_SUCCESSFULLY("groups.created_successfully"),
    UNKNOWN_WORLD("groups.unknown_world", "%world"),
    MUST_BE_PLAYER("groups.must_be_player"),
    ADDED_WORLD("groups.added_world", "%world", "%group"),
    REMOVED_GROUP("groups.removed_group"),
    WORLD_NOT_IN_GROUP("groups.world_not_in_group", "%group", "%world"),
    WORLD_REMOVED("groups.world_removed", "%world", "%group"),
    RESPAWN_NOT_IN_GROUP("groups.respawn_not_in_group"),
    RESPAWN_SET("groups.respawn_world_set", "%group");

    private val tags: Array<out String> = tags

    fun getKey(): String
    {
        return key
    }

    fun getTags(): Array<out String>
    {
        return tags
    }
}
