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
            100,
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
            100,
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
            100,
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
            100,
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
            100,
            2f,
            25f,
            0f,
            100,
            1f
        )
        assertEquals(0f, abs(result))
    }

    @Test
    fun testCalculateFutureTranslation_inCenterWithSmallImage_returnsCenterTranslation() {
        val result = Calculator.calculateFutureTranslation(
            40,
            2f,
            30f,
            0f,
            60,
            1f
        )
        // Use abs, because it doesn't matter if it's -0 or 0.
        assertEquals(-10f, result)
    }

    @Test
    fun testCalculateFutureTranslation_onLeftEdgeWithSmallImage_returnsLeftTranslation() {
        val result = Calculator.calculateFutureTranslation(
            40,
            2f,
            0f,
            0f,
            60,
            1f
        )
        assertEquals(0f, result)
    }

    @Test
    fun testCalculateFutureTranslation_onLeftSideWithSmallImage_returnsLeftTranslation() {
        val result = Calculator.calculateFutureTranslation(
            40,
            2f,
            10f, // 10 should still be enough to zoom the the edge
            0f,
            60,
            1f
        )
        assertEquals(0f, result)
    }

    @Test
    fun testCalculateFutureTranslation_onExactLeftSideWithSmallImage_returnsLeftTranslation() {
        val result = Calculator.calculateFutureTranslation(
            40,
            2f,
            22.5f, // 22.5 should be exactly enough to zoom the the edge
            0f,
            60,
            1f
        )
        assertEquals(0f, result)
    }

    //TODO: Fix
    /*@Test
    fun testCalculateFutureTranslation_onExtremeScaledSmallImageEdge_returnsTranslation() {
        val result = Calculator.calculateFutureTranslation(
            100,
            10f,
            50f,
            0f,
            200,
            1f
        )
        assertEquals(400f, result)
    }*/

    //TODO: Fix
    /*@Test
    fun testCalculateFutureTranslation_onExtremeScaledSmallImageBottomEdge_returnsTranslation() {
        val result = Calculator.calculateFutureTranslation(
            100,
            10f,
            150f,
            0f,
            200,
            1f
        )
        assertEquals(-400f, result)
    }*/
}