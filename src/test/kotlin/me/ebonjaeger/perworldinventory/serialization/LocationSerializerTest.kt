package me.ebonjaeger.perworldinventory.serialization

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import net.minidev.json.JSONObject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(Bukkit::class)
@RunWith(PowerMockRunner::class)
class LocationSerializerTest
{

    @Before
    fun createMocks()
    {
        mockStatic(Bukkit::class.java)
    }

    @Test
    fun verifySerializedLocation()
    {
        // given
        val world = mock(World::class.java)
        given(Bukkit.getWorld("test")).willReturn(world)
        given(world.name).willReturn("test")

        val loc = Location(world, 134.523, 64.0, -3876.26437, 432.67F, 32.63413F)

        // when
        val json = LocationSerializer.serialize(loc)

        // then
        val result = LocationSerializer.deserialize(json)
        assertHasSameProperties(result, loc)
    }

    @Test
    fun deserializedOldDataCorrectly() {
        // given
        val world = mock(World::class.java)
        given(Bukkit.getWorld("test")).willReturn(world)
        given(world.name).willReturn("test")

        val expected = Location(world, 14521.3, 14.0, -2352.121, 123.3F, -2352.532F)

        val obj = JSONObject()
        obj["world"] = "test"
        obj["x"] = 14521.3
        obj["y"] = 14.0
        obj["z"] = -2352.121
        obj["yaw"] = 123.3F
        obj["pitch"] = -2352.532F

        // when
        val actual = LocationSerializer.deserialize(obj)

        // then
        assertHasSameProperties(actual, expected)
    }

    private fun assertHasSameProperties(given: Location, expected: Location)
    {
        assertThat(given.world!!.name, equalTo(expected.world!!.name)) // We created them, we know they're not null!
        assertThat(given.x.toFloat(), equalTo(expected.x.toFloat()))
        assertThat(given.y.toFloat(), equalTo(expected.y.toFloat()))
        assertThat(given.z.toFloat(), equalTo(expected.z.toFloat()))
        assertThat(given.pitch, equalTo(expected.pitch))
        assertThat(given.yaw, equalTo(expected.yaw))
    }
}
