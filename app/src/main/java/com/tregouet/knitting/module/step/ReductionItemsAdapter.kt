package com.tregouet.knitting.module.step

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.knitting.R
import com.tregouet.knitting.model.ReductionItem
import kotlinx.android.synthetic.main.item_reduction_item.view.*

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
class ReductionItemsAdapter(private val rulesActivity: StepActivity, private var reductionItems: ArrayList<ReductionItem>) : RecyclerView.Adapter<ReductionItemsAdapter.ViewHolder>() {

    /**
     * onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.item_reduction_item, parent,false), this)

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
    class ViewHolder(itemView: View, private val reductionItemsAdapter: ReductionItemsAdapter) : RecyclerView.ViewHolder(itemView) {

        /**
         * Bind the rule with the ViewHolder
         */
        fun bind(reductionItem: ReductionItem) = with(itemView) {
            itemView.position.text = reductionItem.order.toString()
            itemView.times.text = reductionItem.times.toString()
            itemView.nb_mesh.text = reductionItem.numberOfMesh.toString()
            itemView.side.text = reductionItem.side

            itemView.remove.setOnClickListener {
                reductionItemsAdapter.reductionItems.remove(reductionItem)
                reductionItemsAdapter.notifyDataSetChanged()
            }
        }
    }
}