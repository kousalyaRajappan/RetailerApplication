package com.za.toptitup.loginlibrary.retailerFragments.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.za.toptitup.loginlibrary.R
import com.za.toptitup.loginlibrary.databinding.ItemDashboardModuleBinding
import com.za.toptitup.loginlibrary.retailerFragments.models.DashboardModule

class DashboardAdapter(
    private val list: List<DashboardModule>
) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemDashboardModuleBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemDashboardModuleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = list[position]
        holder.binding.txtTitle.text = item.title
        holder.binding.imgIcon.setImageResource(item.icon)
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("productId", item.productId)
                putString("productName", item.title)
            }
            it.findNavController().navigate(R.id.productDetailFragment, bundle)
        }
    }
}
