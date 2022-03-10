package com.shoppingapp.info.screens.favorites

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shoppingapp.info.R
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.databinding.ProductItemBinding
import com.shoppingapp.info.utils.getOfferPercentage


class LikedProductAdapter(proList: List<Product>, private val context: Context) :
	RecyclerView.Adapter<LikedProductAdapter.ViewHolder>() {

	var data = proList

	lateinit var onClickListener: OnClickListener

	inner class ViewHolder(private val binding: ProductItemBinding) :
		RecyclerView.ViewHolder(binding.root) {
		@SuppressLint("NotifyDataSetChanged")
		fun bind(product: Product) {
			binding.productCard.setOnClickListener {
				onClickListener.onClick(product)
			}
			binding.productNameTv.text = product.name
			binding.productPriceTv.text = context.getString(R.string.pro_details_price_value, product.price.toString())
			binding.productRatingBar.rating = product.rating.toFloat()
			binding.productActualPriceTv.apply {
				paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
				text = context.getString(
					R.string.pro_details_actual_strike_value,
					product.mrp.toString()
				)
			}
			binding.productOfferValueTv.text = context.getString(
				R.string.pro_offer_precent_text,
				getOfferPercentage(product.mrp, product.price).toString()
			)
			if (product.images.isNotEmpty()) {
				val imgUrl = product.images[0].toUri().buildUpon().scheme("https").build()
				Glide.with(context)
					.asBitmap()
					.load(imgUrl)
					.into(binding.productImageView)
				binding.productImageView.clipToOutline = true
			}

			//hiding unnecessary button
			binding.productAddToCartButton.visibility = View.GONE
			binding.productDeleteButton.visibility = View.GONE
			binding.productLikeCheckbox.visibility = View.GONE

			// setting edit button as delete button
			binding.btnProductEdit.setImageResource(R.drawable.ic_delete_24)
			binding.btnProductEdit.setOnClickListener {
				onClickListener.onDeleteClick(product)

//				notifyItemRemoved(adapterPosition)
//				notifyDataSetChanged()

			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(
			ProductItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(data[position])
	}

	override fun getItemCount() = data.size

	interface OnClickListener {
		fun onClick(product: Product)
		fun onDeleteClick(product: Product)
	}
}