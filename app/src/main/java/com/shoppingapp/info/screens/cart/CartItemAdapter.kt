package com.shoppingapp.info.screens.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shoppingapp.info.R
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.UserData
import com.shoppingapp.info.databinding.CartItemBinding
import com.shoppingapp.info.databinding.CircularLoaderLayoutBinding


class CartItemAdapter(
	private val context: Context, items: List<UserData.CartItem>,
	products: List<Product>, userLikes: List<String>
) : RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {

	lateinit var onClickListener: OnClickListener
	var data: List<UserData.CartItem> = items
	var proList: List<Product> = products
	var likesList: List<String> = userLikes

	inner class ViewHolder(private val binding: CartItemBinding) :
		RecyclerView.ViewHolder(binding.root) {
		fun bind(itemData: UserData.CartItem) {
			binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
			val proData = proList.find { it.productId == itemData.productId } ?: Product()
			binding.cartProductTitle.text = proData.name
			binding.cartProductPrice.text =
				context.getString(R.string.price_text, proData.price.toString())
			if (proData.images.isNotEmpty()) {
				val imgUrl = proData.images[0].toUri().buildUpon().scheme("https").build()
				Glide.with(context)
					.asBitmap()
					.load(imgUrl)
					.into(binding.productImageView)
				binding.productImageView.clipToOutline = true
			}
			binding.cartProductQuantity.text = itemData.quantity.toString()

			if (likesList.contains(proData.productId)) {
				binding.cartProductLikeBtn.setImageResource(R.drawable.liked_heart_drawable)
			} else {
				binding.cartProductLikeBtn.setImageResource(R.drawable.heart_icon_drawable)
			}

			binding.cartProductLikeBtn.setOnClickListener {
				binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
				if (!likesList.contains(proData.productId)) {
					binding.cartProductLikeBtn.setImageResource(R.drawable.liked_heart_drawable)
				} else {
					binding.cartProductLikeBtn.setImageResource(R.drawable.heart_icon_drawable)
				}
				onClickListener.onLikeClick(proData.productId)
//				onClickListener.onLikeClick(proData.productId)
			}

			binding.cartProductDeleteBtn.setOnClickListener {
				binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
				onClickListener.onDeleteClick(itemData.itemId, binding.loaderLayout)
			}
			binding.btnCartProductPlus.setOnClickListener {
				binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
				onClickListener.onPlusClick(itemData.itemId)
			}
			binding.btnCartProductMinus.setOnClickListener {
				binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
				onClickListener.onMinusClick(itemData.itemId, itemData.quantity, binding.loaderLayout)
			}

		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(
			CartItemBinding.inflate(
				LayoutInflater.from(parent.context), parent, false
			)
		)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(data[position])
	}

	override fun getItemCount() = data.size

	interface OnClickListener {
		fun onLikeClick(productId: String)
		fun onDeleteClick(itemId: String, itemBinding: CircularLoaderLayoutBinding)
		fun onPlusClick(itemId: String)
		fun onMinusClick(itemId: String, currQuantity: Int, itemBinding: CircularLoaderLayoutBinding)
	}

}