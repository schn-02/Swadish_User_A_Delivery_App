package com.example.userBlinkit.CheckOut

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log

import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blinklit.R
import com.example.blinklit.databinding.ActivityCheckOutBinding
import com.example.blinklit.databinding.AlertAddressDialogBinding
import com.example.userBlinkit.Adapters.CheckoutAdapter
import com.example.userBlinkit.Models.Address_Model
import com.example.userBlinkit.Models.Product
import com.example.userBlinkit.Splash.checkSplash
import com.example.userBlinkit.UserAuthModel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class checkOut : AppCompatActivity() , PaymentResultWithDataListener {

    private lateinit var binding: ActivityCheckOutBinding
    private lateinit var adapter: CheckoutAdapter
    private  var CHANNEL_ID = "channel id"
     lateinit var database  :FirebaseDatabase
     lateinit var auth  :FirebaseAuth
      var TotalPrice:Int = 0
     var totalAmountInPaise:Long =0
    private var fetchedProducts: List<Product>? = null
    private var fetchedItemCount: Map<String, Int>? = null
    private var fetchedPrice: Double? = null
    private val viewModel = UserAuthModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()


        val co  =Checkout()
        co.setKeyID("rzp_test_2umtP5sifdGMk1")
        NotificationChannel()
        // Receive multiple random IDs from intent
        val randomIDs = intent.getStringArrayListExtra("checkout")
        val itemCountMap = intent.getSerializableExtra("itemCount") as? HashMap<String, Int>
         TotalPrice = intent.getLongExtra("itemCount2", 0).toInt()
        binding.subTotalBill.text = TotalPrice.toString()
        if (TotalPrice <200)
        {
            binding.GrandTotalBill.text = (50.0 + TotalPrice).toString()
            binding.deliveryChargesBill.text = "50"

        }
        else
        {
            binding.GrandTotalBill.text =  TotalPrice.toString()
            binding.deliveryChargesBill.text = "Free Deilvery"

            val amountString = binding.GrandTotalBill.text.toString()

            val amountInDouble = amountString.toDouble()

             totalAmountInPaise = (amountInDouble * 100).toLong()


        }

        binding.PaymentNext.setOnClickListener {
            val uid = auth.currentUser?.uid

            if (uid != null) {
                val AddressREF = database.reference
                    .child("All_users")
                    .child("users")
                    .child(uid)
                    .child("UserAddress")

                // Check if the address node exists
                AddressREF.get().addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        // Address does not exist, show the address dialog
                        showAddressDialog()
                    } else {
                        var isAnyChildEmpty = false

                        // Iterate through all children to check for empty values
                        for (child in snapshot.children) {
                            val value = child.getValue(String::class.java)
                            if (value.isNullOrEmpty()) {
                                isAnyChildEmpty = true
                                break
                            }
                        }

                        if (isAnyChildEmpty) {
                            // If any child is empty, show the address dialog
                            showAddressDialog()
                        } else {
                            // If all children are non-empty, show the custom alert dialog
                            showCustomAlertDialog()
                        }
                    }
                }.addOnFailureListener {
                    Log.e("FirebaseError", "Failed to check address data: ${it.message}")
                }
            } else {
                Log.e("AuthError", "User is not authenticated.")
            }
        }

        // Setup RecyclerView and Adapter
        adapter = CheckoutAdapter()
        binding.checkoutRecycler.layoutManager = LinearLayoutManager(this)
        binding.checkoutRecycler.adapter = adapter
        if (randomIDs != null && itemCountMap != null && TotalPrice !==null) {
            fetchCheckOutProducts(randomIDs , itemCountMap , TotalPrice.toDouble())
        }


    }

    private fun showAddressDialog() {


            // Get the current activity context
            val context1 = this // or use context if in fragment or any other context
            val editAddress = AlertAddressDialogBinding.inflate(LayoutInflater.from(context1))
           editAddress.apply {
               val alertDialog = AlertDialog.Builder(context1)
                   .setView(editAddress.root)
                   .create()

               AddAddress.setOnClickListener {

                   val address =  Address.text.toString()
                   val pincode = PinCode.text.toString()
                   val phoneno = PhoneNo.text.toString()
                   val state = state.text.toString()

                    if (address.isEmpty()||pincode.isEmpty()|| phoneno.isEmpty()||state.isEmpty())
                    {
                        Toast.makeText(context1, "Please fill All Details" , Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }



                   val district  = District.text.toString()
                   val addressModel = Address_Model(address1 = address , pincode1 =pincode , phoneno1 = phoneno
                       ,state1= state, district1 = district)
                   val uid = auth.currentUser?.uid
                        database.reference.child("All_users").child("users").child(uid!!).child("UserAddress").setValue(addressModel).addOnCompleteListener {
                           if (it.isSuccessful)
                           {

                               Toast.makeText(context1, "Address Uploaded Successfuly", Toast.LENGTH_SHORT).show()
                               alertDialog.dismiss()
                           }
                       }.addOnFailureListener {
                           Toast.makeText(context1, "Address Failed  to Uploaded", Toast.LENGTH_SHORT).show()

                       }
               }



               alertDialog.show()

             }




    }

    fun showCustomAlertDialog() {

        // Get the current activity context
        val context = this // or use context if in fragment or any other context

        // Inflate the custom layout
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.alert_payment_gateway, null)

        // Create an AlertDialog Builder
        val alertDialogBuilder = AlertDialog.Builder(context)

        // Set the custom layout as the dialog's view
        alertDialogBuilder.setView(dialogView)

        // Optionally set properties like title, buttons, etc
        alertDialogBuilder.setCancelable(true)  // Dialog can be cancelled by pressing outside or back button

        // Create the AlertDialog object
        val dialog = alertDialogBuilder.create()

        // Get references to buttons from the custom layout (if needed)
        val cash = dialogView.findViewById<AppCompatImageView>(R.id.CashOn) // Replace with your actual button ID
        val paytm = dialogView.findViewById<AppCompatImageView>(R.id.paytm) // Replace with your actual button ID

        // Set button click listeners

        cash.setOnClickListener {
            // Progress Dialog create karo
            val progressDialog = ProgressDialog(this@checkOut)
            progressDialog.setMessage("Processing your order...")
            progressDialog.setCancelable(false) // Dialog ko cancel nahi kar sakte
            progressDialog.show()

            // 4 seconds ka delay
            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss() // Dialog dismiss karo
                Toast.makeText(this@checkOut, "Food order Successfully", Toast.LENGTH_SHORT).show()
                NotificationForOrder()
                saveOrderData() // Aapka function call

                startActivity(Intent(this@checkOut, checkSplash::class.java)) // Home activity open karo
                finish()
            }, 4000) // 4000ms = 4 seconds
        }


        paytm.setOnClickListener {
            // Add your logic for Paytm button
            Toast.makeText(context, "Proceeding with Paytm", Toast.LENGTH_SHORT).show()
            paymentInt()
            dialog.dismiss()


        }

        // Show the AlertDialog
        dialog.show()
    }




    private fun fetchCheckOutProducts(randomIds: ArrayList<String>, itemCount: MutableMap<String, Int> , Price :Double) {
        lifecycleScope.launch {
            viewModel.FetchAllCheckoutProducts(randomIds, itemCount ,Price).collect { (products, itemCount) ->
                adapter.differr.submitList(products)

                fetchedProducts = products
                fetchedItemCount = itemCount
                fetchedPrice = Price
            }
        }
    }


        private fun paymentInt() {
            val activity: Activity = this
            val co = Checkout()
            co.setKeyID("rzp_test_WimZh97r2MKJGf") // Your Razorpay key

            try {
                val options = JSONObject()
                options.put("name", "Swadish")
                options.put("description", "Food App")
                options.put("image", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQSMY2y5A0GxVTWB-q1jS6HVPzmJeHLJ3YiyA&s")
                options.put("theme.color", "#3399cc")
                options.put("currency", "INR")
                options.put("amount", totalAmountInPaise) // Make sure this is an integer

                val retryObj = JSONObject()
                retryObj.put("enabled", true)
                retryObj.put("max_count", 70)
                options.put("retry", retryObj)

                val prefill = JSONObject()
                prefill.put("email", "robbinhood846@gmail.com")
                prefill.put("contact", "9784329023")
                options.put("prefill", prefill)

                co.open(this@checkOut, options)
            } catch (e: Exception) {
                Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }


        override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        runOnUiThread {
            startActivity(Intent(this@checkOut, checkSplash::class.java))
            Toast.makeText(this@checkOut , "Payment SuccessFull", Toast.LENGTH_SHORT).show()
            NotificationForOrder()
            saveOrderData()
            finish()

        }
    }



    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        runOnUiThread {
            Toast.makeText(this@checkOut, "Error :PAYMENT FAILED ...TRY AGAIN", Toast.LENGTH_SHORT).show()
        }
    }




    private fun saveOrderData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val dbReference = FirebaseDatabase.getInstance().getReference("All_users")
            .child("users").child(uid!!).child("userODERS")

        // Ensure fetchedProducts and other data are not null
        fetchedProducts?.let { products ->
            fetchedItemCount?.let { itemCount ->
                for (product in products) {
                    val productId = product.productRandomId ?: continue // Skip if product ID is null

                    // Check if product.dateTime is a valid String and convert it to Date
                    val formattedDate = try {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        // Try parsing the product.dateTime string
                        sdf.parse(product.dateTime)?.let {
                            // If parsing is successful, format it back to the desired format
                            sdf.format(it)
                        } ?: throw Exception("Invalid date format")
                    } catch (e: Exception) {
                        // Handle parsing errors, log them and fallback to the current date
                        Log.e("DateError", "Invalid date format: ${product.dateTime}, using current date instead", e)
                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()) // Fallback to current date
                    }


                    // Create a map to store product data in Firebase
                    val productData = mapOf(
                        "productRandomId" to product.productRandomId,
                        "productName" to product.productTitle,
                        "productPrice" to product.productPrice,
                        "productCount" to (itemCount[productId] ?: 0), // Convert count to Int
                        "productImageURI" to product.productImageURI, // Assuming productImageURI is a List<String>
                        "product_Date" to formattedDate,
                        "AdminUID" to product.adminUID,
                        "Status" to product.Status
                    )

                    // Use push() to add a unique key for each product
                    dbReference.child(product.productRandomId)  .setValue(productData)
                        .addOnSuccessListener {
                            Log.d("Database", "Product $productId saved successfully.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Database", "Failed to save product $productId: ${e.message}")
                        }
                }
            }
        }
    }

    private  fun NotificationForOrder()
    {
         val builder = NotificationCompat.Builder(this@checkOut , CHANNEL_ID)
            builder.setSmallIcon(R.drawable.slack)
                .setContentTitle("Swadish")
                .setContentText("Food Order Successfully...😊")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
               with(NotificationManagerCompat.from(this@checkOut))
               {
                   if (ActivityCompat.checkSelfPermission(
                           applicationContext,
                           Manifest.permission.POST_NOTIFICATIONS
                       ) != PackageManager.PERMISSION_GRANTED
                   ) {

                       return
                   }
                   notify(1, builder.build())
               }

    }

    private  fun NotificationChannel()
    {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
             val channel = android.app.NotificationChannel(
                 CHANNEL_ID,
                 "Swadish",
                 NotificationManager.IMPORTANCE_DEFAULT
             )
            channel.description = " Food Orders"
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
