package com.example.userBlinkit.Orders

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blinklit.databinding.ActivityOrdersBinding
import com.example.userBlinkit.Adapters.Orders_Adapter
import com.example.userBlinkit.Models.Product
import com.example.userBlinkit.Profile.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Orders : AppCompatActivity() {

    private lateinit var binding: ActivityOrdersBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapter: Orders_Adapter
    private lateinit var dataList: ArrayList<Product>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize RecyclerView

        binding.backorder3.setOnClickListener {
            startActivity(Intent(this , Profile::class.java))
            finish()
        }
        dataList = ArrayList()
        adapter = Orders_Adapter(this, dataList)
        binding.orderRecycler.layoutManager = LinearLayoutManager(this)
        binding.orderRecycler.adapter = adapter

        binding.orderno.visibility = View.VISIBLE

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        database = FirebaseDatabase.getInstance()
            .reference
            .child("All_users")
            .child("users")
            .child(uid)
            .child("userODERS")

        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear() // Clear the list to avoid duplicates
                for (dataSnapshot in snapshot.children) {
                    val productName = dataSnapshot.child("productName").getValue(String::class.java)
                    val productPrice = dataSnapshot.child("productPrice").getValue(String::class.java)
                    val productItemCount = dataSnapshot.child("productCount").getValue(Int::class.java) ?: 0
                    val dateTime = dataSnapshot.child("product_Date").getValue(String::class.java)
                    val status = dataSnapshot.child("Status").getValue(String::class.java)


                    // Make sure productPrice is a valid number (e.g., Double or Float)
                    val price = productPrice?.toDoubleOrNull() ?: 0.0
                    val totalPrice = price * productItemCount

                    Toast.makeText(this@Orders, "sun :-$totalPrice", Toast.LENGTH_SHORT).show()

                    if (productName != null && dateTime != null) {
                        binding.orderno.visibility = View.GONE

                        val product = Product(
                            productTitle = productName,
                            productPrice = totalPrice.toString(),  // Convert totalPrice to string
                            dateTime = dateTime,
                            itemCount = productItemCount,
                            Status =  status

                        )
                        dataList.add(product)
                    }
                }
                adapter.notifyDataSetChanged() // Notify the adapter of the data change
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Orders, "Failed to fetch data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
