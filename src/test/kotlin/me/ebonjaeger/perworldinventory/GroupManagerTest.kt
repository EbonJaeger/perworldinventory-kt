package me.ebonjaeger.perworldinventory

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * Test for [GroupManager].
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(PerWorldInventory::class)
class GroupManagerTest {

    @InjectMocks
    lateinit var groupManager: GroupManager

    @Mock
    lateinit var plugin: PerWorldInventory

    @Test
    fun shouldReturnAbsentValueForNonExistentGroup() {
        assertThat(groupManager.getGroup("test"), absent())
    }
}
