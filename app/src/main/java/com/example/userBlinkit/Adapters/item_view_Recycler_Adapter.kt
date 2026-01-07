package com.example.userBlinkit.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.adminblinkit.Filter.FilterMain
import com.example.blinklit.R
import com.example.blinklit.databinding.SampleLayoutItemViewProductBinding
import com.example.userBlinkit.ClickedCategory
import com.example.userBlinkit.MainActivity
import com.example.userBlinkit.Models.Product


import kotlin.reflect.KFunction3

class item_view_Recycler_Adapter(private val context: Context, val CheckOut: KFunction3<ArrayList<String>, MutableMap<String, Int>, Long, Unit>):RecyclerView.Adapter<item_view_Recycler_Adapter.viewHolder>(),Filterable {
    var original = ArrayList<Product>()
     var randomIDList = ArrayList<String>()
    private val itemCounts = mutableMapOf<String, Int>()
     var totalPrice =0
    var TotalClicked = 0


    val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {

            return oldItem.productRandomId == newItem.productRandomId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)


    class viewHolder(val binding: SampleLayoutItemViewProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(
            SampleLayoutItemViewProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val products = differ.currentList[position]
        val itemCart: TextView? = if (context is MainActivity) {
            (context).findViewById(R.id.Next1)
        } else null

        val itemcart2: TextView? = if (context is ClickedCategory){
            (context ).findViewById(R.id.itemCartMain1)
        } else null

      val countclicked: TextView? = if (context is ClickedCategory){
            (context ).findViewById(R.id.showItemCount)
        } else null

            val countclicked2: TextView? = if (context is MainActivity){
            (context ).findViewById(R.id.showCount)
        } else null




        holder.binding.apply {
            val imageList = ArrayList<SlideModel>()
            products.productImageURI?.forEach { imaguri ->
                val slideModel = SlideModel(imaguri, ScaleTypes.FIT)
                imageList.add(slideModel)
            }

            if (imageList.isNotEmpty()) {
                imageslider.setImageList(imageList)
            } else {
                Toast.makeText(
                    holder.itemView.context,
                    "No images available",
                    Toast.LENGTH_SHORT
                ).show()
            }


            showProductAdd.setOnClickListener {


                  TotalClicked++
                val priceDouble = products.productPrice?.toDoubleOrNull()
                if (priceDouble != null) {
                    totalPrice += priceDouble.toInt()
                }

                if (!randomIDList.contains(products.productRandomId)) {
                    randomIDList.add(products.productRandomId)  // Add product to ID list
                }

                itemCounts[products.productRandomId] = 1  // Initialize count to 1
                num.text = "1"

                countclicked?.text = TotalClicked.toString()
                countclicked2?.text = TotalClicked.toString()

                itemCart?.visibility = View.VISIBLE
                itemcart2?.visibility = View.VISIBLE
                countclicked?.visibility = View.VISIBLE
                countclicked2?.visibility = View.VISIBLE
                showProductAdd.visibility = View.GONE
                AddCart.visibility = View.VISIBLE

                // Add Button Logic
                addnum.setOnClickListener {

                    val priceDouble = products.productPrice?.toDoubleOrNull()
                    if (priceDouble != null) {
                        totalPrice += priceDouble.toInt()
                    }


                    val updatedCount = itemCounts[products.productRandomId]?.plus(1) ?: 1
                    itemCounts[products.productRandomId] = updatedCount

                    num.text = updatedCount.toString()
                    TotalClicked++


                    countclicked?.text = TotalClicked.toString()
                    countclicked2?.text = TotalClicked.toString()

                }

                // Subtract Button Logic
                sub.setOnClickListener {
                    TotalClicked--
                    val priceDouble = products.productPrice?.toDoubleOrNull()
                    if (priceDouble != null && totalPrice >= priceDouble.toInt()) {
                        totalPrice -= priceDouble.toInt()
                    }

                    val updatedCount = (itemCounts[products.productRandomId]?.minus(1))?.coerceAtLeast(0) ?: 0
                    itemCounts[products.productRandomId] = updatedCount
                    num.text = updatedCount.toString()

                    countclicked?.text = TotalClicked.toString()
                    countclicked2?.text = TotalClicked.toString()


                    // If count reaches zero for this product
                    if (updatedCount == 0) {
                        randomIDList.remove(products.productRandomId)  // Remove from ID list
                        itemCounts.remove(products.productRandomId)    // Remove from counts
                        showProductAdd.visibility = View.VISIBLE
                        AddCart.visibility = View.GONE
                    }

                    // Check if all counts are zero
                    val allCountsZero = itemCounts.values.all { it == 0 }
                    if (allCountsZero) {
                        TotalClicked =0
                        itemCart?.visibility = View.GONE
                        itemcart2?.visibility = View.GONE
                        countclicked?.visibility = View.GONE
                        countclicked2?.visibility = View.GONE

                    }
                }

                // Cart Button Logic
                itemCart?.setOnClickListener {
                    CheckOut(randomIDList, itemCounts, totalPrice.toLong())
                }

                itemcart2?.setOnClickListener {
                    CheckOut(randomIDList, itemCounts, totalPrice.toLong())
                }
                countclicked2?.setOnClickListener {
                    CheckOut(randomIDList, itemCounts, totalPrice.toLong())
                }
                countclicked?.setOnClickListener {
                    CheckOut(randomIDList, itemCounts, totalPrice.toLong())
                }
            }

            showProductName.text = products.productTitle
            showProductUnit.text = products.ProductQuantity + products.ProductUnit
            showProductPrice.text = "₹${products.productPrice}"
        }
        TotalClicked =0
    }





    private val filter: FilterMain? = null

    override fun getFilter(): Filter {

        if (filter == null) {
            return FilterMain(this, original)
        } else {
            return filter
        }

    }




}
