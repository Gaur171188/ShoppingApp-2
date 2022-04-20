package com.shoppingapp.info.screens.cart


import android.content.Context
import android.util.Log
import com.airbnb.epoxy.TypedEpoxyController
import com.shoppingapp.info.cart
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User.CartItem
import com.shoppingapp.info.databinding.CircularLoaderLayoutBinding
import com.shoppingapp.info.repository.user.UserRepository
import com.shoppingapp.info.screens.home.HomeViewModel


class CartController(): TypedEpoxyController<List<CartItem>>() {

    lateinit var clickListener: OnClickListener
    lateinit var likes: List<String>
    lateinit var products: List<Product>




    companion object{
        private const val TAG = "CartControllerss"
    }





    override fun buildModels(data: List<CartItem>?) {
        Log.d(TAG, "cartItems: ${data?.size}")
        data?.forEachIndexed { index, cart ->

            Log.d(TAG,"likes: ${likes.size}")
            Log.d(TAG,"products: ${products.size}")

            val product = products.find { it.productId == cart.productId } ?: Product()
            val isLiked = likes.contains(product.productId)

            cart {
                id(cart.itemId)
                cartData(cart)
                isLike(isLiked)
                productData(product)
                onDeleteClick { _ -> clickListener.onDeleteClick(cart,index) }
                onLikeClick { _ -> clickListener.onLikeClick(cart,index) }
                onItemMinusClick { _ -> clickListener.onMinusClick(cart,index) }
                onItemPlusClick { _ -> clickListener.onPlusClick(cart,index) }
            }


        }
    }

    interface OnClickListener {
        fun onLikeClick(cartItem: CartItem, position: Int)
        fun onDeleteClick(cartItem: CartItem, position: Int)
        fun onPlusClick(cartItem: CartItem, position: Int)
        fun onMinusClick(cartItem: CartItem, position: Int)
    }


//    class CartClickListener(val clickListener: (cart: CartItem) -> Unit) {
//        fun onClick(cart: CartItem) = clickListener(cart)
//    }

}
