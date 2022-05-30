package nl.birdly.zoombox.util

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.abs

class CalculatorTest {

    @Test
    fun testCalculateMaxTranslation_withoutZoom_isCorrect() {
        val result: Float = Calculator.calculateMaxTranslation(
            1f,
            100
        )
        assertEquals(0f, result)
    }

    @Test
    fun testCalculateMaxTranslation_withDoubleZoom_isCorrect() {
        val result: Float = Calculator.calculateMaxTranslation(
            2f,
            100
        )
        assertEquals(100f, result)
    }

    @Test
    fun testCalculateMaxTranslation_withTripleZoom_isCorrect() {
        val result: Float = Calculator.calculateMaxTranslation(
            3f,
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
            100
        )
        assertEquals(-50f, result)
    }

    @Test
    fun testCalculateFutureTranslation_inCenterZoomOut_returnsCenterTranslation() {
        val result = Calculator.calculateFutureTranslation(
            1f,
            50f,
            100
        )
        assertEquals(0f, abs(result))
    }

    @Test
    fun testCalculateFutureTranslation_inRightEdgeZoomOut_returnsCenterTranslation() {
        val result = Calculator.calculateFutureTranslation(
            1.5f,
            100f,
            100
        )
        assertEquals(-50f, result)
    }

    @Test
    fun testCalculateTranslation_inRightEdgeZoomInToZoomIn_keepsSameTranslation() {
        val result = Calculator.calculateFutureTranslation(
            2f,
            1080f,
            1080
        )
        assertEquals(-1080f, result)
    }

    @Test
    fun testCalculateTranslation_inCenterZoomInToZoomIn_keepsSameTranslation() {
        val result = Calculator.calculateFutureTranslation(
            2f,
            540f,
            1080
        )
        assertEquals(-540f, result)
    }

    @Test
    fun testCalculateFutureTranslation_inAlmostCenter_returnsAlmostCenterTranslation() {
        val result = Calculator.calculateFutureTranslation(
            2f,
            40f,
            100
        )
        // Use abs, because it doesn't matter if it's -0 or 0.
        assertEquals(-30f, result)
    }

    @Test
    fun testCalculateFutureTranslation_onLeftEdge_returnsLeftTranslation() {
        val result = Calculator.calculateFutureTranslation(
            2f,
            0f,
            100
        )
        // Results in 50
        assertEquals(0f, result)
    }

    @Test
    fun testCalculateFutureTranslation_onLeftSide_returnsLeftTranslation() {
        val result = Calculator.calculateFutureTranslation(
            2f,
            10f, // 10 should still be enough to zoom the the edge
            100
        )
        // Results in -50
        assertEquals(0f, result)
    }

    @Test
    fun testCalculateFutureTranslation_onExactLeftSide_returnsLeftTranslation() {
        val result = Calculator.calculateFutureTranslation(
            2f,
            25f,
            100
        )
        assertEquals(0f, abs(result))
    }

    @Test
    fun keepTranslationWithinBounds_withoutZoom_returnsToCentre() {
        assertEquals(0f, abs(Calculator.keepTranslationWithinBounds(
            0f,
            1f,
            100,
            100
        )))
    }

    @Test
    fun keepTranslationWithinBounds_withoutZoomWithTranslation_returnsToCentre() {
        assertEquals(0f, abs(Calculator.keepTranslationWithinBounds(
            10000f,
            1f,
            100,
            100
        )))
    }

    @Test
    fun keepTranslationWithinBounds_withZoomWithoutTranslation_doesNotMove() {
        assertEquals(0f, abs(Calculator.keepTranslationWithinBounds(
            0f,
            2f,
            100,
            100
        )))
    }

    @Test
    fun keepTranslationWithinBounds_withZoomWithTranslation_returnsToEdge() {
        assertEquals(100f, abs(Calculator.keepTranslationWithinBounds(
            100000f,
            2f,
            100,
            100
        )))
    }

    @Test
    fun keepTranslationWithinBounds_withZoomWithNegativeTranslationWithLargeParent_returnsToEdge() {
        assertEquals(0f, abs(Calculator.keepTranslationWithinBounds(
            -100000f,
            2f,
            200,
            100
        )))
    }

    @Test
    fun keepTranslationWithinBounds_withZoomTranslationWithLargeParent_returnsToEdge() {
        assertEquals(0f, abs(Calculator.keepTranslationWithinBounds(
            100000f,
            2f,
            200,
            100
        )))
    }
}