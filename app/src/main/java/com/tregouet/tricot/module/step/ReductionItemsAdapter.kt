package com.tregouet.tricot.module.step

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.tricot.R
import com.tregouet.tricot.model.ReductionItem
import kotlinx.android.synthetic.main.item_reduction_item.view.*

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
class ReductionItemsAdapter(private var reductionItems: ArrayList<ReductionItem>) : RecyclerView.Adapter<ReductionItemsAdapter.ViewHolder>() {

    /**
     * onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.item_reduction_item, parent,false))

    /**
     * onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(reductionItems[position])

    /**
     * getItemCount
     */
    override fun getItemCount() = reductionItems.size

    /**
     *
     */
    fun setReductionItems(reductionItems: ArrayList<ReductionItem>){
        this.reductionItems = reductionItems
        notifyDataSetChanged()
    }

    /**
     * ViewHolder
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Bind the rule with the ViewHolder
         */
        fun bind(reductionItem: ReductionItem) = with(itemView) {
            itemView.reduction_item_description.text = String.format("• %d fois %d mailles du côté %s", reductionItem.times, reductionItem.numberOfMesh, reductionItem.side)
        }
    }
}