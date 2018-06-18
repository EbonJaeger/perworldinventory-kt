package me.ebonjaeger.perworldinventory

import java.util.logging.Logger

/**
 * Static logger.
 */
object ConsoleLogger
{

    private var logger: Logger? = null
    private var useDebug: Boolean = false

    fun setLogger(logger: Logger)
    {
        ConsoleLogger.logger = logger
    }

    fun setUseDebug(useDebug: Boolean)
    {
        ConsoleLogger.useDebug = useDebug
    }

    fun severe(message: String)
    {
        logger?.severe(message)
    }

    fun severe(message: String, cause: Throwable)
    {
        logger?.severe(message + " " + formatThrowable(cause))
        cause.printStackTrace()
    }

    fun warning(message: String)
    {
        logger?.warning(message)
    }

    fun warning(message: String, cause: Throwable)
    {
        logger?.warning(message + " " + formatThrowable(cause))
        cause.printStackTrace()
    }

    fun info(message: String)
    {
        logger?.info(message)
    }

    fun info(message: String, cause: Throwable)
    {
        logger?.info(message + " " + formatThrowable(cause))
        cause.printStackTrace()
    }

    fun debug(message: String)
    {
        if (useDebug)
        {
            logger?.info("[DEBUG] $message")
        }
    }

    fun debug(message: String, cause: Throwable)
    {
        if (useDebug)
        {
            debug(message + " " + formatThrowable(cause))
            cause.printStackTrace()
        }
    }

    private fun formatThrowable(throwable: Throwable): String
    {
        return "[" + throwable.javaClass.simpleName + "] " + throwable.message
    }
}
