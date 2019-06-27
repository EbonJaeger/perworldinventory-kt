package me.ebonjaeger.perworldinventory.data

import ch.jalu.injector.factory.SingletonStore
import me.ebonjaeger.perworldinventory.ConsoleLogger
import javax.inject.Inject
import javax.inject.Provider

class DataSourceProvider @Inject constructor (private val dataSourceStore: SingletonStore<DataSource>) : Provider<DataSource>
{

    override fun get(): DataSource
    {
        try
        {
            return createDataSource()
        } catch (ex: Exception)
        {
            ConsoleLogger.severe("Unable to create data source:", ex)
            throw IllegalStateException("Error during initialization of data source", ex)
        }
    }

    private fun createDataSource(): DataSource
    {
        // Later on we will have logic here to differentiate between flatfile and MySQL.
        return dataSourceStore.getSingleton(FlatFile::class.java)
    }
}
