package com.shoppingapp.info.screens.product_details


import android.widget.ImageView
import com.airbnb.epoxy.TypedEpoxyController
import com.shoppingapp.info.image


class ImageController(): TypedEpoxyController<List<String>>() {

    lateinit var clickListener: OnClickListener

    companion object {
        private const val TAG = "ImageController"
    }


    override fun buildModels(images: List<String>?) {

        images?.forEachIndexed { index, image->
            image {
                id(index)
                image(image)
                onImageClick { v->
                    clickListener.onItemClick(image)
                }

            }
        }
    }



    interface OnClickListener {
        fun onItemClick(image: String)
    }


}