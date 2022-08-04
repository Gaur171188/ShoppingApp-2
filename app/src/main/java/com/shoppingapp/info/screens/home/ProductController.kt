package com.shoppingapp.info.screens.home

import com.airbnb.epoxy.TypedEpoxyController
import com.shoppingapp.info.data.Ad
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.homeAd
import com.shoppingapp.info.productHome
import kotlin.properties.Delegates


// the data it will be (ads  or products)
class ProductController(): TypedEpoxyController<List<Any>>() {

    lateinit var clickListener: OnClickListener
    lateinit var likes: List<String>

//    var isSeller by Delegates.notNull<Boolean>()
var isSeller = false


    override fun buildModels(list: List<Any>?) {

        list?.forEachIndexed { index , data ->

            when(data){


                is Product -> {
                    val isLiked = likes.contains(data.productId)

                    productHome {
                        id(data.productId)
                        productData(data)
                        isLiked(isLiked)
                        isSeller(isSeller)
                        onItemClick { v->
                            clickListener.onProductClick(data)
                        }
                        onItemLiked { v->
                            clickListener.onLikeClick(data)
                        }
                    }

                }

                is Ad -> {
                    val image = data.image
                    homeAd {
                        id(image)
                        image(image)
                        onItemClick { v->
                            clickListener.onAdClicked()
                        }
                    }
                }


            }

        }


    }



    interface OnClickListener {
        fun onAdClicked()
        fun onProductClick(product: Product)
        fun onLikeClick(product: Product)
    }


}