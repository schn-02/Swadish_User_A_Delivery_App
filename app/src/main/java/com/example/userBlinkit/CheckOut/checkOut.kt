package com.example.userBlinkit.CheckOut

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
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
import com.google.firebase.database.FirebaseDatabase
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class checkOut : AppCompatActivity(), PaymentResultWithDataListener {

    private lateinit var binding: ActivityCheckOutBinding
    private lateinit var adapter: CheckoutAdapter

    private val CHANNEL_ID = "channel_id"

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private var totalPrice: Int = 0
    private var deliveryCharge: Int = 0
    private var grandTotal: Int = 0
    private var totalAmountInPaise: Long = 0

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

        Checkout.preload(applicationContext)

        createNotificationChannel()

        val randomIDs = intent.getStringArrayListExtra("checkout")
        val itemCountMap = intent.getSerializableExtra("itemCount") as? HashMap<String, Int>

        setupAmountDetails()
        setupRecyclerView()

        if (randomIDs != null && itemCountMap != null) {
            fetchCheckOutProducts(randomIDs, itemCountMap, totalPrice.toDouble())
        } else {
            Toast.makeText(this, "Checkout data missing", Toast.LENGTH_SHORT).show()
        }

        binding.PaymentNext.setOnClickListener {
            checkAddressBeforePayment()
        }
    }

    private fun setupAmountDetails() {
        totalPrice = intent.getLongExtra("itemCount2", 0L).toInt()

        deliveryCharge = if (totalPrice < 200) {
            50
        } else {
            0
        }

        grandTotal = totalPrice + deliveryCharge

        binding.subTotalBill.text = "₹$totalPrice"

        binding.deliveryChargesBill.text = if (deliveryCharge == 0) {
            "Free Delivery"
        } else {
            "₹$deliveryCharge"
        }

        binding.GrandTotalBill.text = "₹$grandTotal"

        // Razorpay amount paise me leta hai.
        // Example: ₹200 = 20000 paise
        totalAmountInPaise = grandTotal.toLong() * 100

        Log.d("CHECKOUT_AMOUNT", "Subtotal: $totalPrice")
        Log.d("CHECKOUT_AMOUNT", "Delivery Charge: $deliveryCharge")
        Log.d("CHECKOUT_AMOUNT", "Grand Total: $grandTotal")
        Log.d("CHECKOUT_AMOUNT", "Razorpay Amount Paise: $totalAmountInPaise")
    }

    private fun setupRecyclerView() {
        adapter = CheckoutAdapter()
        binding.checkoutRecycler.layoutManager = LinearLayoutManager(this)
        binding.checkoutRecycler.adapter = adapter
    }

    private fun checkAddressBeforePayment() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            Log.e("AuthError", "User is not authenticated.")
            return
        }

        val addressRef = database.reference
            .child("All_users")
            .child("users")
            .child(uid)
            .child("UserAddress")

        addressRef.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    showAddressDialog()
                    return@addOnSuccessListener
                }

                var isAnyChildEmpty = false

                for (child in snapshot.children) {
                    val value = child.getValue(String::class.java)
                    if (value.isNullOrEmpty()) {
                        isAnyChildEmpty = true
                        break
                    }
                }

                if (isAnyChildEmpty) {
                    showAddressDialog()
                } else {
                    showCustomAlertDialog()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to check address", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseError", "Failed to check address data: ${it.message}")
            }
    }

    private fun showAddressDialog() {
        val context = this
        val editAddress = AlertAddressDialogBinding.inflate(LayoutInflater.from(context))

        val alertDialog = AlertDialog.Builder(context)
            .setView(editAddress.root)
            .create()

        editAddress.AddAddress.setOnClickListener {
            val address = editAddress.Address.text.toString().trim()
            val pincode = editAddress.PinCode.text.toString().trim()
            val phoneNo = editAddress.PhoneNo.text.toString().trim()
            val state = editAddress.state.text.toString().trim()
            val district = editAddress.District.text.toString().trim()

            if (address.isEmpty() || pincode.isEmpty() || phoneNo.isEmpty() || state.isEmpty()) {
                Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = auth.currentUser?.uid

            if (uid == null) {
                Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val addressModel = Address_Model(
                address1 = address,
                pincode1 = pincode,
                phoneno1 = phoneNo,
                state1 = state,
                district1 = district
            )

            database.reference
                .child("All_users")
                .child("users")
                .child(uid)
                .child("UserAddress")
                .setValue(addressModel)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Address uploaded successfully", Toast.LENGTH_SHORT).show()
                        alertDialog.dismiss()
                    } else {
                        Toast.makeText(context, "Address upload failed", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Address failed to upload", Toast.LENGTH_SHORT).show()
                }
        }

        alertDialog.show()
    }

    private fun showCustomAlertDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_payment_gateway, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val cash = dialogView.findViewById<AppCompatImageView>(R.id.CashOn)
        val paytm = dialogView.findViewById<AppCompatImageView>(R.id.paytm)

        cash.setOnClickListener {
            dialog.dismiss()
            processCashOrder()
        }

        paytm.setOnClickListener {
            Toast.makeText(this, "Proceeding with online payment", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            paymentInt()
        }

        dialog.show()
    }

    private fun processCashOrder() {
        val progressDialog = ProgressDialog(this@checkOut)
        progressDialog.setMessage("Processing your order...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            progressDialog.dismiss()

            Toast.makeText(this@checkOut, "Food order successfully", Toast.LENGTH_SHORT).show()

            notificationForOrder()
            saveOrderData()

            startActivity(Intent(this@checkOut, checkSplash::class.java))
            finish()
        }, 4000)
    }

    private fun fetchCheckOutProducts(
        randomIds: ArrayList<String>,
        itemCount: MutableMap<String, Int>,
        price: Double
    ) {
        lifecycleScope.launch {
            viewModel.FetchAllCheckoutProducts(randomIds, itemCount, price)
                .collect { result ->
                    val products = result.first
                    val countMap = result.second

                    adapter.differr.submitList(products)

                    fetchedProducts = products
                    fetchedItemCount = countMap
                    fetchedPrice = price
                }
        }
    }

    private fun paymentInt() {
        val activity: Activity = this
        val co = Checkout()
        co.setKeyID("rzp_test_S7SN3KcymfzZgF")

        try {
            if (totalAmountInPaise <= 0) {
                Toast.makeText(this, "Invalid payment amount", Toast.LENGTH_SHORT).show()
                return
            }

            Log.d("RAZORPAY_AMOUNT", "Amount sent to Razorpay: $totalAmountInPaise paise")

            val options = JSONObject()
            options.put("name", "Swadish")
            options.put("description", "Food Order Payment")
            options.put(
                "image",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQSMY2y5A0GxVTWB-q1jS6HVPzmJeHLJ3YiyA&s"
            )
            options.put("theme.color", "#3399cc")
            options.put("currency", "INR")

            // Important:
            // Razorpay amount paise me hota hai.
            // ₹200 ke liye 20000 pass hoga.
            options.put("amount", totalAmountInPaise)

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 3)
            options.put("retry", retryObj)

            val prefill = JSONObject()
            prefill.put("email", "robbinhood846@gmail.com")
            prefill.put("contact", "9784329023")
            options.put("prefill", prefill)

            co.open(this@checkOut, options)

        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(paymentId: String?, paymentData: PaymentData?) {
        runOnUiThread {
            Toast.makeText(this@checkOut, "Payment Successful", Toast.LENGTH_SHORT).show()

            notificationForOrder()
            saveOrderData()

            startActivity(Intent(this@checkOut, checkSplash::class.java))
            finish()
        }
    }

    override fun onPaymentError(code: Int, response: String?, paymentData: PaymentData?) {
        runOnUiThread {
            Toast.makeText(this@checkOut, "Payment failed, try again", Toast.LENGTH_SHORT).show()
            Log.e("RAZORPAY_ERROR", "Code: $code Response: $response")
        }
    }

    private fun saveOrderData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val dbReference = FirebaseDatabase.getInstance()
            .getReference("All_users")
            .child("users")
            .child(uid)
            .child("userODERS")

        val products = fetchedProducts
        val itemCount = fetchedItemCount

        if (products == null || itemCount == null) {
            Log.e("OrderSave", "Products or item count is null")
            return
        }

        for (product in products) {
            val productId = product.productRandomId ?: continue

            val formattedDate = try {
                val inputFormats = listOf(
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()),
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                )

                var parsedDate: Date? = null

                for (format in inputFormats) {
                    try {
                        parsedDate = format.parse(product.dateTime)
                        if (parsedDate != null) break
                    } catch (_: Exception) {
                    }
                }

                if (parsedDate != null) {
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(parsedDate)
                } else {
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                }

            } catch (e: Exception) {
                Log.e("DateError", "Date parse failed: ${product.dateTime}", e)
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            }

            val productData = mapOf(
                "productRandomId" to product.productRandomId,
                "productName" to product.productTitle,
                "productPrice" to product.productPrice,
                "productCount" to (itemCount[productId] ?: 0),
                "productImageURI" to product.productImageURI,
                "product_Date" to formattedDate,
                "AdminUID" to product.adminUID,
                "Status" to product.Status,
                "orderSubTotal" to totalPrice,
                "deliveryCharge" to deliveryCharge,
                "grandTotal" to grandTotal
            )

            dbReference.child(product.productRandomId)
                .setValue(productData)
                .addOnSuccessListener {
                    Log.d("Database", "Product $productId saved successfully.")
                }
                .addOnFailureListener { e ->
                    Log.e("Database", "Failed to save product $productId: ${e.message}")
                }
        }
    }

    private fun notificationForOrder() {
        val builder = NotificationCompat.Builder(this@checkOut, CHANNEL_ID)
            .setSmallIcon(R.drawable.letters)
            .setContentTitle("Swadish")
            .setContentText("Food order successfully... 😊")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(this@checkOut)

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notificationManager.notify(1, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                "Swadish",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.description = "Food Orders"

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}