package com.example.userBlinkit.Models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class Product(
    var productRandomId: String = UUID.randomUUID().toString(),
    var productTitle: String? = null,
    var ProductQuantity: String? = null,
    var ProductUnit: String? = null,
    var productPrice: String? = null,
    var productStock: String? = null,
    var MobileNo: String? = null,
    var Status: String? = "Waiting",
    var productCategory: String? = null,
    var productType: String? = null,
    var itemCount: Int = 1,
    var dateTime: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),  // Set current date here
    var adminUID: String? = null,
    var productImageURI: MutableList<String>? = mutableListOf() // List to store multiple image URLs
)
