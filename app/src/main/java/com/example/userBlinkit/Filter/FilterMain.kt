package com.example.adminblinkit.Filter

import android.widget.Filter

import com.example.userBlinkit.Adapters.item_view_Recycler_Adapter
import com.example.userBlinkit.Models.Product
import java.util.Locale

class FilterMain(private val adapter:item_view_Recycler_Adapter ,
                 private  val filterProducts :ArrayList<Product>):Filter()
{

    override fun performFiltering(searchingText: CharSequence?): FilterResults {
        val filterResults = FilterResults()
        if (!searchingText.isNullOrEmpty()) {
            val query = searchingText.toString().trim().uppercase(Locale.getDefault()).split(" ")
            val filterProductList = ArrayList<Product>()
            for (products in filterProducts) {
                if (query.any { search ->
                        products.productTitle?.uppercase(Locale.getDefault())?.contains(search) == true ||
                                products.productCategory?.uppercase(Locale.getDefault())?.contains(search) == true ||
                                products.productType?.uppercase(Locale.getDefault())?.contains(search) == true||
                                products.productPrice?.uppercase(Locale.getDefault())?.contains(search) == true||
                                products.ProductUnit?.uppercase(Locale.getDefault())?.contains(search) == true

                    }) {
                    filterProductList.add(products)
                }
            }
            filterResults.apply {
                count = filterProductList.size
                values = filterProductList
            }
        } else {
            filterResults.apply {
                count = filterProducts.size
                values = filterProducts
            }
        }
        return filterResults
    }

    override fun publishResults(p0: CharSequence?, results: FilterResults?) {
        adapter.differ.submitList(results?.values as? ArrayList<Product>)
    }


}