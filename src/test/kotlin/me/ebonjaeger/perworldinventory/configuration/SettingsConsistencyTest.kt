package me.ebonjaeger.perworldinventory.configuration

import ch.jalu.configme.configurationdata.ConfigurationDataBuilder
import ch.jalu.configme.resource.YamlFileReader
import com.google.common.collect.Sets
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isEmpty
import me.ebonjaeger.perworldinventory.TestHelper.getFromJar
import org.junit.jupiter.api.Test

/**
 * Tests that the config.yml file corresponds with the settings holder classes in the code.
 */
class SettingsConsistencyTest
{

    private val configData = ConfigurationDataBuilder.createConfiguration(
            PluginSettings::class.java,
            PlayerSettings::class.java,
            MetricsSettings::class.java)

    private val yamlReader = YamlFileReader(getFromJar("/config.yml"))

    @Test
    fun shouldContainAllPropertiesWithSameDefaultValue()
    {
        // given / when / then
        configData.properties.forEach {
            assertThat("config.yml does not have property for $it",
                    it.isPresent(yamlReader), equalTo(true))
            assertThat("config.yml does not have same default value for $it",
                    it.determineValue(yamlReader), equalTo(it.defaultValue))
        }
    }

    @Test
    fun shouldNotHaveUnknownProperties()
    {
        // given
        val keysInYaml = yamlReader.getKeys(true)

        val keysInCode = configData.properties.map { it.path }.toSet()

        // when / then
        val difference = Sets.difference(keysInYaml, keysInCode)
        assertThat("config.yml has unknown properties",
                difference, isEmpty)
    }
}
