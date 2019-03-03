package com.tregouet.knitting_images.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ExifInterface
import java.io.IOException
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.util.Log
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.model.Image
import kotlinx.android.synthetic.main.activity_project.*
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.popup_picture_option.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


/**
 * Created by mariececile.tregouet on 27/01/2018.
 */
class ImageUtils {
    companion object {

        public fun openCameraOrFolders(activity : Activity, fileUri : Uri) {
            val dialog = Dialog(activity)
            dialog.setContentView(R.layout.popup_picture_option)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.take_picture.setOnClickListener {
                dialog.dismiss()
                validatePermissions(activity, fileUri)
            }

            dialog.choose_picture.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                if (intent.resolveActivity(activity.packageManager) != null) {
                    activity.startActivityForResult(intent, Constants().CHOOSE_PHOTO_REQUEST)
                }
            }

            dialog.cancel.setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
        }

        private fun validatePermissions(activity : Activity, fileUri : Uri) {
            Dexter.withActivity(activity)
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(object: PermissionListener {
                        override fun onPermissionGranted(
                                response: PermissionGrantedResponse?) {
                            launchCamera(activity, fileUri)
                        }

                        override fun onPermissionRationaleShouldBeShown(
                                permission: PermissionRequest?,
                                token: PermissionToken?) {
                            AlertDialog.Builder(activity)
                                    .setTitle(
                                            R.string.storage_permission_rationale_title)
                                    .setMessage(
                                            R.string.storage_permition_rationale_message)
                                    .setNegativeButton(
                                            android.R.string.cancel,
                                            { dialog, _ ->
                                                dialog.dismiss()
                                                token?.cancelPermissionRequest()
                                            })
                                    .setPositiveButton(android.R.string.ok,
                                            { dialog, _ ->
                                                dialog.dismiss()
                                                token?.continuePermissionRequest()
                                            })
                                    .setOnDismissListener({
                                        token?.cancelPermissionRequest() })
                                    .show()
                        }

                        override fun onPermissionDenied(
                                response: PermissionDeniedResponse?) {
                            /*Snackbar.make(subtitle,
                                    R.string.storage_permission_denied_message,
                                    Snackbar.LENGTH_LONG)
                                    .show()*/
                        }
                    })
                    .check()
        }

        private fun launchCamera(activity : Activity, fileUri : Uri) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(intent.resolveActivity(activity.packageManager) != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                activity.startActivityForResult(intent, Constants().TAKE_PHOTO_REQUEST)
            }
        }

        public fun processCapturedPhoto(activity: Activity, mCurrentPhotoPath: String) : File {
            val cursor = activity.contentResolver.query(Uri.parse(mCurrentPhotoPath),
                    Array(1) {android.provider.MediaStore.Images.ImageColumns.DATA},
                    null, null, null)
            cursor.moveToFirst()
            val photoPath = cursor.getString(0)
            cursor.close()
            return File(photoPath)
        }

        public fun processAlbumPhoto(activity: Activity, data : Intent?) : String {
            val bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, data?.data)
            return saveImage(bitmap, activity)
        }

        private fun saveImage(myBitmap : Bitmap, activity: Activity) : String {
            val bytes = ByteArrayOutputStream()
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

            try {
                val f = File(activity.filesDir, Calendar.getInstance().timeInMillis.toString() + ".jpg")
                f.createNewFile()
                val fo = FileOutputStream(f)
                fo.write(bytes.toByteArray())
                MediaScannerConnection.scanFile(activity,
                        arrayOf(f.path),
                        arrayOf("image/jpeg"), null)
                fo.close()
                Log.d("TAG", "File Saved::--->" + f.absolutePath)
                Log.i("test image", f.path + " / " + f.absolutePath)

                return f.absolutePath
            } catch (e1 : IOException) {
                e1.printStackTrace()
            }
            return ""
        }

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
                e.printStackTrace()
                return null
            }

        }

        fun rotateImage(url: String): Bitmap? {
            val file = File(url)
            var exifInterface: ExifInterface? = null
            try {
                exifInterface =  ExifInterface(file.path)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val orientation = exifInterface!!.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            if ( (orientation == ExifInterface.ORIENTATION_NORMAL) || (orientation == 0) ) {
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, ""+ExifInterface.ORIENTATION_ROTATE_90)
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, ""+ExifInterface.ORIENTATION_ROTATE_180)
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, ""+ExifInterface.ORIENTATION_ROTATE_270)
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, ""+ExifInterface.ORIENTATION_NORMAL)
            }
            try {
                exifInterface.saveAttributes()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return ImageUtils.getBitmap(url)
        }

        fun getBitmap(path: String): Bitmap? {
            Log.e("inside of", "getBitmap = " + path)
            try {
                var b: Bitmap? = null
                val o = BitmapFactory.Options()
                o.inJustDecodeBounds = true

                val matrix = Matrix()
                val exifReader = ExifInterface(path)
                val orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
                var rotate = 0
                if (orientation == ExifInterface.ORIENTATION_NORMAL) {
                    // Do nothing. The original image is fine.
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    rotate = 90
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    rotate = 180
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    rotate = 270
                }
                matrix.postRotate(rotate.toFloat())
                try {
                    b = loadBitmap(path, rotate)
                } catch (e: OutOfMemoryError) {
                    e.printStackTrace()
                }

                System.gc()
                return b
            } catch (e: Exception) {
                Log.e("my tag", e.message, e)
                e.printStackTrace()
                return null
            }

        }

        fun loadBitmap(path: String, orientation: Int): Bitmap? {
            var bitmap: Bitmap? = null
            try {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(path, options)
                bitmap = BitmapFactory.decodeFile(path)
                if (orientation > 0) {
                    val matrix = Matrix()
                    matrix.postRotate(orientation.toFloat())
                    bitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return bitmap
        }
    }
}