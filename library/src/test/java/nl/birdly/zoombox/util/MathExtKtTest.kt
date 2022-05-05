package nl.birdly.zoombox.util

import org.junit.Assert.assertEquals
import org.junit.Test

class MathExtKtTest {

    @Test
    fun `Lower then min returns min`() {
        assertEquals(1f, minMax(1f, 2f, 0f))
    }

    @Test
    fun `Min returns min`() {
        assertEquals(1f, minMax(1f, 2f, 1f))
    }

    @Test
    fun `Higher then max returns max`() {
        assertEquals(2f, minMax(1f, 2f, 3f))
    }

    @Test
    fun `Max returns max`() {
        assertEquals(2f, minMax(1f, 2f, 2f))
    }

    @Test
    fun `Inbetween returns inbetween`() {
        assertEquals(1.5f, minMax(1f, 2f, 1.5f))
    }
}