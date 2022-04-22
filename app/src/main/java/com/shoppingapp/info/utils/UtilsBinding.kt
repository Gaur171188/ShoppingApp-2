package com.shoppingapp.info.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.shoppingapp.info.R
import com.shoppingapp.info.utils.x.context

@SuppressLint("StaticFieldLeak")
object x{
   lateinit var context: Context
}


@BindingAdapter("setImage")
fun setImage(image: ImageView, images: List<String>?){
    try {
        val imgUrl = images?.get(0)?.toUri()?.buildUpon()?.scheme("https")?.build()
        Glide.with(context)
            .asBitmap()
            .load(imgUrl)
            .into(image)
    }catch (ex: Exception){

    }

//    image.clipToOutline = true
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
fun setProductLike(image: ImageView , isLike: Boolean){
    if (isLike) {
       image.setImageResource(R.drawable.liked_heart_drawable)
    } else {
        image.setImageResource(R.drawable.heart_icon_drawable)
    }
}

@BindingAdapter("setQuantity")
fun setQuantity(tv: TextView, quantity: Int){
    tv.text = "Quantity: $quantity"
}

@BindingAdapter("setPrice")
fun setPrice(tv: TextView, v: Double){
    tv.text = "$v"
}


@BindingAdapter("setItemsCount")
fun setItemsCount(tv: TextView,v: Int){
    tv.text = "Items($v)"
}