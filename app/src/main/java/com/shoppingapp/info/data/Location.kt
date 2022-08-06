package com.shoppingapp.info.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
):Parcelable
