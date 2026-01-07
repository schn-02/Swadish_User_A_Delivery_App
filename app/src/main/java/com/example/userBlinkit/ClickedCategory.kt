package com.example.userBlinkit

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.blinklit.R
import com.example.blinklit.databinding.ActivityClickedCategoryBinding
import com.example.userBlinkit.Adapters.item_view_Recycler_Adapter
import com.example.userBlinkit.CheckOut.checkOut
import com.example.userBlinkit.Models.Product
import kotlinx.coroutines.launch

class ClickedCategory : AppCompatActivity() {
    lateinit var binding: ActivityClickedCategoryBinding
    private var viewModel = UserAuthModel()
    private  var category: String?=null
    var adapter = item_view_Recycler_Adapter(this ,::CheckOut)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
         binding = ActivityClickedCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarCategory)  // 'toolbar' aapke custom toolbar ka reference


        category = intent.getStringExtra("category")
        fetchCategoryProduct()

        setToolbarTitle()
    }

    private  fun CheckOut(randomID:ArrayList<String> , itemCount: MutableMap<String, Int> , price:Long)
    {

        val intent = Intent(this, checkOut::class.java)

        // Pass `randomID` as usual
        intent.putStringArrayListExtra("checkout", randomID)

        // Convert `itemCount` to a `HashMap` and pass it
        val itemCountHashMap = HashMap(itemCount) // Convert to HashMap
        intent.putExtra("itemCount", itemCountHashMap)
        intent.putExtra("itemCount2", price )


        startActivity(intent)
    }


    private fun fetchCategoryProduct() {
             binding.shimmerClicked.visibility = View.VISIBLE

        lifecycleScope.launch {
            viewModel.FetchAllProducts(category.toString()).collect{it->

                if(it.isEmpty())
                {
                  binding.clickedRecyler.visibility= View.GONE
                  binding.noProducts.visibility= View.VISIBLE
                    binding.loadingFood5.visibility = View.VISIBLE
                }

                else
                {
                    binding.clickedRecyler.visibility= View.VISIBLE
                    binding.noProducts.visibility= View.GONE
                    binding.loadingFood5.visibility = View.GONE

                }

                adapter = item_view_Recycler_Adapter(this@ClickedCategory, ::CheckOut)
                binding.clickedRecyler.adapter = adapter
                adapter.differ.submitList(it)
                adapter.original = it as ArrayList<Product>
                binding.shimmerClicked.visibility= View.GONE
            }
        }

    }

    private fun setToolbarTitle() {
        binding.toolbarCategory.title = category
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menuclicked, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search12) {
            startActivity(Intent(this@ClickedCategory, MainActivity::class.java))
            return true  // Yahan true return karna hoga to indicate ki item ka action handle ho gaya hai
        }
        return super.onOptionsItemSelected(item)
    }



}