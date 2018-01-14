package com.tregouet.tricot.module.step

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.tricot.R
import com.tregouet.tricot.model.Rule
import kotlinx.android.synthetic.main.item_current_rule.view.*

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
class CurrentRulesAdapter(private val rules: ArrayList<Rule>) : RecyclerView.Adapter<CurrentRulesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.item_current_rule, parent,false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(rules[position])

    override fun getItemCount() = rules.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(rule: Rule) = with(itemView) {
            Log.i("CurrentRulesAdapter", "bind")
            itemView.title.text = "• " + rule.stitch
        }
    }
}