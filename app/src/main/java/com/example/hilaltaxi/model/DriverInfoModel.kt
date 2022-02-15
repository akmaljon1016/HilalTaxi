package com.example.hilaltaxi.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DriverInfoModel(
    var firstName: String = "",
    var lastName: String = "",
    var phoneNumber: String = "",
    var rating: Double = 0.0
) : Parcelable