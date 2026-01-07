package com.example.userBlinkit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.blinklit.R
import com.example.blinklit.databinding.ActivityMainBinding
import com.example.userBlinkit.Adapters.item_view_Recycler_Adapter
import com.example.userBlinkit.CheckOut.checkOut
import com.example.userBlinkit.Home.Home
import com.example.userBlinkit.Models.Product
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity()
{
    private  lateinit var binding: ActivityMainBinding
    private var viewModels =UserAuthModel()

    var adapter = item_view_Recycler_Adapter(this, ::CheckOut)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
             startActivity(Intent(this@MainActivity, Home::class.java))
             finish()
         }



        binding.search.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(Text: CharSequence?, start: Int, before: Int, count: Int) {
                 val searchText =Text.toString().trim()
                  adapter.filter.filter(searchText)
            }
            override fun afterTextChanged(s: Editable?) {
            }

        })
        getAllProducts()

    }


    private fun getAllProducts() {
        binding.shimmer.visibility = View.VISIBLE

        lifecycleScope.launch {
                viewModels.FetchAllProducts("All").collect{
                       adapter = item_view_Recycler_Adapter(this@MainActivity , ::CheckOut)
                    binding.homeRecycler.adapter= adapter
                    binding.shimmer.visibility= View.GONE
                    adapter.differ.submitList(it)
                    adapter.original = it as ArrayList<Product>

                }
            }
    }

    private fun CheckOut(randomID: ArrayList<String>, itemCount: MutableMap<String, Int> , TotalPrice:Long) {
        val intent = Intent(this, checkOut::class.java)


        // Pass `randomID` as usual
        intent.putStringArrayListExtra("checkout", randomID)

        // Convert `itemCount` to a `HashMap` and pass it
        val itemCountHashMap = HashMap(itemCount) // Convert to HashMap
        intent.putExtra("itemCount", itemCountHashMap )
        intent.putExtra("itemCount2", TotalPrice )

        startActivity(intent)
        finish()
    }

}