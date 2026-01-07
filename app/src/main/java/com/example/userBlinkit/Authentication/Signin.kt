package com.example.userBlinkit.Authentication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.blinklit.R
import com.example.blinklit.databinding.ActivitySigninBinding
import com.example.userBlinkit.Home.Home

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Signin : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySigninBinding

    private lateinit var database: FirebaseDatabase




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.signin.isEnabled = false
        binding.signin.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))

        // Adding TextWatcher to both email and password fields
        binding.enemail2.addTextChangedListener(loginTextWatcher)
        binding.enpass2.addTextChangedListener(loginTextWatcher)

        // Sign-in button click listener
        binding.signin.setOnClickListener {

                login()
        }

        binding.dontaccount.setOnClickListener {
            startActivity(Intent(this@Signin, Signup::class.java))
            finish()
        }
    }

    private val loginTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val emailInput = binding.enemail2.text.toString().trim()
            val passInput = binding.enpass2.text.toString().trim()

            if (emailInput.isNotEmpty() && passInput.isNotEmpty()) {
                // Enable button and set color to green
                binding.signin.apply {
                    setBackgroundColor(ContextCompat.getColor(this@Signin, R.color.green))
                    isEnabled = true
                }
            } else {
                // Keep button grey and disabled if fields are empty
                binding.signin.apply {
                    setBackgroundColor(ContextCompat.getColor(this@Signin, R.color.white))
                    isEnabled = false
                }
            }
        }
    }

    private fun login()
    {
        val email = binding.enemail2.text.toString()
        val pass = binding.enpass2.text.toString()
        binding.loadingFood.visibility = View.VISIBLE


        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful)
            {
                val uid = auth.currentUser?.uid

                if (uid != null) {
                    database.reference.child("All_users").child("users").child(uid).get().addOnSuccessListener {
                        val userType = it.child("userType").getValue(String::class.java)

                        if(userType =="User")
                        {
                            Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()
                            binding.loadingFood.visibility = View.GONE
                            startActivity(Intent(this, Home::class.java))
                            finish()
                        }
                        else
                        {
                            Toast.makeText(this, "only User can login in this app   ", Toast.LENGTH_LONG).show()
                            binding.loadingFood.visibility = View.GONE


                        }
                    }
                }
            }
        }
    }
}


