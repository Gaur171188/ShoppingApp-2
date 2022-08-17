package com.shoppingapp.info.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "products")
data class Product (
    @PrimaryKey
    val productId: String = "",
    var name: String = "",
    val owner: String = "",
    var description: String = "",
    val category: String = "",
    var price: Double = 0.0,
    var mrp: Double = 0.0,
    var availableSizes: List<Int?> = ArrayList(),
    var availableColors: List<String?> = ArrayList(),
    var images: List<String> = ArrayList(),
    val country: String = "",
    val rating: Double = 0.0
): Parcelable {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "productId" to productId,
            "name" to name,
            "owner" to owner,
            "description" to description,
            "category" to category,
            "price" to price,
            "availableSizes" to availableSizes,
            "availableColors" to availableColors,
            "images" to images,
            "country" to country,
            "rating" to rating
        )
    }
}

