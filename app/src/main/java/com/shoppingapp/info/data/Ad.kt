package com.shoppingapp.info.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*


@Parcelize
data class Ad (
    val id: String = "",
    var image: String = "",
    val date: Date = Date(),
    var title: String? = "",
    val destination: Int = 0
):Parcelable{
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "image" to image,
            "date" to date,
            "title" to title!!,
            "destination" to destination
        )
    }
}

