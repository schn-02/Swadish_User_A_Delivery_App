package com.example.userBlinkit.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.blinklit.R
import com.example.userBlinkit.Models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Orders_Adapter(val context:Context , val list:ArrayList<Product>):RecyclerView.Adapter<Orders_Adapter.viewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {

     val view:View = LayoutInflater.from(context).inflate(R.layout.sample_layout_orders, parent,false)

        return viewHolder(view)
    }

    override fun getItemCount(): Int {
             return list.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val item = list[position]

        holder.price.text = item.productPrice.toString()
        holder.Date.text = item.dateTime
        holder.Title.text = item.productTitle
        holder.count.text = item.itemCount.toString()
        holder.remove.visibility = View.VISIBLE


         holder.remove.setOnClickListener {

             val dialog = AlertDialog.Builder(context)
             dialog.setMessage("Are you sure  want to Delete your order..😢 \"Please wait user , your order request  will accept soon..😊?")
             dialog.setPositiveButton("Yes"){dialog ,_->
                 removeorders(position)
                  notifyItemRemoved(position)             }

             dialog.setNegativeButton("No"){dialog , _->

                 dialog.dismiss()

             }
             val alertDialog = dialog.create()
             alertDialog.show()


         }
        if (item.Status == "Accept") {
            holder.status2.text = "Accept"
            holder.status2.setTextColor(Color.BLUE)
            holder.remove.visibility = View.GONE

        }

        if (item.Status == "Delieverd") {
            holder.status2.text = "Ordered"
            holder.status2.setTextColor(Color.GREEN)
            holder.remove.visibility = View.GONE

        }
        if (item.Status == "Reject") {
            holder.status2.text = "Reject"
            holder.status2.setTextColor(Color.RED)
            holder.remove.visibility = View.GONE
        }
    }
    private fun removeorders(position: Int) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseDatabase.getInstance().reference
            .child("All_users")
            .child("users")
            .child(uid!!)
            .child("userODERS")

        // Firebase query to fetch the data
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(context, "Invalid data!", Toast.LENGTH_SHORT).show()
                    return
                }

                var countorders = 0
                for (orderSnapshot in snapshot.children) {
                    if (countorders == position) {
                        // Delete the specific order
                        orderSnapshot.ref.removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Order removed successfully!", Toast.LENGTH_SHORT).show()
                                notifyDataSetChanged()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Failed to remove order: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        return // Exit the loop after deleting the item
                    }
                    countorders++
                }

                if (countorders <= position) {
                    Toast.makeText(context, "Position not found!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Operation cancelled: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    class viewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        val Date = itemView.findViewById<TextView>(R.id.Date)
        val Title = itemView.findViewById<TextView>(R.id.ProductTitle)
        val price = itemView.findViewById<TextView>(R.id.price)
        val status2 = itemView.findViewById<TextView>(R.id.status2)
        val count = itemView.findViewById<TextView>(R.id.count)
        val remove = itemView.findViewById<ImageView>(R.id.deleteOrder)
    }
}



