package com.tregouet.knitting_images.module.image

import android.os.Bundle
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.module.base.BaseActivity
import com.tregouet.knitting_images.utils.Constants
import com.tregouet.knitting_images.utils.RealmManager
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
        val images = ArrayList(RealmManager().createImageDao().loadAllForElement(intent.getIntExtra(Constants().IMAGE_TYPE, 0), intent.getIntExtra(Constants().PROJECT_ID, 0)).toList())
        RealmManager().close()

        val pagerAdapter = ImagePageAdapter(supportFragmentManager, images)
        imagesViewPager.adapter = pagerAdapter
    }
}