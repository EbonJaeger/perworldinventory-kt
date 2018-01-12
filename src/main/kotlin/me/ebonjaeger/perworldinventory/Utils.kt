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
}
