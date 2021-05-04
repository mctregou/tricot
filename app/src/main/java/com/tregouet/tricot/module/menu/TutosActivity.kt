package com.tregouet.tricot.module.menu

import android.os.Bundle
import com.tregouet.tricot.R
import com.tregouet.tricot.module.base.BaseActivity
import android.support.v4.view.ViewPager
import kotlinx.android.synthetic.main.activity_tutos.*


class TutosActivity : BaseActivity() {

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutos)

        setPager()
    }

    /**
     * Set application version
     */
    private fun setPager() {
        viewPager.adapter = TutoAdapter(supportFragmentManager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {/*empty*/
            }

            override fun onPageSelected(position: Int) {
                pageIndicatorView.selection = position
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }
}
