package com.shoppingapp.info.screens.cart


import android.util.Log
import com.airbnb.epoxy.TypedEpoxyController
import com.shoppingapp.info.cart
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User.CartItem


class CartController(): TypedEpoxyController<List<CartItem>>() {

    lateinit var clickListener: OnClickListener

    lateinit var likes: List<String>
    lateinit var products: List<Product>



    companion object{
        private const val TAG = "CartControllerss"
    }




    override fun buildModels(data: List<CartItem>?) {
//        Log.d(TAG, "cartItems: ${data?.size}")
        data?.forEachIndexed { index, cart->

//            Log.d(TAG,"likes: ${likes.size}")
//            Log.d(TAG,"products: ${products.size}")

            val product = products.find { it.productId == cart.productId } ?: Product()
            val isLiked = likes.contains(product.productId)


                cart {
                    id(cart.itemId)
                    cartData(cart)
                    isLike(isLiked)
                    productData(product)
                    onDeleteClick { _ -> clickListener.onDeleteClick(cart,index) }
                    onItemClick { _ -> clickListener.onItemClick(cart,index) }
                }




        }
    }

    interface OnClickListener {
        fun onDeleteClick(cartItem: CartItem, position: Int)
        fun onItemClick(cartItem: CartItem, position: Int)
    }


//    class CartClickListener(val clickListener: (cart: CartItem) -> Unit) {
//        fun onClick(cart: CartItem) = clickListener(cart)
//    }

}
