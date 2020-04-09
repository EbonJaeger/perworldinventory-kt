package me.ebonjaeger.perworldinventory

import java.util.logging.Logger

/**
 * Static logger.
 */
object ConsoleLogger
{

    private var logger: Logger? = null
    private var logLevel: LogLevel = LogLevel.INFO

    fun setLogger(logger: Logger)
    {
        ConsoleLogger.logger = logger
    }

    fun setLogLevel(logLevel: LogLevel)
    {
        ConsoleLogger.logLevel = logLevel
    }

    /**
     * Log a SEVERE message.
     *
     * @param message The message to log
     */
    fun severe(message: String)
    {
        logger?.severe(message)
    }

    /**
     * Log a SEVERE message with the cause of an error.
     *
     * @param message The message to log
     * @param cause The cause of an error
     */
    fun severe(message: String, cause: Throwable)
    {
        logger?.severe(message + " " + formatThrowable(cause))
        cause.printStackTrace()
    }

    /**
     * Log a WARN message.
     *
     * @param message The message to log
     */
    fun warning(message: String)
    {
        logger?.warning(message)
    }

    /**
     * Log a WARN message with the cause of an error.
     *
     * @param message The message to log
     * @param cause The cause of an error
     */
    fun warning(message: String, cause: Throwable)
    {
        logger?.warning(message + " " + formatThrowable(cause))
        cause.printStackTrace()
    }

    /**
     * Log an INFO message.
     *
     * @param message The message to log
     */
    fun info(message: String)
    {
        logger?.info(message)
    }

    /**
     * Log a FINE message if enabled.
     * <p>
     * Implementation note: this logs a message on INFO level because
     * levels below INFO are disabled by Bukkit/Spigot.
     *
     * @param message The message to log
     */
    fun fine(message: String)
    {
        if (logLevel.includes(LogLevel.FINE))
        {
            logger?.info("[FINE] $message")
        }
    }

    /**
     * Log a DEBUG message if enabled.
     * <p>
     * Implementation note: this logs a message on INFO level and prefixes it with "DEBUG" because
     * levels below INFO are disabled by Bukkit/Spigot.
     *
     * @param message The message to log
     */
    fun debug(message: String)
    {
        if (logLevel.includes(LogLevel.DEBUG))
        {
            logger?.info("[DEBUG] $message")
        }
    }

    private fun formatThrowable(throwable: Throwable): String
    {
        return "[" + throwable.javaClass.simpleName + "] " + throwable.message
    }
}
