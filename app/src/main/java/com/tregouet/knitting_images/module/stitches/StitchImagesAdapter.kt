package com.tregouet.knitting_images.module.stitches

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.model.Image
import com.tregouet.knitting_images.module.image.ImageActivity
import com.tregouet.knitting_images.utils.Constants
import com.tregouet.knitting_images.utils.ImageUtils
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
                stitch_image.setImageBitmap(ImageUtils.getBitmap(stitchImage.url!!))
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
            val values = ContentValues(1)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            activity.fileUri = activity.contentResolver
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values)
            activity.stitchId = stitchId
            ImageUtils.openCameraOrFolders(activity, activity.fileUri!!)

        }
    }
}