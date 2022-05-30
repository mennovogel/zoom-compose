package nl.birdly.zoombox.util

import kotlin.math.max
import kotlin.math.min

object Calculator {

    fun calculateMaxTranslation(
        scale: Float,
        imageSize: Int
    ) = max(0f, scale * imageSize - imageSize)

    // ⚠️ Experimental
    fun calculateMaxTranslation(
        scale: Float,
        parentImageSize: Int,
        childImageSize: Int
    ) = max(0f, scale * childImageSize - parentImageSize)

    fun calculateOverEdgeTranslationRange(
            translationRange: ClosedFloatingPointRange<Float>,
            imageViewSize: Int
    ) = (translationRange.start - imageViewSize * .5f)..
            (translationRange.endInclusive + imageViewSize * .5f)

    /**
     * Calculate the translation (x or y) we want to animate to after a double tap.
     *
     * @param futureScale The scale we want to animate to
     * @param touchPoint The x or y value of the double tap.
     * @param currentViewSize The current image view size (width or height).
     */
    fun calculateFutureTranslation(
        futureScale: Float,
        touchPoint: Float,
        currentViewSize: Int
    ): Float {
        /**
         * The maximum and minimum translation the ImageView should have (with the futureScale),
         * so the View is still fully visible.
         */
        val translationRange = calculateMaxTranslation(
            futureScale,
            currentViewSize
        ).let {
            -it..0f
        }

        /**
         * The maximum and minimum translation the ImageView should have (with the futureScale)
         * when the view should not be fully visible, but only half of it is visible.
         */
        val overEdgeTranslationRange = calculateOverEdgeTranslationRange(
            translationRange,
            currentViewSize
        )

        /**
         * Now that we have the touchPointRelativeToImageView, we can calculate what this
         * would be with within the new translation values.
         * with "touchPointRelativeToImageView / currentViewSize" we calculate the x in a
         * number between 0.0 and 1.0.
         * with "maxTranslation - minTranslation" we calculate the difference between the
         * lowest possible translation value and the highest possible value, the answer is
         * somewhere in between. We multiply this with the previous number.
         * then with "- maxTranslation" we correct this, because the minimum translation is
         * not 0, but somewhere lower then that.
         */
        val uncorrectedResult = -(
                (touchPoint / currentViewSize) *
                        (overEdgeTranslationRange.endInclusive - overEdgeTranslationRange.start)
                        - overEdgeTranslationRange.endInclusive
                )

        return uncorrectedResult.limitByRange(translationRange)
    }

    // ⚠️ Experimental
    /**
     * Calculate the translation (x or y) we want to animate to after pan to keep the view in
     * bounds.
     *
     * @param scale The scale we want to animate to
     * @param currentViewSize The current image view size (width or height).
     */
    fun keepTranslationWithinBounds(
        translation: Float,
        scale: Float,
        parentZoomViewSize: Int,
        childZoomViewSize: Int
    ): Float {
        /**
         * The maximum and minimum translation the ImageView should have (with the futureScale),
         * so the View is still fully visible.
         */
        val translationRange = calculateMaxTranslation(
            scale,
            parentZoomViewSize,
            childZoomViewSize
        ).let {
            0f..it
        }

        return translation.limitByRange(translationRange)
    }

    private fun Float.limitByRange(range: ClosedFloatingPointRange<Float>) =
        max(range.start, min(range.endInclusive, this))
}