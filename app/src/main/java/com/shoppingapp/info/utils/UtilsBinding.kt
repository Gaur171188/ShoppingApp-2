package com.shoppingapp.info.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.shoppingapp.info.R
import com.shoppingapp.info.utils.x.context
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StaticFieldLeak")
object x{
   lateinit var context: Context
}


@BindingAdapter("setImage")
fun setImage(image: ImageView, images: List<String>?) {
    try {
        val imgUrl = images?.get(0)?.toUri()?.buildUpon()?.scheme("https")?.build()
        Glide.with(image.context)
            .asBitmap()
            .load(imgUrl)
            .into(image)
    }catch (ex: Exception){ }
}

@BindingAdapter("setImageProfile")
fun imageProfile(image: ImageView, imageUri: String?) {
    try {
        if (imageUri != null){
            Glide.with(image.context)
                .load(imageUri.toUri())
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(image)
        }

    }catch (ex: Exception){}
}


@BindingAdapter("setAppBarItems")
fun setAppBarItems(topAppBar: MaterialToolbar, isSeller: Boolean){
    if (isSeller){
        topAppBar.menu.removeItem(R.id.item_favorites)
        topAppBar.menu.removeItem(R.id.item_cart)
    }
}




//@BindingAdapter("isSeller")
//fun isSeller(v: View , isSeller: Boolean){
//    if (isSeller){
//        checkBox.visibility = View.GONE
//    }else{
//        checkBox.visibility = View.VISIBLE
//    }
//}

@BindingAdapter("setLikeButtonStatus")
fun setLikeButtonStatus(v: View, isSeller: Boolean){
    if (isSeller){
        v.hide()
    }else{
        v.show()
    }
}


@BindingAdapter("setAddProductButtonStatus")
fun setAddProductButtonStatus(v: View, isSeller: Boolean){
    if (isSeller){
        v.show()
    }else{
        v.hide()
    }
}

@BindingAdapter("setTitle")
fun setProductTitle(tv: TextView, title: String){
    tv.text = title
}



@BindingAdapter("setProductPrice")
fun setProductPrice(tv: TextView, price: Double){
    tv.text = context.getString(R.string.price_text,price.toString())
}

@BindingAdapter("setLike")
fun setProductLike(checkBox: CheckBox , isLike: Boolean){
    checkBox.isChecked = isLike
}


@BindingAdapter("setAdImage")
fun setAdImage(image: ImageView,v: Int){
    Glide.with(context)
        .asBitmap()
        .load(v)
        .into(image)
}

@BindingAdapter("setQuantity")
fun setQuantity(tv: TextView, quantity: Int){
    tv.text = "Quantity: $quantity"
}

@BindingAdapter("setPrice")
fun setPrice(tv: TextView, v: Double) {
    val price = v.toInt()
    tv.text = "$price DLY"
}

@BindingAdapter("setOrderDate")
fun setOrderDate(tv: TextView, date: Date){
    tv.text = SimpleDateFormat("dd/MM/yyyy").format(date)
}


//@BindingAdapter(value = ["date","status"], requireAll = true)
//fun setOrderStatus(tv: TextView, date: Date){
//    tv.text = "Ordered on: " + SimpleDateFormat("dd/MM/yyyy").format(date)
//}

@BindingAdapter("setOrderStatus")
fun setOrderStatus(tv: TextView, value: String){
    tv.text = value
}


@BindingAdapter("setUserState")
fun setUserState(image: ImageView,b: Boolean){
    if (b){ // seller
        image.visibility = View.GONE
    }else{ // customer
        image.visibility = View.VISIBLE
    }
}

@BindingAdapter("setOrderItemsCount")
fun setOrderItemsCount(tv: TextView, v: Int){
    tv.text = "$v items purchased"
}


@BindingAdapter("setItemsCount")
fun setItemsCount(tv: TextView,v: Int){
    tv.text = "Items($v)"
}