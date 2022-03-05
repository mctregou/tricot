package com.tregouet.tricot.module.menu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.tregouet.tricot.R

/**
 * Created by mariececile.tregouet on 03/02/2018.
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
