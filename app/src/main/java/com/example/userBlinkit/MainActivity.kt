package com.example.userBlinkit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.blinklit.databinding.ActivityMainBinding
import com.example.userBlinkit.Adapters.item_view_Recycler_Adapter
import com.example.userBlinkit.CheckOut.checkOut
import com.example.userBlinkit.Home.Home
import com.example.userBlinkit.Models.Product
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var viewModels = UserAuthModel()
    private lateinit var adapter: item_view_Recycler_Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.itemCartMain.visibility = View.GONE
        binding.Next1.visibility = View.VISIBLE
        binding.showCount.visibility = View.VISIBLE

        adapter = item_view_Recycler_Adapter(this, ::CheckOut)
        binding.homeRecycler.adapter = adapter

        binding.back.setOnClickListener {
            startActivity(Intent(this@MainActivity, Home::class.java))
            finish()
        }

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                text: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val searchText = text.toString().trim()
                adapter.filter.filter(searchText)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        getAllProducts()
    }

    private fun getAllProducts() {
        binding.shimmer.visibility = View.VISIBLE
        binding.itemCartMain.visibility = View.GONE

        lifecycleScope.launch {
            viewModels.FetchAllProducts("All").collect { productList ->

                binding.shimmer.visibility = View.GONE

                adapter.differ.submitList(productList)
                adapter.original = ArrayList(productList as List<Product>)
            }
        }
    }

    private fun CheckOut(
        randomID: ArrayList<String>,
        itemCount: MutableMap<String, Int>,
        totalPrice: Long
    ) {
        val intent = Intent(this, checkOut::class.java)

        intent.putStringArrayListExtra("checkout", randomID)

        val itemCountHashMap = HashMap(itemCount)
        intent.putExtra("itemCount", itemCountHashMap)
        intent.putExtra("itemCount2", totalPrice)

        startActivity(intent)
        finish()
    }
}