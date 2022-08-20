package com.shoppingapp.info.data


/**
  this data class is used to manage your products from the admin portal
 **/
data class ProductOptions (
  val title: String = "",
  val isColorExist: Boolean? = null,
  val isSizeExist: Boolean? = null,
  val imageOption: ImageOption? = null,


){
  data class ImageOption (
    val isImageExist: Boolean,
    val maxImages: Int = 0,

  )
}
