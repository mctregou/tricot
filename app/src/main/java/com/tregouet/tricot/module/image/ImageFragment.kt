package com.tregouet.tricot.module.image

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.tricot.R
import com.tregouet.tricot.model.Image
import com.tregouet.tricot.utils.Constants
import com.tregouet.tricot.utils.ImageUtils
import com.tregouet.tricot.utils.RealmManager
import com.tregouet.tricot.utils.Utils
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
        if (image?.customDrawable != null) {
            imagePhotoView.setImageDrawable(ContextCompat.getDrawable(context!!, image?.customDrawable!!))
            delete.visibility = View.INVISIBLE;
            turn_image.visibility = View.INVISIBLE;
        } else {
            imagePhotoView.setImageBitmap(ImageUtils.getBitmap(image?.url!!))
        }
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