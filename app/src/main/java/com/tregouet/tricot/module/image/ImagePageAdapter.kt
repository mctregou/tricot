package com.tregouet.tricot.module.image

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.tregouet.tricot.model.Image

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
class ImagePageAdapter(fragmentManager: FragmentManager, private val images: ArrayList<Image>) :
        FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return ImageFragment.newInstance(images[position])
    }

    override fun getCount(): Int {
        return images.size
    }
}