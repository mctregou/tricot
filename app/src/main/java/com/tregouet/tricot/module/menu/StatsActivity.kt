package com.tregouet.tricot.module.menu

import android.os.Bundle
import com.tregouet.tricot.R
import com.tregouet.tricot.module.base.BaseActivity

import com.tregouet.tricot.utils.RealmManager
import kotlinx.android.synthetic.main.activity_stats.*

class StatsActivity : BaseActivity() {

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        setRanksNumber()
    }

    /**
     * Calculate and set total ranks number
     */
    private fun setRanksNumber() {
        val steps = RealmManager().createStepDao().loadAll()
        var ranksNumber = 0
        for (step in steps){
            System.out.println("step.currentRank=" + step.currentRank)
            ranksNumber += step.currentRank-1
        }
        if (ranksNumber == 0){
            ranks_number.text = getString(R.string.ranks_number_0)
        } else if (ranksNumber < 10){
            ranks_number.text = getString(R.string.ranks_number_10, ranksNumber)
        } else {
            ranks_number.text = getString(R.string.ranks_number_message, ranksNumber)
        }

    }
}
