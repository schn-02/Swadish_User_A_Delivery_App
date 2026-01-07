package com.example.userBlinkit.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.blinklit.databinding.SampleCheckoutLayoutBinding
import com.example.userBlinkit.Models.Product


class CheckoutAdapter :RecyclerView.Adapter<CheckoutAdapter.viewHolder>() {







    val diffUtill = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {

            return oldItem.productRandomId == newItem.productRandomId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem && oldItem.itemCount == newItem.itemCount
        }

    }

    val differr = AsyncListDiffer(this, diffUtill)

    class  viewHolder(val binding22:SampleCheckoutLayoutBinding):RecyclerView.ViewHolder(binding22.root)
    {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {

        return  viewHolder(SampleCheckoutLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun getItemCount(): Int
    {
        return differr.currentList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val products = differr.currentList[position]
        holder.binding22.apply {
            val imageList2 = ArrayList<SlideModel>()
            products.productImageURI?.forEach { imaguri ->
                val slideModel = SlideModel(imaguri, ScaleTypes.FIT)
                imageList2.add(slideModel)
            }

            if (imageList2.isNotEmpty()) {
                CheckOutImage.setImageList(imageList2)
            } else {
                Toast.makeText(
                    holder.itemView.context,
                    "No images available",
                    Toast.LENGTH_SHORT
                ).show()
            }

            checkoutUnit.text = products.ProductUnit
            checkoutRuppees.text = products.productPrice
            checkoutProductTitle.text = products.productTitle
            checkoutItemCount.text = products.itemCount.toString()


        }


    }

}