package com.example.rocketcat.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.core.graphics.scale
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/**
 * @author xres
 * @createDate 12/4/20
 */
const val ID = "xres_blur"

/**
 * @param blurRadius 模糊半径
 * @param ratioX 模糊部分宽度比率,默认 0f to 1f，指整个宽度
 */
class BlurTransformation(
    val context: Context,
    private var blurRadius: Float,
    private val ratioX: Pair<Float, Float> = 0f to 1f,
    private val ratioY: Pair<Float, Float> = 0f to 1f,
    private val scaleFactor: Int = 4
) : BitmapTransformation() {

    private val uniKey: String
        get() {
            val (ratioLeft, ratioRight) = ratioX
            val (ratioTop, ratioBottom) = ratioY
            require(ratioLeft <= ratioRight) { "ratioX和ratioY Pair的两个值需要属于[0f,1f],且前面的值不能大于后面的值" }
            require(ratioTop <= ratioBottom) { "ratioX和ratioY Pair的两个值需要属于[0f,1f],且前面的值不能大于后面的值" }
            require(ratioLeft in 0f..1f) { "ratioX和ratioY Pair的两个值需要属于[0f,1f],且前面的值不能大于后面的值" }
            require(ratioTop in 0f..1f) { "ratioX和ratioY Pair的两个值需要属于[0f,1f],且前面的值不能大于后面的值" }
            require(ratioRight in 0f..1f) { "ratioX和ratioY Pair的两个值需要属于[0f,1f],且前面的值不能大于后面的值" }
            require(ratioBottom in 0f..1f) { "ratioX和ratioY Pair的两个值需要属于[0f,1f],且前面的值不能大于后面的值" }
            return ID + blurRadius + ratioLeft + ratioTop + ratioRight + ratioBottom
        }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) =
        messageDigest.update(uniKey.toByteArray())


    override fun equals(other: Any?): Boolean = (other as? BlurTransformation)?.let {
        it.blurRadius == blurRadius && it.ratioX == ratioX && it.ratioY == ratioY
    } ?: false


    override fun hashCode() = uniKey.hashCode()


    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {

        blurRadius = if (blurRadius > 25f) 25f else blurRadius
        //可变的原始bitmap
        val originBitmap = toTransform.copy(toTransform.config!!, true)
        //底部bitmap参数

        val (ratioLeft, ratioRight) = ratioX
        val (ratioTop, ratioBottom) = ratioY
        val rect = Rect(
            (originBitmap.width * ratioLeft).toInt(),
            (originBitmap.height * ratioTop).toInt(),
            (originBitmap.width * ratioRight).toInt(),
            (originBitmap.height * ratioBottom).toInt()
        )
        val (bWidth, bHeight) = (rect.right - rect.left) to (rect.bottom - rect.top)
        val blurredBitmap =
            Bitmap.createBitmap(originBitmap, rect.left, rect.top, bWidth, bHeight).run {
                scale(width / scaleFactor, height / scaleFactor, false)
            }

        val pixels = IntArray(bWidth * bHeight)
        val rs = RenderScript.create(context)

        // Allocate memory for Renderscript to work with
        val input: Allocation = Allocation.createFromBitmap(
            rs,
            blurredBitmap,
            Allocation.MipmapControl.MIPMAP_FULL,
            Allocation.USAGE_SHARED
        )
        val output: Allocation = Allocation.createTyped(rs, input.type)

        // Load up an instance of the specific script that we want to use.
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setInput(input)
        // Set the blur radius
        script.setRadius(blurRadius)
        // Start the ScriptIntrinisicBlur
        script.forEach(output)
        // Copy the output to the blurred bitmap
        output.copyTo(blurredBitmap)
        rs.destroy()
        output.destroy()
        script.destroy()
        blurredBitmap.scale(bWidth, bHeight, false)
            .getPixels(pixels, 0, bWidth, 0, 0, bWidth, bHeight)
        originBitmap.setPixels(pixels, 0, bWidth, rect.left, rect.top, bWidth, bHeight)
        return originBitmap


    }


}
