package com.example.userBlinkit.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blinklit.R
import home_Model

class Adapter_Home(val list: ArrayList<home_Model>, val context: Context, val onCategoryClicked: (home_Model) -> Unit):
    RecyclerView.Adapter<Adapter_Home.viewholder>()
{

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): viewholder
        {
           val view:View = LayoutInflater.from(context).inflate(R.layout.sample_layout_item_product_home , p0 , false)
            return viewholder(view)
    }

    override fun getItemCount(): Int
    {
        return list.size
    }

    override fun onBindViewHolder(holder:viewholder, p1: Int)
    {
        val  home = list[p1]
        holder.img.setImageResource(home.Image)
        holder.text.setText(home.Text)

        holder.img.setOnClickListener {
            onCategoryClicked(home)
        }



    }

    class  viewholder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var img: ImageView =  itemView.findViewById(R.id.imageView3)
        var text: TextView = itemView.findViewById(R.id.textView11)
    }
}