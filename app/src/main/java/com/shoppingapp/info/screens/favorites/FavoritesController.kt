package com.shoppingapp.info.screens.favorites

import com.airbnb.epoxy.TypedEpoxyController

import com.shoppingapp.info.data.Product
import com.shoppingapp.info.productFavorites


// the data it will be (ads  or products)
class FavoritesController(): TypedEpoxyController<List<Product>>() {

    lateinit var clickListener: OnClickListener

    override fun buildModels(list: List<Product>?) {

        list?.forEachIndexed { index , data ->


            productFavorites {
                id(data.productId)
                productData(data)
                onItemClick { v->
                    clickListener.onProductClick(data)
                }
                onItemLiked { v->
                    clickListener.onLikeClick(data)
                }
            }


        }


    }



    interface OnClickListener {
        fun onProductClick(product: Product)
        fun onLikeClick(product: Product)
    }


}