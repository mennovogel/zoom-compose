package nl.birdly.zoom.util

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.abs

class CalculatorTest {

    @Test
    fun testCalculateMaxTranslation_withoutZoom_isCorrect() {
        val result: Float = Calculator.calculateMaxTranslation(
            1f,
            100,
            100
        )
        assertEquals(0f, result)
    }

    @Test
    fun testCalculateMaxTranslation_withDoubleZoom_isCorrect() {
        val result: Float = Calculator.calculateMaxTranslation(
            2f,
            100,
            100
        )
        assertEquals(100f, result)
    }

    @Test
    fun testCalculateMaxTranslation_withTripleZoom_isCorrect() {
        val result: Float = Calculator.calculateMaxTranslation(
            3f,
            100,
            100
        )
        assertEquals(200f, result)
    }

    @Test
    fun testCalculateOverEdgeTranslationRange_withoutZoom_isCorrect() {
        val result = Calculator.calculateOverEdgeTranslationRange(0f..0f, 100)
        assertEquals(-50f..50f, result)
    }

    @Test
    fun testCalculateOverEdgeTranslationRange_withDoubleZoom_isCorrect() {
        val result = Calculator.calculateOverEdgeTranslationRange(-100f..0f, 100)
        assertEquals(-150f..50f, result)
    }

    @Test
    fun testCalculateOverEdgeTranslationRange_withTripleZoom_isCorrect() {
        val result = Calculator.calculateOverEdgeTranslationRange(-200f..0f, 100)
        assertEquals(-250f..50f, result)
    }

    @Test
    fun testCalculateFutureTranslation_inCenter_returnsCenterTranslation() {
        val result = Calculator.calculateFutureTranslation(
            2f,
            50f,
            0f,
            100,
            1f
        )
        assertEquals(-50f, result)
    }

    @Test
    fun testCalculateFutureTranslation_inAlmostCenter_returnsAlmostCenterTranslation() {
        val result = Calculator.calculateFutureTranslation(
            2f,
            40f,
            0f,
            100,
            1f
        )
        // Use abs, because it doesn't matter if it's -0 or 0.
        assertEquals(-30f, result)
    }

    @Test
    fun testCalculateFutureTranslation_onLeftEdge_returnsLeftTranslation() {
        val result = Calculator.calculateFutureTranslation(
            2f,
            0f,
            0f,
            100,
            1f
        )
        // Results in 50
        assertEquals(0f, result)
    }

    @Test
    fun testCalculateFutureTranslation_onLeftSide_returnsLeftTranslation() {
        val result = Calculator.calculateFutureTranslation(
            2f,
            10f, // 10 should still be enough to zoom the the edge
            0f,
            100,
            1f
        )
        // Results in -50
        assertEquals(0f, result)
    }

    @Test
    fun testCalculateFutureTranslation_onExactLeftSide_returnsLeftTranslation() {
        val result = Calculator.calculateFutureTranslation(
            2f,
            25f,
            0f,
            100,
            1f
        )
        assertEquals(0f, abs(result))
    }
}