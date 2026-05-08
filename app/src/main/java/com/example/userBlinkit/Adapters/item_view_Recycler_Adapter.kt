package com.example.userBlinkit.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
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

class item_view_Recycler_Adapter(
    private val context: Context,
    private val CheckOut: KFunction3<ArrayList<String>, MutableMap<String, Int>, Long, Unit>
) : RecyclerView.Adapter<item_view_Recycler_Adapter.viewHolder>(), Filterable {

    var original = ArrayList<Product>()
    private var filter: FilterMain? = null

    private val randomIDList = ArrayList<String>()
    private val itemCounts = mutableMapOf<String, Int>()

    private var totalPrice = 0
    private var totalClicked = 0

    private val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productRandomId == newItem.productRandomId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    class viewHolder(val binding: SampleLayoutItemViewProductBinding) :
        RecyclerView.ViewHolder(binding.root)

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
        val product = differ.currentList[position]

        val mainCartLayout: LinearLayout? = if (context is MainActivity) {
            context.findViewById(R.id.itemCartMain)
        } else {
            null
        }

        val clickedCartLayout: LinearLayout? = if (context is ClickedCategory) {
            context.findViewById(R.id.footerLayout)
        } else {
            null
        }

        val mainCartCount: TextView? = if (context is MainActivity) {
            context.findViewById(R.id.showCount)
        } else {
            null
        }

        val clickedCartCount: TextView? = if (context is ClickedCategory) {
            context.findViewById(R.id.showItemCount)
        } else {
            null
        }

        holder.binding.apply {

            val imageList = ArrayList<SlideModel>()

            product.productImageURI?.forEach { imageUri ->
                imageList.add(SlideModel(imageUri, ScaleTypes.FIT))
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

            showProductName.text = product.productTitle
            showProductUnit.text = product.ProductQuantity + product.ProductUnit
            showProductPrice.text = "₹${product.productPrice}"

            val currentCount = itemCounts[product.productRandomId] ?: 0

            if (currentCount > 0) {
                showProductAdd.visibility = View.GONE
                AddCart.visibility = View.VISIBLE
                num.text = currentCount.toString()
            } else {
                showProductAdd.visibility = View.VISIBLE
                AddCart.visibility = View.GONE
                num.text = "1"
            }

            showProductAdd.setOnClickListener {
                val price = product.productPrice?.toDoubleOrNull()?.toInt() ?: 0

                if (!randomIDList.contains(product.productRandomId)) {
                    randomIDList.add(product.productRandomId)
                }

                itemCounts[product.productRandomId] = 1
                totalPrice += price
                totalClicked++

                num.text = "1"

                showProductAdd.visibility = View.GONE
                AddCart.visibility = View.VISIBLE

                updateCartUi(
                    mainCartLayout,
                    clickedCartLayout,
                    mainCartCount,
                    clickedCartCount
                )
            }

            addnum.setOnClickListener {
                val price = product.productPrice?.toDoubleOrNull()?.toInt() ?: 0

                val oldCount = itemCounts[product.productRandomId] ?: 0
                val newCount = oldCount + 1

                itemCounts[product.productRandomId] = newCount
                totalPrice += price
                totalClicked++

                num.text = newCount.toString()

                updateCartUi(
                    mainCartLayout,
                    clickedCartLayout,
                    mainCartCount,
                    clickedCartCount
                )
            }

            sub.setOnClickListener {
                val price = product.productPrice?.toDoubleOrNull()?.toInt() ?: 0

                val oldCount = itemCounts[product.productRandomId] ?: 0

                if (oldCount <= 0) {
                    return@setOnClickListener
                }

                val newCount = oldCount - 1

                totalPrice -= price
                if (totalPrice < 0) totalPrice = 0

                totalClicked--
                if (totalClicked < 0) totalClicked = 0

                if (newCount == 0) {
                    itemCounts.remove(product.productRandomId)
                    randomIDList.remove(product.productRandomId)

                    showProductAdd.visibility = View.VISIBLE
                    AddCart.visibility = View.GONE
                    num.text = "1"
                } else {
                    itemCounts[product.productRandomId] = newCount
                    num.text = newCount.toString()
                }

                updateCartUi(
                    mainCartLayout,
                    clickedCartLayout,
                    mainCartCount,
                    clickedCartCount
                )
            }

            mainCartLayout?.setOnClickListener {
                CheckOut(randomIDList, itemCounts, totalPrice.toLong())
            }

            clickedCartLayout?.setOnClickListener {
                CheckOut(randomIDList, itemCounts, totalPrice.toLong())
            }
        }
    }

    private fun updateCartUi(
        mainCartLayout: LinearLayout?,
        clickedCartLayout: LinearLayout?,
        mainCartCount: TextView?,
        clickedCartCount: TextView?
    ) {
        if (totalClicked > 0) {
            mainCartLayout?.visibility = View.VISIBLE
            clickedCartLayout?.visibility = View.VISIBLE

            mainCartCount?.visibility = View.VISIBLE
            clickedCartCount?.visibility = View.VISIBLE

            mainCartCount?.text = totalClicked.toString()
            clickedCartCount?.text = totalClicked.toString()
        } else {
            mainCartLayout?.visibility = View.GONE
            clickedCartLayout?.visibility = View.GONE

            mainCartCount?.text = "0"
            clickedCartCount?.text = "0"
        }
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterMain(this, original)
        }
        return filter!!
    }
}