package com.example.userBlinkit

import androidx.lifecycle.ViewModel
import com.example.userBlinkit.Models.Product
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserAuthModel : ViewModel() {

    fun FetchAllProducts(text: String): Flow<List<Product>> = callbackFlow {
        // Firebase database reference
        val db = FirebaseDatabase.getInstance().getReference("Admin").child("ProductsDetails")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = mutableListOf<Product>()

                // Loop through all products
                for (productSnapshot in snapshot.children) {
                    // Get product object
                    val prod = productSnapshot.getValue(Product::class.java)

                    // Check if the category matches or if we want all products
                    if (text == "All" || prod?.productCategory == text) {
                        // Fetch images for the product
                        val imageList = mutableListOf<String>()
                        val imageSnapshot = productSnapshot.child("Admin_Product_Images")

                        for (image in imageSnapshot.children) {
                            image.getValue(String::class.java)?.let { imageURL ->
                                if (text == "All" || prod?.productCategory == text) {
                                    imageList.add(imageURL)
                                }
                            }
                        }

                        prod?.productImageURI = imageList
                        prod?.let { products.add(it) }  // Add product to list if valid
                    }
                }

                // Send the fetched list to the flow
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the cancellation here
                close(error.toException()) // Close flow with the error
            }
        }

        // Add the event listener to Firebase
        db.addValueEventListener(eventListener)

        // Cleanup the listener when the flow is closed
        awaitClose { db.removeEventListener(eventListener) }
    }

    fun FetchAllCheckoutProducts(
        randomIds: ArrayList<String>,
        item: MutableMap<String, Int>,
        Price: Double
    ): Flow<Triple<List<Product>, Map<String, Int> , Double>> = callbackFlow {
        val db1 = FirebaseDatabase.getInstance().getReference("Admin").child("ProductsDetails")
        val products = mutableListOf<Product>()

        val eventListener1 = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                products.clear()

                for (productSnapshot in snapshot.children) {
                    val prod = productSnapshot.getValue(Product::class.java)

                    // Check if product ID is in the randomIds list
                    if (prod != null && randomIds.contains(prod.productRandomId)) {
                        // Fetch product imagee
                        val imageList = mutableListOf<String>()
                        val imageSnapshot = productSnapshot.child("Admin_Product_Images")
                        for (image in imageSnapshot.children) {
                            image.getValue(String::class.java)?.let { imageURL ->
                                imageList.add(imageURL)
                            }
                        }

                        // Set product image URIs
                        prod.productImageURI = imageList

                        // Set product count from `item` map if it exists
                        prod.itemCount = item[prod.productRandomId] ?: 0

                        products.add(prod)
                    }
                }

                // Send the list of products and the entire item count map
                trySend(Triple(products.toList(), item, Price )) // Send both products list and item count map
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        db1.addValueEventListener(eventListener1)
        awaitClose { db1.removeEventListener(eventListener1) }
    }
}
