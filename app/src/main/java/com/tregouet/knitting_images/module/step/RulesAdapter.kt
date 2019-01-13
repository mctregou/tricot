package com.tregouet.knitting_images.module.step

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.model.Rule
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

            itemView.setOnClickListener {
                if (itemView.description.visibility == View.VISIBLE){
                    itemView.description.visibility = View.GONE
                } else {
                    itemView.description.visibility = View.VISIBLE
                }
            }
        }
    }
}