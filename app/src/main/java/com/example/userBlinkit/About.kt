package com.example.userBlinkit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.blinklit.databinding.ActivityAboutBinding


class About: AppCompatActivity()
{    lateinit var binding : ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.instagram.setOnClickListener{
             instagram()
        }

        binding.gmail.setOnClickListener {
            gmail()
        }
        binding.linkedin.setOnClickListener {
            linkedin()
        }

    }
    private fun instagram(){

            val intent = Intent()
            intent.setAction(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.setData(Uri.parse("https://www.instagram.com/schn_rwt_02/"))
            startActivity(intent)

    }
    private fun linkedin(){


            val intent = Intent()
            intent.setAction(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.setData(Uri.parse("https://www.linkedin.com/in/sachin-rawat-542aab2b0/"))
            startActivity(intent)

    }

    private fun gmail(){
            val intent = Intent()
            intent.setAction(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.setData(Uri.parse("mailto:robbinhood846@gmail.com"))
            startActivity(intent)

    }
}