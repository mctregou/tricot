package com.tregouet.knitting_images.utils

import android.media.ExifInterface
import java.io.IOException
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.BitmapFactory
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created by mariececile.tregouet on 27/01/2018.
 */
class ImageUtils {
    companion object {

        fun getOrientation(path: String): Int {
            var exif: ExifInterface? = null
            try {
                exif = ExifInterface(path)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return exif!!.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED)
        }

        fun rotateBitmap(url: String, orientation: Int): Bitmap? {

            var bitmap = getBitmapFromURL(url)

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_NORMAL -> return bitmap
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                    matrix.setRotate(180f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    matrix.setRotate(90f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    matrix.setRotate(-90f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
                else -> return bitmap
            }
            try {
                val bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap?.width!!, bitmap.height, matrix, true)
                bitmap.recycle()
                return bmRotated
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
                return null
            }

        }

        fun getBitmapFromURL(imgUrl: String): Bitmap? {
            try {
                val url = URL(imgUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                return BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                // Log exception
                return null
            }

        }
    }
}