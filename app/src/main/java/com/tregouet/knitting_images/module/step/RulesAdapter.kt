package com.tregouet.knitting_images.module.step

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.model.Image
import com.tregouet.knitting_images.model.Rule
import com.tregouet.knitting_images.module.stitches.StitchImagesAdapter
import com.tregouet.knitting_images.utils.Constants
import com.tregouet.knitting_images.utils.RealmManager
import kotlinx.android.synthetic.main.item_rule.view.*

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
class RulesAdapter(private val rulesActivity: StepActivity, private val projects: ArrayList<Rule>) : RecyclerView.Adapter<RulesAdapter.ViewHolder>() {

    /**
     * onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.item_rule, parent,false), rulesActivity)

    /**
     * onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(projects[position])

    /**
     * getItemCount
     */
    override fun getItemCount() = projects.size

    /**
     * ViewHolder
     */
    class ViewHolder(itemView: View, private val rulesActivity: StepActivity) : RecyclerView.ViewHolder(itemView) {

        /**
         * Bind the rule with the ViewHolder
         */
        fun bind(rule: Rule) = with(itemView) {
            itemView.title.text = rule.stitch
            itemView.frequency.text = rulesActivity.getString(R.string.frequence_ranks, rule.frequency)
            itemView.description.text = rule.description

            getImages(rule)

            itemView.setOnClickListener {
                if (itemView.hidden_options.visibility == View.VISIBLE){
                    itemView.hidden_options.visibility = View.GONE
                } else {
                    itemView.hidden_options.visibility = View.VISIBLE
                }
            }
        }

        fun getImages(rule : Rule) {
            RealmManager().open()
            val images = ArrayList(RealmManager().createImageDao().loadAllForElement(Constants().STITCH_IMAGE, rule.id!!).toList())
            RealmManager().close()

            itemView.stitch_images.layoutManager = LinearLayoutManager(rulesActivity, LinearLayoutManager.HORIZONTAL, false)
            val adapter = StitchImagesAdapter(rulesActivity, rule.id!!, images)
            itemView.stitch_images.adapter = adapter
        }
    }
}