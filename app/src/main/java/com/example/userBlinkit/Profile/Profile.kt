package com.example.userBlinkit.Profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.blinklit.R
import com.example.blinklit.databinding.ActivityProfileBinding
import com.example.userBlinkit.About
import com.example.userBlinkit.Authentication.Signin
import com.example.userBlinkit.Authentication.Signup
import com.example.userBlinkit.MainActivity
import com.example.userBlinkit.Orders.Orders

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Profile : AppCompatActivity()
{
    lateinit var  binding : ActivityProfileBinding

    lateinit var database: DatabaseReference
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.backprofile.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


        binding.deleteAccount.setOnClickListener{
            val dialog = AlertDialog.Builder(this)
                dialog.setMessage("Are you sure you want to Delete ?")

                 dialog.setPositiveButton("Yes"){dialog ,_->
                     deleteAccount()
                }

            dialog.setNegativeButton("No"){dialog , _->

                dialog.dismiss()

            }
            dialog.setNeutralButton("Help"){dialog , _->
                startActivity(Intent(this, About::class.java))

            }
            val alertDialog = dialog.create()
            alertDialog.show()


        }



        fetchPhoneNumber { phone ->
            if (phone != null) {
                binding.number.text = phone
            } else {
                binding.number.text = "No phone found"
            }
        }

         binding.orders.setOnClickListener {
             startActivity(Intent(this, Orders::class.java))

         }

        binding.About.setOnClickListener{
            startActivity(Intent(this, About::class.java))
        }

        binding.LogOut.setOnClickListener{
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage("Are you sure you want to Logout ?")

            dialog.setPositiveButton("Yes"){dialog ,_->
                logOut()
            }

            dialog.setNegativeButton("No"){dialog , _->

                dialog.dismiss()

            }
            dialog.setNeutralButton("Help"){dialog , _->
                startActivity(Intent(this, About::class.java))

            }
            val alertDialog = dialog.create()
            alertDialog.show()
        }
        binding.AddressBook.setOnClickListener {
                   EditAddress()
        }}

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()

        // Start the Signin activity
        val intent = Intent(this@Profile, Signin::class.java)

        // Clear the activity stack and prevent going back to the Profile activity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }

    private fun deleteAccount() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            val db = FirebaseDatabase.getInstance().reference
                .child("All_users")
                .child("users")
                .child(uid)

            db.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Firebase Authentication se user ko delete karna
                    FirebaseAuth.getInstance().currentUser?.delete()?.addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            // Sign out the user after successful deletion
                            FirebaseAuth.getInstance().signOut()
                            // User successfully deleted from both DB and Authentication
                            Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, Signup::class.java))
                            finish()
                        } else {
                            // Handle failure for authentication deletion
                            Toast.makeText(this, "Failed to delete user from authentication: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Handle failure for database deletion
                    Toast.makeText(this, "Failed to delete user from database: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPhoneNumber(callback: (String?) -> Unit) {
        database = FirebaseDatabase.getInstance().reference.child("All_users").child("users")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    callback(null)
                    return
                }

                for (userSnapshot in snapshot.children) {
                    val userOrderAddress = userSnapshot.child("UserAddress")
                    if (userOrderAddress.exists()) {
                        val phone = userOrderAddress.child("phoneno1").getValue(String::class.java)
                        callback(phone) // Pass the phone number back
                        return
                    }
                }
                callback(null) // No phone number found
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Profile, "Failed to fetch address: ${error.message}", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }


    private fun EditAddress() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val context = this // or use context if in fragment or any other context
        // Database reference pointing to the user's address data
        val database = FirebaseDatabase.getInstance().reference.child("All_users").child("users").child(uid!!).child("UserAddress")

        // Inflate the custom layout
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.alert_address, null)

        // Create an AlertDialog Builder
        val alertDialogBuilder = AlertDialog.Builder(context)

        // Set the custom layout as the dialog's view
        alertDialogBuilder.setView(dialogView)
        alertDialogBuilder.setTitle("Edit Address")
        alertDialogBuilder.setCancelable(true)

        // Create the AlertDialog object
        val dialog = alertDialogBuilder.create()
        dialog.show()

        // Initialize Views
        val edit = dialog.findViewById<Button>(R.id.EditAddress)!!
        val save = dialog.findViewById<Button>(R.id.SaveAddress)!!
        val State = dialog.findViewById<EditText>(R.id.state1)!!
        val District = dialog.findViewById<EditText>(R.id.District1)!!
        val pincode = dialog.findViewById<EditText>(R.id.PinCode1)!!
        val phoneno = dialog.findViewById<EditText>(R.id.PhoneNo1)!!
        val Address = dialog.findViewById<EditText>(R.id.Address1)!!

        // Load data from Firebase
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val address = snapshot.child("address1").getValue(String::class.java)
                val district = snapshot.child("district1").getValue(String::class.java)
                val phone = snapshot.child("phoneno1").getValue(String::class.java)
                val pin = snapshot.child("pincode1").getValue(String::class.java)
                val state = snapshot.child("state1").getValue(String::class.java)

//
//                if (phone != null) {
//                    // Set the phone number to the binding view
//                    binding.number.text = phone
//                } else {
//                    // Handle the case where the phone number is not found
//                    Toast.makeText(context, "Phone number not found", Toast.LENGTH_SHORT).show()
//                }

                // Populate the fields with the data
                Address.setText(address)
                District.setText(district)
                phoneno.setText(phone)
                pincode.setText(pin)
                State.setText(state)

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load address", Toast.LENGTH_SHORT).show()
            }
        })

        // Enable editing when Edit button is clicked
        edit.setOnClickListener {
            Address.isEnabled = true
            District.isEnabled = true
            phoneno.isEnabled = true
            pincode.isEnabled = true
            State.isEnabled = true
        }

        // Save changes to Firebase
        save.setOnClickListener {
            val text = Address.text.toString()
            val pinCode = pincode.text.toString()
            val stateText = State.text.toString()
            val districtText = District.text.toString()
            val number = phoneno.text.toString()

             Toast.makeText(this , "ye lo $number", Toast.LENGTH_SHORT).show()

            // Create the updates hash map
            val updates = hashMapOf<String, Any>(
                "address1" to text,
                "pincode1" to pinCode,
                "state1" to stateText,
                "district1" to districtText,
                "phoneno1" to number
            )

            // Update the Firebase database with new values
            database.updateChildren(updates).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Address updated successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss() // Close the dialog after successful update
                } else {
                    Toast.makeText(context, "Failed to update address", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}



