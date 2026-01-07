package com.example.userBlinkit.Authentication

import android.content.Intent
import android.os.Bundle

import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.blinklit.R
import com.example.blinklit.databinding.ActivitySignupBinding
import com.example.userBlinkit.Home.Home
import com.example.userBlinkit.Models.users

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Signup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()


        // Set initial button color to grey and disable it
        binding.Signup.isEnabled = false
        binding.Signup.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))

        // Adding TextWatcher to all required fields
        binding.enemail.addTextChangedListener(signUpTextWatcher)
        binding.enpass.addTextChangedListener(signUpTextWatcher)
        binding.enconfirmpass.addTextChangedListener(signUpTextWatcher)
        binding.enname.addTextChangedListener(signUpTextWatcher)


        if (auth.currentUser!=null)
        {
                Toast.makeText(this , "Directing to home" , Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@Signup, Home::class.java))
            finish()
        }


        // Sign-up button click listener
        binding.Signup.setOnClickListener {

            signUpUser()
        }

        binding.alreadyaccount.setOnClickListener {
            startActivity(Intent(this, Signin::class.java))
            finish()
        }

    }

    private val signUpTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val emailInput = binding.enemail.text.toString().trim()
            val passInput = binding.enpass.text.toString().trim()
            val confirmPasswordInput = binding.enconfirmpass.text.toString().trim()
            val nameInput = binding.enname.text.toString().trim()

            // Enable button only if all fields are non-empty
            if (emailInput.isNotEmpty() && passInput.isNotEmpty() &&
                confirmPasswordInput.isNotEmpty() && nameInput.isNotEmpty()) {
                binding.Signup.apply {
                    setBackgroundColor(ContextCompat.getColor(this@Signup, R.color.green))
                    isEnabled = true
                }
            } else {
                binding.Signup.apply {
                    setBackgroundColor(ContextCompat.getColor(this@Signup, R.color.white))
                    isEnabled = false
                }
            }
        }
    }
    private fun signUpUser() {
        binding.loadingFood2.visibility  = View.VISIBLE

        val email = binding.enemail.text.toString().trim()
        val pass = binding.enpass.text.toString().trim()
        val confirmPassword = binding.enconfirmpass.text.toString().trim()
        val name = binding.enname.text.toString().trim()
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        // Input validation
        if (email.isEmpty() || pass.isEmpty() || confirmPassword.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            binding.loadingFood2.visibility  = View.GONE


        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            binding.loadingFood2.visibility  = View.GONE

        }
        if (pass != confirmPassword) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show()
            binding.loadingFood2.visibility  = View.GONE


        }

        val usersRef = database.reference.child("All_users").child("users")

            // Check if device ID already registered
        usersRef.orderByChild("deviceId").equalTo(deviceId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Device ID already registered
                            Toast.makeText(this@Signup, "device is already registerd Please regidtered with registered email.", Toast.LENGTH_SHORT).show()
                            binding.loadingFood2.visibility = View.GONE

                            auth.signOut()  // Logout any previously logged-in user
                            binding.loadingFood2.visibility  = View.GONE
                            startActivity(Intent(this@Signup, Signin::class.java))
                            finish()
                        } else {
                            // Proceed with registration if not registered on this device
                            setupSignUpProcess(usersRef)


                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@Signup, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()

                    }
                })
        }

        private fun setupSignUpProcess(usersRef: DatabaseReference) {

                val email = binding.enemail.text.toString()
                val pass = binding.enpass.text.toString()
                val name = binding.enname.text.toString()
                val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)


                // Check if email is already registered
                usersRef.orderByChild("userEmail").equalTo(email)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(emailSnapshot: DataSnapshot) {
                            if (emailSnapshot.exists()) {
                                Toast.makeText(this@Signup, "Email is Already Registerd .", Toast.LENGTH_SHORT).show()
                                binding.loadingFood2.visibility  = View.GONE

                                auth.signOut()  // Logout any previously logged-in user
                                startActivity(Intent(this@Signup, Signin::class.java))
                                finish()
                            } else {
                                auth.createUserWithEmailAndPassword(email, pass)
                                    .addOnCompleteListener(this@Signup) { signUpTask ->
                                        if (signUpTask.isSuccessful) {
                                            val userId = signUpTask.result.user!!.uid
                                            val newUser = users(
                                                uid = userId,
                                                userName = name,
                                                userEmail = email,
                                                deviceId = deviceId.toString(),
                                                userType = "User",
                                                userPassword = pass
                                            )
                                            usersRef.child(userId).setValue(newUser)
                                                .addOnCompleteListener { dbTask ->
                                                    if (dbTask.isSuccessful) {
                                                        Toast.makeText(this@Signup, "Signup Successful", Toast.LENGTH_SHORT).show()

                                                        startActivity(Intent(this@Signup, Home::class.java))
                                                        binding.loadingFood2.visibility  = View.GONE

                                                        finish()
                                                    } else {
                                                        Toast.makeText(this@Signup, "oops user data is not saved", Toast.LENGTH_SHORT).show()
                                                        binding.loadingFood2.visibility  = View.GONE

                                                    }
                                                }
                                        } else {
                                            val errorMsg = signUpTask.exception?.message ?: "Error"
                                            Toast.makeText(this@Signup, "Signup Failed: $errorMsg", Toast.LENGTH_SHORT).show()
                                            binding.loadingFood2.visibility  = View.GONE

                                        }
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@Signup, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                            binding.loadingFood2.visibility  = View.GONE

                        }
                    })
            }
        }







