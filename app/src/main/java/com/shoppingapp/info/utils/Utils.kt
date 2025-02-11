package com.shoppingapp.info.utils

import android.app.Activity
import android.content.Context
import android.text.format.DateUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.room.TypeConverter
import com.shoppingapp.info.data.User
import java.util.*
import java.util.regex.Pattern
import kotlin.math.roundToInt


const val ERR_UPLOAD = "UploadErrorException"

enum class DataStatus { LOADING, ERROR, SUCCESS }
enum class UserType { CUSTOMER, SELLER, ADMIN}
enum class OrderStatus { CONFIRMED, BINDING, ARRIVING, DELIVERED, REJECTED }
enum class Sort { NAME,DATE }
enum class Days { YESTERDAY, TODAY, MONTH_AGO }


object CheckTime {
    fun isYesterday(d: Date) = DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
    fun isTomorrow(d: Date) = DateUtils.isToday(d.time - DateUtils.DAY_IN_MILLIS)
}


fun getItemsPriceTotal(price: Map<String, Double>,items: List<User.CartItem>): Double {
    var totalPrice = 0.0
    price.forEach { (itemId, price) ->
        totalPrice += price * (items.find { it.itemId == itemId }?.quantity ?: 1)
    }
    return totalPrice
}


class MyOnFocusChangeListener : View.OnFocusChangeListener {
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v != null) {
            val inputManager =
                v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (!hasFocus) {

                inputManager.hideSoftInputFromWindow(v.windowToken, 0)
            } else {
                inputManager.toggleSoftInputFromWindow(v.windowToken, 0, 0)

            }
        }
    }
}


internal fun isEmailValid(email: String): Boolean {
    val EMAIL_PATTERN = Pattern.compile(
        "\\s*[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+\\s*"
    )
    return if (email.isEmpty()) {
        false
    } else {
        EMAIL_PATTERN.matcher(email).matches()
    }
}

internal fun getProductId(ownerId: String): String {
    val uniqueId = UUID.randomUUID().toString()
    return "$uniqueId-$ownerId"
//    return "pro-$proCategory-$ownerId-$uniqueId"
}

internal fun getOrderId(): String {
    val uniqueId = UUID.randomUUID().toString()
    return "$uniqueId"
}

internal fun getAddressId(): String {
    val uniqueId = UUID.randomUUID().toString()
    return "$uniqueId"
}



fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


internal fun getOfferPercentage(costPrice: Double, sellingPrice: Double): Int {
    if (costPrice == 0.0 || sellingPrice == 0.0 || costPrice <= sellingPrice)
        return 0
    val off = ((costPrice - sellingPrice) * 100) / costPrice
    return off.roundToInt()
}

internal fun getRandomString(length: Int, uNum: String, endLength: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    fun getStr(l: Int): String = (1..l).map { allowedChars.random() }.joinToString("")
    return getStr(length) + uNum + getStr(endLength)
}


//@SuppressLint("CommitPrefEdits")
//class SharePref(context: Context,fileName: String){
//    companion object{
//        private const val KEY_REMEMBER_ME="RememberMe"
//        const val FILE_USER = "user_file"
//    }
//
//    private val pref = context.getSharedPreferences(fileName,Context.MODE_PRIVATE)
//    private val edit = pref.edit()
//
//    fun setRememberMe(value: Boolean){
//        edit.putBoolean(KEY_REMEMBER_ME,value)
//        edit.apply()
//    }
//
//    fun getRememberMe() = pref.getBoolean(KEY_REMEMBER_ME,false)
//
//
//}






class ListTypeConverter {
    @TypeConverter
    fun fromStringToStringList(value: String): List<String> {
        return value.split(",").map { it }
    }

    @TypeConverter
    fun fromStringListToString(value: List<String>): String {
        return value.joinToString(separator = ",")
    }

    @TypeConverter
    fun fromStringToIntegerList(value: String): List<Int> {
        return value.split(",").map { it.toInt() }
    }

    @TypeConverter
    fun fromIntegerListToString(value: List<Int>): String {
        return value.joinToString(separator = ",")
    }
}
