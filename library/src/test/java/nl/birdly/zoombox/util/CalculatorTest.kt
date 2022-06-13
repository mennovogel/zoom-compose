package nl.birdly.zoombox.util

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.abs
import kotlin.math.absoluteValue

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
        assertEquals(0f, abs(Calculator.keepTranslationWithinBounds(
            -10000f,
            1f,
            100,
            100
        )))
        assertEquals(0f, abs(Calculator.keepTranslationWithinBounds(
            10000f,
            1f,
            100,
            100
        )))
        assertEquals(0f, abs(Calculator.keepTranslationWithinBounds(
            200f,
            1f,
            100,
            50
        )))
    }

    @Test
    fun keepTranslationWithinBounds_withZoomWithingBounds_keepsImageInCenter() {
        // 2039 * 0.5 == 1019.5 -> 1019.5 / 2 == -509.75f
        assertEquals(-509.75f, Calculator.keepTranslationWithinBounds(
            0f,
            1.5f,
            2039,
            720
        ))
        assertEquals(-1019.5f, Calculator.keepTranslationWithinBounds(
            0f,
            2f,
            2039,
            720
        ))
    }

    @Test
    fun keepTranslationWithinBounds_withZoomOutOfBounds_keepsImageOnTop() {
        // 4 * 720 == 2880
        // 2880 - 2039 == 841
        // 841 / 2 == 420.5
        // 2039 * 3 / 2 == 3058.5
        // 3058.5 - 420.5 == 2618
        // 3058.5 + 420.5 == 3479

        assertEqualsRounded(-2638f, Calculator.keepTranslationWithinBounds(
            0f,
            4f,
            2039,
            720
        ))

        assertEqualsRounded(-3479f, Calculator.keepTranslationWithinBounds(
            -4000f,
            4f,
            2039,
            720
        ))
    }

    private fun assertEqualsRounded(expected: Float, actual: Float, accuracy: Float = 0.0001f) {
        var isNotEqual = false
        if ((expected - actual).absoluteValue > accuracy) isNotEqual = true
        if (isNotEqual) throw AssertionError("$expected is not equal to $actual")
    }
}