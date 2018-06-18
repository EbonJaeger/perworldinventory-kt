package me.ebonjaeger.perworldinventory

import ch.jalu.configme.properties.Property
import me.ebonjaeger.perworldinventory.configuration.Settings
import me.ebonjaeger.perworldinventory.data.DataSource
import me.ebonjaeger.perworldinventory.data.DataSourceProvider
import me.ebonjaeger.perworldinventory.initialization.DataDirectory
import me.ebonjaeger.perworldinventory.initialization.Injector
import me.ebonjaeger.perworldinventory.initialization.InjectorBuilder
import me.ebonjaeger.perworldinventory.initialization.PluginFolder
import me.ebonjaeger.perworldinventory.listener.player.InventoryCreativeListener
import me.ebonjaeger.perworldinventory.listener.player.PlayerQuitListener
import me.ebonjaeger.perworldinventory.listener.player.PlayerTeleportListener
import me.ebonjaeger.perworldinventory.service.BukkitService
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.PluginLogger
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import org.bukkit.scheduler.BukkitScheduler
import org.hamcrest.core.IsNot.not
import org.hamcrest.core.IsNull.nullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.File
import java.util.logging.Logger
import kotlin.reflect.KClass


/**
 * Test for the initialization of [PerWorldInventory].
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(PerWorldInventory::class, Settings::class)
class PerWorldInventoryInitializationTest {

    @Rule
    val temporaryFolder = TemporaryFolder()

    @Mock
    private lateinit var server: Server

    @Mock
    private lateinit var settings: Settings

    @Mock
    private lateinit var pluginManager: PluginManager

    private lateinit var pluginFolder: File
    private lateinit var plugin: PerWorldInventory

    @Before
    fun mockServer() {
        pluginFolder = temporaryFolder.newFolder()

        setField(Bukkit::class, null, "server", server)
        given(server.logger).willReturn(mock(Logger::class.java))
        given(server.scheduler).willReturn(mock(BukkitScheduler::class.java))
        given(server.pluginManager).willReturn(pluginManager)
        given(server.version).willReturn("1.9.4-RC1")

        given(settings.getProperty(any(Property::class.java))).willAnswer { invocation -> (invocation.arguments[0] as Property<*>).defaultValue }

        // PluginDescriptionFile is final and so cannot be mocked
        val descriptionFile = PluginDescriptionFile(
                "PerWorldInventory", "N/A", PerWorldInventory::class.java.canonicalName)
        val pluginLoader = JavaPluginLoader(server)
        plugin = PerWorldInventory(pluginLoader, descriptionFile, pluginFolder, null)
        setField(JavaPlugin::class, plugin, "logger", mock(PluginLogger::class.java))
    }

    @Test
    fun shouldInitializeObjectsProperly() {
        val injector = InjectorBuilder().addDefaultHandlers("me.ebonjaeger.perworldinventory").create()
        injector.register(PerWorldInventory::class, plugin)
        injector.register(Server::class, server)
        injector.register(PluginManager::class, pluginManager)
        injector.provide(PluginFolder::class, pluginFolder)
        injector.provide(DataDirectory::class, File(pluginFolder, "data"))
        injector.register(Settings::class, settings)
        injector.registerProvider(DataSource::class, DataSourceProvider::class)

        // when
        plugin.injectServices(injector)

        // then
        assertThat(injector.getIfAvailable(GroupManager::class), not(nullValue()))
        assertThat(injector.getIfAvailable(BukkitService::class), not(nullValue()))

        verifyListenerWasRegistered(PlayerQuitListener::class, injector)
        verifyListenerWasRegistered(InventoryCreativeListener::class, injector)
        verifyListenerWasRegistered(PlayerTeleportListener::class, injector)
    }

    private fun verifyListenerWasRegistered(clazz: KClass<out Listener>, injector: Injector) {
        val listener = injector.getIfAvailable(clazz)
        assertThat("Listener available for $clazz", listener, not(nullValue()))
        Mockito.verify(pluginManager).registerEvents(listener, plugin)
    }

    private fun <T: Any> setField(clazz: KClass<out T>, instance: T?, fieldName: String, value: Any?) {
        val field = clazz.java.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(instance, value)
    }
}
