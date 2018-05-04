package me.ebonjaeger.perworldinventory

/**
 * Object that holds utility methods.
 */
object Utils
{

    /**
     * Check if a server's version is the same as a given version or higher.
     *
     * @param version The server's version
     * @param major The major version number
     * @param minor The minor version number
     * @param patch The patch version number
     * @return True if the server is running the same version or newer
     */
    fun checkServerVersion(version: String, major: Int, minor: Int, patch: Int): Boolean
    {
        val versionNum = version.substring(version.indexOf('.') - 1,
                version.length - 1).trim()
        val parts = versionNum.split(".")

        try
        {
            if (parts[0].toInt() >= major)
            {
                return if (parts[1].toInt() == minor)
                {
                    if (parts.size == 2)
                    {
                        true
                    } else
                    {
                        parts[2].toInt() >= patch
                    }
                } else
                {
                    parts[1].toInt() > minor
                }
            }
        } catch (ex: NumberFormatException)
        {
            return false
        }

        return false
    }

    /**
     * Get the header to display at the top of the `worlds.yml`
     * configuration file. This is so we can easily save the header
     * when the file is modified using in-game commands without having
     * to clutter up another class or method.
     */
    fun getWorldsConfigHeader(): String
    {
        return "# In this file you define your groups and the worlds in them.\n" +
                "# Follow this format:\n" +
                "# groups:\n" +
                "#   default:\n" +
                "#     worlds:\n" +
                "#     - world\n" +
                "#     - world_nether\n" +
                "#     - world_the_end\n" +
                "#     default-gamemode: SURVIVAL\n" +
                "#   creative:\n" +
                "#     worlds:\n" +
                "#     - creative\n" +
                "#     default-gamemode: CREATIVE\n" +
                "#\n" +
                "# 'default' and 'creative' are the names of the groups\n" +
                "# worlds: is a list of all worlds in the group\n" +
                "# If you have 'manage-gamemodes' set to true in the main config, the server\n" +
                "# will use the 'default-gamemode' here to know what gamemode to put users in."
    }
}
