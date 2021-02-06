package com.tregouet.knitting_images.module.image

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.model.Image
import com.tregouet.knitting_images.utils.Constants
import com.tregouet.knitting_images.utils.ImageUtils
import com.tregouet.knitting_images.utils.RealmManager
import com.tregouet.knitting_images.utils.Utils
import kotlinx.android.synthetic.main.fragment_image.*

class ImageFragment : Fragment() {

    var image: Image? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         return inflater.inflate(R.layout.fragment_image, container, false)
    }

    companion object{
        fun newInstance(image: Image):ImageFragment{
            val args = Bundle()
            args.putSerializable(Constants.IMAGE, image)
            val fragment = ImageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        image = arguments?.get(Constants.IMAGE) as Image
        imagePhotoView.setImageBitmap(ImageUtils.getBitmap(image?.url!!))
        imagePhotoView.setZoomable(true)

        delete.setOnClickListener {
            val confirmationDialog = Utils().showDialog(context!!, R.string.warning, R.string.image_delete_confirmation, View.OnClickListener {
                RealmManager().open()
                RealmManager().createImageDao().removeById(image?.id!!)
                RealmManager().close()
                activity?.finish()
            })
            confirmationDialog.show()
        }

        turn_image.setOnClickListener {
            imagePhotoView.setImageBitmap(ImageUtils.rotateImage(image?.url!!))
        }

    }

}