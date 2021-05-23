package com.example.rocketcat.adapter

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/**
 * @author xres
 * @createDate 12/4/20
 */
const val ID = "xres_blur"

class BlurTransformation(
    val context: Context,
    private var blurRadius: Float,
    private val ratio: Float = 1f
) : BitmapTransformation() {


    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID.toByteArray())
    }

    override fun equals(other: Any?): Boolean {
        val otherO = other as? BlurTransformation
        return otherO?.blurRadius == blurRadius && otherO.ratio == ratio
    }

    override fun hashCode(): Int {
        return (ID + blurRadius.hashCode() + ratio.hashCode()).hashCode()
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {

        blurRadius = if (blurRadius > 25f) 25f else blurRadius
        //可变的原始bitmap
        val originBitmap = toTransform.copy(toTransform.config, true)
        //底部bitmap参数
        val offsetX = 0
        val offsetY = ((1 - ratio) * outHeight).toInt()
        val bWidth = outWidth
        val bHeight = (ratio * outHeight).toInt()
        val blurredBitmap = Bitmap.createBitmap(originBitmap, offsetX, offsetY, bWidth, bHeight)
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
        blurredBitmap.getPixels(pixels, 0, bWidth, 0, 0, bWidth, bHeight)
        originBitmap.setPixels(pixels, 0, bWidth, offsetX, offsetY, bWidth, bHeight)
        return originBitmap


    }


}
