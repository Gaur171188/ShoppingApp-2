package com.shoppingapp.info.screens.home

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shoppingapp.info.R
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.databinding.HomeAdLayoutBinding
import com.shoppingapp.info.databinding.ProductItemBinding
import com.shoppingapp.info.utils.SharePrefManager
import com.shoppingapp.info.utils.getOfferPercentage


class ProductAdapter(
    proList: List<Any>,
    userLikes: List<String>,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data = proList
    var likesList = userLikes


    lateinit var onClickListener: OnClickListener
    lateinit var bindImageButtons: BindImageButtons
    private val sessionManager = SharePrefManager(context)
    private val isUserSeller = sessionManager.isUserSeller()

    inner class ItemViewHolder(binding: ProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val proName = binding.productNameTv
        private val proPrice = binding.productPriceTv
        private val productCard = binding.productCard
        private val productImage = binding.productImageView
        private val proDeleteButton = binding.productDeleteButton
        private val proEditBtn = binding.btnProductEdit
        private val proMrp = binding.productActualPrice
        private val proOffer = binding.productOfferValue
        private val proRatingBar = binding.productRatingBar
        private val proLikeButton = binding.productLikeCheckbox
        private val proCartButton = binding.productAddToCartButton

        fun bind(productData: Product) {

            /** btn product card **/
            productCard.setOnClickListener {
                if (!isUserSeller){
                    onClickListener.onClick(productData,position)
                }
            }

            /** product name **/
            proName.text = productData.name

            /** price data **/
            proPrice.text = context.getString(R.string.pro_details_price_value, productData.price.toString())

            /** rating data **/
            proRatingBar.rating = productData.rating.toFloat()

            /** actual price **/
            proMrp.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            proMrp.text =
                context.getString(
                    R.string.pro_details_actual_strike_value,
                    productData.mrp.toString()
                )

            /** product offer **/
            proOffer.text = context.getString(
                R.string.pro_offer_precent_text,
                getOfferPercentage(productData.mrp, productData.price).toString()
            )

            /** button product liked **/
            proLikeButton.setOnClickListener {
                onClickListener.onLikeClick(productData.productId)
            }

            /** button add product to cart **/
            proCartButton.setOnClickListener {
                onClickListener.onAddToCartClick(productData,position)
            }

            /** button edit product **/
            proEditBtn.setOnClickListener {
                onClickListener.onEditClick(productData.productId)
            }

            /** button delete product **/
            proDeleteButton.setOnClickListener {
                onClickListener.onDeleteClick(productData)
            }

            /** product image **/
            if (productData.images.isNotEmpty()) {
                val imgUrl = productData.images[0].toUri().buildUpon().scheme("https").build()
                Glide.with(context)
                    .asBitmap()
                    .load(imgUrl)
                    .into(productImage)

                productImage.clipToOutline = true
            }

            /** liked button **/
            proLikeButton.isChecked = likesList.contains(productData.productId)


            bindImageButtons.setLikeButton(productData.productId, proLikeButton)


            bindImageButtons.setCartButton(productData.productId, proCartButton)


            if (isUserSeller) {
                proLikeButton.visibility = View.GONE
                proCartButton.visibility = View.GONE

            } else {
                proEditBtn.visibility = View.GONE
                proDeleteButton.visibility = View.GONE



            }
        }
    }

    inner class AdViewHolder(binding: HomeAdLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val adImageView: ImageView = binding.adImageView
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_AD -> AdViewHolder(
                HomeAdLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> ItemViewHolder(
                ProductItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val proData = data[position]) {
            is Int -> (holder as AdViewHolder).adImageView.setImageResource(proData)
            is Product -> (holder as ItemViewHolder).bind(proData)
        }
    }

    override fun getItemCount(): Int = data.size

    companion object {
        const val VIEW_TYPE_PRODUCT = 1
        const val VIEW_TYPE_AD = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is Int -> VIEW_TYPE_AD
            is Product -> VIEW_TYPE_PRODUCT
            else -> VIEW_TYPE_PRODUCT
        }
    }

    interface BindImageButtons {
        fun setLikeButton(productId: String, button: CheckBox)
        fun setCartButton(productId: String, imgView: ImageView)
    }

    interface OnClickListener {
        fun onClick(productData: Product, position: Int)
        fun onDeleteClick(productData: Product)
        fun onEditClick(productId: String) {}
        fun onLikeClick(productId: String) {}
        fun onAddToCartClick(productData: Product, position: Int) {}
    }
}