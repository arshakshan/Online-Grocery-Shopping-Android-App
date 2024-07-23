package com.example.flash.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InternetItem(
    // since the names are different we mark it as serializable

    @SerialName(value = "stringResourceId")
    val itemName: String = "",

    @SerialName(value = "itemCategoryId")
    val itemCategory: String = "",

    @SerialName(value = "itemQuantity")
    val itemQuantity: String = "",

    @SerialName(value = "item_price")
    val itemPrice: Int = 0,

    @SerialName(value = "imageResourceId")
    val imageUrl: String = ""
)