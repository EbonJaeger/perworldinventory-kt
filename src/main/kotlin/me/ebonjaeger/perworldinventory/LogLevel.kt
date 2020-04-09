package me.ebonjaeger.perworldinventory

/**
 * Log level.
 *
 * @param value the log level; the higher the number the more "important" the level.
 *              A log level enables its number and all above.
 */
enum class LogLevel(private val value: Int)
{

    /** Info: General messages. */
    INFO(3),

    /** Fine: More detailed messages that may still be interesting to plugin users. */
    FINE(2),

    /** Debug: Very detailed messages for debugging. */
    DEBUG(1);

    /**
     * Return whether the current log level includes the given log level.
     *
     * @param level the level to process
     * @return true if the level is enabled, false otherwise
     */
    fun includes(level: LogLevel): Boolean
            = value <= level.value
}
