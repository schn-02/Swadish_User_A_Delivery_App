package com.example.userBlinkit.Home

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blinklit.R
import com.example.blinklit.databinding.ActivityHomeBinding
import com.example.userBlinkit.Adapters.Adapter_Home
import com.example.userBlinkit.ClickedCategory
import com.example.userBlinkit.MainActivity
import com.example.userBlinkit.Profile.Profile

import home_Model

class Home : AppCompatActivity()
{
    private  lateinit var binding :ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        supportActionBar?.show()

         binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView:RecyclerView = findViewById(R.id.HomeRecycler2)
        val list = ArrayList<home_Model>()
        binding.search.setOnClickListener{
            startActivity(Intent(this , MainActivity::class.java))
        }

        binding.ProfileHome.setOnClickListener {
            startActivity(Intent(this , Profile::class.java))
        }

        list.add(home_Model(R.drawable.all, "All"))
        list.add(home_Model(R.drawable.masala, "Masala"))
        list.add(home_Model(R.drawable.atta_rice, "Atta Rice & Dal"))
        list.add(home_Model(R.drawable.cold_and_juices, "Cold Drink & Juices"))
        list.add(home_Model(R.drawable.dairy_breakfast, "Dairy and Breakfast"))
        list.add(home_Model(R.drawable.dry_masala, "Dry Masala"))
        list.add(home_Model(R.drawable.chicken_meat, "Chicken Meat & Fish"))
        list.add(home_Model(R.drawable.instant_frozen, "Instant & Frozen Foods"))
        list.add(home_Model(R.drawable.sangam_milk, "sangam Milk"))
        list.add(home_Model(R.drawable.pharma_wellness, "Pharma & Wellness"))
        list.add(home_Model(R.drawable.sauce_spreads, "Sauces & Spreads"))
        list.add(home_Model(R.drawable.sweet_tooth, "Sweet Tooth"))
        list.add(home_Model(R.drawable.vegetable, "vegetables & Fruits"))
        list.add(home_Model(R.drawable.tea, "Tea"))
        list.add(home_Model(R.drawable.tea_coffee, "Tea Coffee & Health Drinks"))
        list.add(home_Model(R.drawable.munchies, "Munchies"))
        list.add(home_Model(R.drawable.organic_premium, "Organic & Premimum "))
        list.add(home_Model(R.drawable.pet_care, "Pet Care"))
        list.add(home_Model(R.drawable.baby, "Baby Care"))
        list.add(home_Model(R.drawable.bakery_biscuits, "Bakery & Biscuits"))
        list.add(home_Model(R.drawable.toned_milk, "Toned Milk"))


        val homeAdapter = Adapter_Home(list, this,::onCategoryClicked )
        recyclerView.adapter = homeAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)

    }

    private fun onCategoryClicked(category:home_Model)
    {
        val intent = Intent(this,ClickedCategory::class.java)
        intent.putExtra("category", category.Text)
        startActivity(intent)


    }


}