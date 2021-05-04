package com.tregouet.tricot.module.image

import android.os.Bundle
import com.tregouet.tricot.R
import com.tregouet.tricot.module.base.BaseActivity
import com.tregouet.tricot.utils.Constants
import com.tregouet.tricot.utils.RealmManager
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : BaseActivity() {

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
    }

    override fun onResume() {
        super.onResume()

        getImages()
    }

    private fun getImages () {
        RealmManager().open()
        val images = ArrayList(RealmManager().createImageDao().loadAllForElement(intent.getIntExtra(Constants.IMAGE_TYPE, 0), intent.getIntExtra(Constants.ELEMENT_ID, 0)).toList())
        RealmManager().close()

        val pagerAdapter = ImagePageAdapter(supportFragmentManager, images)
        imagesViewPager.adapter = pagerAdapter
        imagesViewPager.currentItem = intent.getIntExtra(Constants.POSITION, 0)
    }
}