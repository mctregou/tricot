package com.tregouet.tricot.module.stitches

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.tregouet.tricot.R
import com.tregouet.tricot.model.Image
import com.tregouet.tricot.module.image.ImageActivity
import com.tregouet.tricot.utils.Constants
import com.tregouet.tricot.utils.ImageUtils
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.item_stitch_image.view.*


/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
class StitchImagesAdapter(private val activity: Activity, private val stitchId : Int, private val stitchImages: ArrayList<Image>) : RecyclerView.Adapter<StitchImagesAdapter.ViewHolder>() {

    /**
     * onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.item_stitch_image, parent,false), activity, stitchId)

    /**
     * onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(stitchImages[position], position)

    /**
     * getItemCount
     */
    override fun getItemCount() = stitchImages.size

    /**
     * ViewHolder
     */
    class ViewHolder(itemView: View, private val activity: Activity, private val stitchId: Int) : RecyclerView.ViewHolder(itemView) {

        /**
         * Bind the rule with the ViewHolder
         */
        fun bind(stitchImage: Image, position: Int) = with(itemView) {
            if (stitchImage.id != -1) {
                Log.i("Realm", stitchImage.id.toString() + "/" + stitchImage.customDrawable + "/" + stitchImage.url);
                if (stitchImage.customDrawable != null) {
                    stitch_image.setImageDrawable(ContextCompat.getDrawable(context!!, stitchImage.customDrawable!!))
                } else if (stitchImage.url != null){
                    stitch_image.setImageBitmap(ImageUtils.getBitmap(stitchImage.url!!))
                }
                stitch_image.setOnClickListener { openZoomCarousel(position) }
            } else {
                stitch_image.setImageResource(R.drawable.ic_add)
                stitch_image.setOnClickListener {
                    if (activity is StitchesActivity) {
                        addPicture(activity)
                    }
                }
            }
        }

        private fun openZoomCarousel(position: Int) {
            val intent = Intent(Intent(activity, ImageActivity::class.java))
            intent.putExtra(Constants.IMAGE_TYPE, Constants.STITCH_IMAGE)
            intent.putExtra(Constants.ELEMENT_ID, stitchId)
            intent.putExtra(Constants.POSITION, position)
            activity.startActivity(intent)
        }

        private fun addPicture(activity : StitchesActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                requestAppPermissions(arrayOf<String>(
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        R.string.app_name, 1)
            } else {
                openCameraActivity(activity)
            }
        }

        private fun openCameraActivity(activity : StitchesActivity) {
            val values = ContentValues(1)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            activity.fileUri = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            activity.stitchId = stitchId
            ImageUtils.openCameraOrFolders(activity, activity.fileUri!!)
        }

        private fun requestAppPermissions(requestedPermissions: Array<String>,
                                          stringId: Int, requestCode: Int) {
            var permissionCheck = PackageManager.PERMISSION_GRANTED
            var shouldShowRequestPermissionRationale = false
            for (permission in requestedPermissions) {
                permissionCheck += ContextCompat.checkSelfPermission(activity, permission)
                shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale || ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
            }
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale) {
                    Toast.makeText(activity, "shouldShowRequestPermissionRationale", Toast.LENGTH_LONG).show()
                } else {
                    ActivityCompat.requestPermissions(activity, requestedPermissions, requestCode)
                }
            } else {
                onPermissionsGranted(requestCode)
            }
        }

        fun onPermissionsGranted(requestCode: Int) {
            if (activity is StitchesActivity) {
                openCameraActivity(activity)
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>?, grantResults: IntArray) {
            activity.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
            var permissionCheck = PackageManager.PERMISSION_GRANTED
            for (permission in grantResults) {
                permissionCheck = permissionCheck + permission
            }
            if (grantResults.size > 0 && permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionsGranted(requestCode)
            } else {
                Toast.makeText(activity, "not granted", Toast.LENGTH_LONG).show()
            }
        }
    }
}