package me.ebonjaeger.perworldinventory.configuration

import ch.jalu.configme.configurationdata.ConfigurationDataBuilder
import com.google.common.collect.Sets
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import me.ebonjaeger.perworldinventory.TestHelper.getFromJar
import org.bukkit.configuration.file.YamlConfiguration
import org.junit.Test

/**
 * Tests that the config.yml file corresponds with the settings holder classes in the code.
 */
class SettingsConsistencyTest
{

    /** Bukkit's FileConfiguration#getKeys returns all inner nodes also. We want to exclude those in tests. */
    private val YAML_INNER_NODES = listOf("metrics", "player", "player.stats")

    private val configData = ConfigurationDataBuilder.collectData(
            PluginSettings::class.java,
            PlayerSettings::class.java,
            MetricsSettings::class.java)

    private val yamlConfig = YamlConfiguration.loadConfiguration(getFromJar("/config.yml"))

    @Test
    fun shouldContainAllPropertiesWithSameDefaultValue()
    {
        // given / when / then
        configData.properties.forEach {
            assertThat("config.yml does not have property for $it",
                    yamlConfig.contains(it.path), equalTo(true))

            assertThat("config.yml does not have same default value for $it",
                    it.defaultValue, equalTo(yamlConfig[it.path]))
        }
    }

    @Test
    fun shouldNotHaveUnknownProperties()
    {
        // given
        val keysInYaml = yamlConfig.getKeys(true)
        keysInYaml.removeAll(YAML_INNER_NODES)

        val keysInCode = configData.properties.map { it.path }.toSet()

        // when / then
        val difference = Sets.difference(keysInYaml, keysInCode)
        assertThat("config.yml has unknown properties",
                difference, hasSize(equalTo(0)))
    }
}
