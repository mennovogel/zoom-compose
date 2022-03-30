package nl.birdly.zoom.util

import android.widget.ImageView
import kotlin.math.max
import kotlin.math.min

object Calculator {

    fun calculateMaxTranslation(
            scale: Float,
            imageSize: Int,
            imageViewSize: Int
    ) = max(0f, scale * imageSize - imageViewSize)

    fun calculateOverEdgeTranslationRange(
            translationRange: ClosedFloatingPointRange<Float>,
            imageViewSize: Int
    ) = (translationRange.start - imageViewSize * .5f)..
            (translationRange.endInclusive + imageViewSize * .5f)

    fun calculateImageSize(imageView: ImageView): Pair<Int, Int>? {
        if (imageView.drawable == null) return null

        val widthScale = imageView.drawable.intrinsicWidth.toFloat() / imageView.width
        val heightScale = imageView.drawable.intrinsicHeight.toFloat() / imageView.height

        return if (widthScale > heightScale) {
            Pair(imageView.width, (imageView.drawable.intrinsicHeight / widthScale).toInt())
        } else {
            Pair((imageView.drawable.intrinsicWidth / heightScale).toInt(), imageView.height)
        }
    }

    /**
     * Calculate the translation (x or y) we want to animate to after a double tap.
     *
     * @param futureScale The scale we want to animate to
     * @param touchPoint The x or y value of the double tap.
     * @param currentTranslation The current translation (x or y) value of the image view.
     * @param currentViewSize The current image view size (width or height).
     * @param currentViewScale The current image view scale (x or y).
     */
    fun calculateFutureTranslation(
        imageSize: Int,
        futureScale: Float,
        touchPoint: Float,
        currentTranslation: Float,
        currentViewSize: Int,
        currentViewScale: Float
    ): Float {
        /**
         * The maximum and minimum translation the ImageView should have (with the futureScale),
         * so the View is still fully visible.
         */
        val translationRange = calculateMaxTranslation(futureScale, imageSize, currentViewSize).let {
            -it..0f
        }

        /**
         * The maximum and minimum translation the ImageView should have (with the futureScale)
         * when the view should not be fully visible, but only half of it is visible.
         */
        val overEdgeTranslationRange = calculateOverEdgeTranslationRange(translationRange, currentViewSize)

        // The maximum translation at this moment.
        val currentMaxTranslation = (currentViewScale - 1) / 2 * currentViewSize

        /**
         * Calculate the [touchPoint] back to how it would be if the scale was 1.0,
         * so it is relative to the image view size.
         */
        val touchPointRelativeToImageView
                = ((-currentTranslation) + currentMaxTranslation + touchPoint) /
                currentViewScale

        /**
         * When the imageSize is smaller then the currentViewSize, we need to correct our
         * calculations so that the pixels outsize the image are not used to calculate the
         * relative position.
         */
        val fixedTouchPointRelativeToImageView = if (imageSize < currentViewSize) {
            val correction = (currentViewSize - imageSize) / 2f
            if (touchPointRelativeToImageView > 0f) {
                max(0f, touchPointRelativeToImageView - correction)
            } else {
                min(0f, touchPointRelativeToImageView + correction)
            }
        } else {
            touchPointRelativeToImageView
        }

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
                (fixedTouchPointRelativeToImageView / imageSize) * (overEdgeTranslationRange.endInclusive - overEdgeTranslationRange.start)
                        - overEdgeTranslationRange.endInclusive
                )

        return uncorrectedResult.limitByRange(translationRange)// - (imageSize / futureScale)
//        return uncorrectedResult
    }

    private fun Float.limitByRange(range: ClosedFloatingPointRange<Float>) =
        max(range.start, min(range.endInclusive, this))
}