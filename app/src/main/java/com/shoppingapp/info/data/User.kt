package com.shoppingapp.info.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.firebase.firestore.PropertyName
import com.shoppingapp.info.utils.ObjectListTypeConvertor
import com.shoppingapp.info.utils.OrderStatus
import com.shoppingapp.info.utils.UserType
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList


@Parcelize
@Entity(tableName = "user")
data class User(
    @PrimaryKey
    var userId: String = "",
    var name: String = "",
    var mobile: String = "",
    var email: String = "",
    var password: String = "",
    var imageProfile: String = "",
    var likes: List<String> = ArrayList(),
    @TypeConverters(ObjectListTypeConvertor::class)
    var cart: List<CartItem> = ArrayList(),
    @TypeConverters(ObjectListTypeConvertor::class)
    var orders: List<OrderItem> = ArrayList(),
    var userType: String = UserType.CUSTOMER.name,
    var wallet: Wallet = Wallet(),
    var country: String = "",
    @field:JvmField // for correct setters names and JSON will be parsed correctly.
    var isPublic: Boolean? = null,
    @field:JvmField
    var isActive: Boolean? = null
): Parcelable{

    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "userId" to userId,
            "name" to name,
            "email" to email,
            "mobile" to mobile,
            "password" to password,
            "imageProfile" to imageProfile,
            "likes" to likes,
            "cart" to cart,
            "orders" to orders,
            "userType" to userType,
            "wallet" to wallet,
            "country" to country,
            "isPublic" to isPublic!!,
            "isActive" to isActive!!
        )
    }

//    val hashMap = hashMapOf<String, Any>()
//    hashMap["userId"] = userId
//    hashMap["name"] = name
//    hashMap["email"] = email
//    hashMap["mobile"] = mobile
//    hashMap["password"] = password
//    hashMap["imageProfile"] = imageProfile
//    hashMap["likes"] = likes
//    hashMap["cart"] = cart
//    hashMap["orders"] = orders
//    hashMap["userType"] = userType
//    hashMap["wallet"] = wallet
//    hashMap["country"] = country
//    if (isPublic != null)
//    hashMap["isPublic"] = isPublic!!
//    if(isActive != null)
//    hashMap["isActive"] = isActive!!
//    return hashMap


    @Parcelize
    data class Wallet(
        var balance: Int? = 0,
        var promotional: Int? = 0,
    ): Parcelable {
        fun toHashMap(): HashMap<String, Any> {
            return hashMapOf(
              "balance" to balance!!,
                "promotional" to promotional!!
            )
        }

    }




    @Parcelize
    data class OrderItem (
        var orderId: String = "",
        var customerId: String = "",
        var items: List<CartItem> = ArrayList(),
        var itemsPrices: Map<String, Double> = mapOf(),
        var shippingCharges: Double = 0.0,
        var paymentMethod: String = "",
        var address: Address = Address(),
        var orderDate: Date = Date(),
        var status: String = OrderStatus.BINDING.name
    ) : Parcelable {
        fun toHashMap(): HashMap<String, Any> {
            return hashMapOf(
                "orderId" to orderId,
                "customerId" to customerId,
                "items" to items.map { it.toHashMap() },
                "itemsPrices" to itemsPrices,
                "shippingCharges" to shippingCharges,
                "paymentMethod" to paymentMethod,
                "orderDate" to orderDate,
                "status" to status
            )
        }
    }


    @Parcelize
    data class CartItem (
        var itemId: String = "",
        var productId: String = "",
        var ownerId: String = "",
        var quantity: Int = 0,
        var color: String? = null,
        var size: Int? = null,
        var country: String
    ) : Parcelable {
        constructor() : this("", "", "", 0, "NA", -1,"")

        fun toHashMap(): HashMap<String, Any> {
            val hashMap = hashMapOf<String, Any>()
            hashMap["itemId"] = itemId
            hashMap["productId"] = productId
            hashMap["ownerId"] = ownerId
            hashMap["quantity"] = quantity
            hashMap["country"] = country
            if (color != null)
                hashMap["color"] = color!!
            if (size != null)
                hashMap["size"] = size!!
            return hashMap
        }
    }




    @Parcelize
    data class Address (
        var addressId: String = "",
        var name: String = "",
        var streetAddress: String = "",
        var city: String = "",
        var location: Location = Location(),
        var phoneNumber: String = ""
    ) : Parcelable {
        fun toHashMap(): HashMap<String, String> {
            return hashMapOf(
                "addressId" to addressId,
                "name" to name,
                "streetAddress" to streetAddress,
                "city" to city,
                "phoneNumber" to phoneNumber
            )
        }
    }


}