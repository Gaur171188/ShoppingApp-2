package com.shoppingapp.info.screens.home

import com.airbnb.epoxy.TypedEpoxyController
import com.shoppingapp.info.data.Ad
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.homeAd
import com.shoppingapp.info.productHome
import com.shoppingapp.info.utils.UserType
import kotlin.properties.Delegates


// the data it will be (ads  or products)
class ProductController(): TypedEpoxyController<List<Any>>() {

    lateinit var clickListener: OnClickListener
    lateinit var likes: List<String>
    var userType = ""


    override fun buildModels(list: List<Any>?) {

        list?.forEachIndexed { index , data ->

            when(data){


                is Product -> {
                    val isLiked = likes.contains(data.productId)
                    val isCustomer = userType == UserType.CUSTOMER.name
                    productHome {
                        id(data.productId)
                        productData(data)
                        isLiked(isLiked)
//                        isSeller(isSeller)
                        isCustomer(isCustomer)
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