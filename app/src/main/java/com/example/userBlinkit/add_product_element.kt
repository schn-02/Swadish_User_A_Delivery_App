package com.example.userBlinkit

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object add_product_element
{
    var allProductCategory = arrayOf(
        "All",
        "Vegetables & Fruits",
        "Dairy and Breakfast" ,
            "Munchies",
        "Cold Drink & Juices",
        "Instant & Frozen Foods",
        "Tea Coffee & Health Drinks",
        "Bakery & Biscuits",
        "Sweet Tooth",
        "Atta Rice & Dal",
        "Dry Fruits Masala & Oil",
        "Sauces & Spreads",
        "Chicken Meat & Fish",
        "Pan Corner",
        "Organic & Premimum",
        "Baby Care",
        "Pharma & Wellness",
        "Cleaning Essential",
        "Home & Office",
        "Personal Care",
        "Pet Care"
    )
    var allUnitsProduct = arrayOf(
        "Kg",
        "gm",
        "ml","Ltr","Packets"
        ,"Pieces"
    )

    var allProductTypes = arrayOf(
        "Milk ,Curd & Paneer",
        "Vegetables",
        "Chis & Crips"
        ,"Fruits",
        "Salt & Sugar"
        ,"Noodles",
        "Cold Drink & Juices",
        "Cooking Oil",
        "Biscuits",
        "Eggs",
        "Chocklates",
        "Bread & Butter",
        "Namkeen",
        "Atta & Rice",
        "Ice Cream",
        "Cake",
        "Ghee",
        "Water",
        "Cookies",
        "Maida & Sooji"
    )

    fun showToastWithDelay(context: Context, message: String, delayMillis: Long)
    {
        Handler(Looper.getMainLooper()).postDelayed({
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }, delayMillis)
    }
    }

//showProductAdd.setOnClickListener {
//    val productRandomIdString = products.productRandomId
//
//    if (productRandomIdString.contains(",")) {
//        val individualIds = productRandomIdString.split(",")
//        randomIDList.addAll(individualIds)
//    } else {
//        randomIDList.add(productRandomIdString)
//    }
//
//    TotalClicked++
//    Toast.makeText(context, "$TotalClicked", Toast.LENGTH_SHORT).show()
//
//    var currentCount = products.itemCount
//    num.text = currentCount.toString()
//    itemCart?.visibility = View.VISIBLE
//    itemcart2?.visibility = View.VISIBLE
//    showProductAdd.visibility = View.GONE
//    AddCart.visibility = View.VISIBLE
//
//    addnum.setOnClickListener {
//        currentCount++
//        products.itemCount = currentCount
//        num.text = currentCount.toString()
//        TotalClicked++
//        Toast.makeText(context, "$TotalClicked", Toast.LENGTH_SHORT).show()
//    }
//
//    sub.setOnClickListener {
//
//        if (currentCount > 0) {
//            currentCount--
//            products.itemCount = currentCount
//            num.text = currentCount.toString()
//            TotalClicked--
//            Toast.makeText(context, "$TotalClicked", Toast.LENGTH_SHORT).show()
//        }
//        if (currentCount == 0) {
//            randomIDList.remove(productRandomIdString)
//            showProductAdd.visibility = View.VISIBLE
//            AddCart.visibility = View.GONE
//            products.itemCount = 1
//            num.text = products.itemCount.toString()
//            Toast.makeText(context, "$TotalClicked", Toast.LENGTH_SHORT).show()
//
//            if (differ.currentList.all { it.itemCount == 1 } && currentCount == 1) {
//                itemCart?.visibility = View.GONE
//                itemcart2?.visibility = View.GONE
//                Toast.makeText(context, "$TotalClicked", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    itemCart?.setOnClickListener {
//        Toast.makeText(context, "$randomIDList", Toast.LENGTH_LONG).show()
//        CheckOut(randomIDList)
//    }
//}
