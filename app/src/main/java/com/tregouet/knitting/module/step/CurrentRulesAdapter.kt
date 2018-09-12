package com.tregouet.knitting.module.step

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.knitting.R
import com.tregouet.knitting.model.Rule
import kotlinx.android.synthetic.main.item_current_rule.view.*

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
class CurrentRulesAdapter(private val rules: ArrayList<Rule>) : RecyclerView.Adapter<CurrentRulesAdapter.ViewHolder>() {

    /**
     * onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.item_current_rule, parent,false))

    /**
     * onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(rules[position])

    /**
     * getItemCount
     */
    override fun getItemCount() = rules.size

    /**
     * ViewHolder
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Bind rule to the ViewHolder
         */
        fun bind(rule: Rule) = with(itemView) {
            itemView.title.text = context.getString(R.string.rule_list, rule.stitch)
        }
    }
}