package com.tregouet.knitting_images.module.menu

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.tregouet.knitting_images.R

/**
 * Created by mariececile.tregouet on 03/02/2018.
 */
/**
 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
 * sequence.
 */
class TutoAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    val texts = listOf(R.string.tuto1, R.string.tuto2, R.string.tuto3, R.string.tuto4)
    val images = listOf(R.drawable.tuto1, R.drawable.tuto2, R.drawable.tuto3, R.drawable.tuto4)

    override fun getItem(position: Int): Fragment {
        return TutosFragment(texts[position], images[position])
    }

    override fun getCount(): Int {
        return texts.size
    }
}
