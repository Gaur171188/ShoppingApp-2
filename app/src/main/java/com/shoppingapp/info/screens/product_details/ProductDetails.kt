package com.shoppingapp.info.screens.product_details

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.PagerSnapHelper
import com.shoppingapp.info.R
import com.shoppingapp.info.ShoeColors
import com.shoppingapp.info.ShoeSizes
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.ProductDetailsBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.properties.Delegates


class ProductDetails: Fragment() {

    companion object{
        const val TAG = "ProductDetails"
    }


    private val homeViewModel by sharedViewModel<HomeViewModel>()
    private lateinit var viewModel: ProductDetailsViewModel

//    private val viewModel by sharedViewModel<ProductDetailsViewModel> {
//        parametersOf(arguments?.getString("productId") as String) // the argument will be fixed in parameter of view model
//    }

//    private val ordersViewModel by sharedViewModel<OrdersViewModel>()



    private var productId: String = ""

    private lateinit var binding: ProductDetailsBinding


    private lateinit var product: Product
    private var selectedSize: Int? = null
    private var selectedColor: String? = null



    private var userId = ""
    private var isUserSeller = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.product_details, container, false)
        viewModel = ViewModelProvider(this)[ProductDetailsViewModel::class.java]

        val sharePrefManager = SharePrefManager(requireContext())
        userId = sharePrefManager.getUserIdFromSession()!!
        isUserSeller = sharePrefManager.isUserSeller()



        product = try { arguments?.getParcelable<Product>(Constants.KEY_PRODUCT)!! }
        catch (ex: Exception){ Product() }



        setViews()

        setObservers()


        return binding.root
    }


    override fun onStart() {
        super.onStart()



        // refresh product data.
        val user = homeViewModel.userData.value ?: User()


        viewModel.loadData(user,product)


        selectedSize = null
        selectedColor = null

    }



    private fun setObservers() {

        /** live data data status **/
        viewModel.loadStatus.observe(viewLifecycleOwner) { dataStatus ->
            when (dataStatus) {
                DataStatus.LOADING -> {
                    binding.proDetailsLayout.show()
                }
                DataStatus.SUCCESS -> {
                    binding.proDetailsLayout.hide()
                }
                DataStatus.ERROR ->{
                    binding.proDetailsLayout.hide()
                }

            }
        }


        /** quantity **/
        viewModel.quantity.observe(viewLifecycleOwner) { quantity ->
            if (quantity != null){
                binding.cartProductQuantity.text = quantity.toString()
            }
        }


        /** live data item in cart **/
        viewModel.isItemInCart.observe(viewLifecycleOwner) {
            if (it != null){
                if (it == true) {
                    binding.btnAddToCart.text = getString(R.string.pro_details_go_to_cart_btn_text)
                } else {
                    binding.btnAddToCart.text = getString(R.string.pro_details_add_to_cart_btn_text)
                }
            }
        }





    }




    private fun setViews() {
        binding.apply {

            val isLiked = homeViewModel.likedProducts.value?.contains(product)!!

            /** button add in cart **/
            btnAddToCart.setOnClickListener {
                addToCart()
            }

            /** button back **/
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            /** button plus quantity **/
            btnAddCartItem.setOnClickListener {
                val quantity = viewModel.quantity.value!!
                if (quantity >= 1){
                    viewModel.setQuantityOfItem(productId,userId, +1)
                }
            }

            /** button minus quantity **/
            btnRemoveCartItem.setOnClickListener {
                val quantity = viewModel.quantity.value!!
                if (quantity >= 2){
                    viewModel.setQuantityOfItem(productId,userId,-1)
                }
            }

            /** button like product **/
            btnLikeProduct.setOnClickListener {
                if (!isLiked){ // add like
                    homeViewModel.insertLikeByProductId(product, userId)
                }else{ // remove like
                    homeViewModel.removeLikeByProductId(product, userId)
                }
            }


            /*
            there is a little delay until update liked data in home view model
             */
            binding.btnLikeProduct.isChecked = isLiked


            // set images and dots
            setImagesView()

            /** title, description and price  **/
            productTitle.text = product.name
            productDescription.text = product.description
            productPrice.text = resources.getString(R.string.pro_details_price_value, product.price.toString())

            setShoeSizeButtons()
            setShoeColorsButtons()


        }



    }


    private fun addToCart() {
        if (viewModel.isItemInCart.value == true) {
            findNavController().navigate(R.id.action_productDetails_to_cart)
        } else {
            if (selectedSize == null){
                showMessage(requireContext(),"Size required")
            }
            if (selectedColor == null){
                showMessage(requireContext(), "Color required")
            }else{
                viewModel.addToCart(product,selectedSize, selectedColor,userId)
            }
        }
    }


    private fun setImagesView() {
        if (context != null) {
            binding.proDetailsImagesRecyclerview.isNestedScrollingEnabled = false
            val adapter = ProductImagesAdapter(requireContext(), product.images ?: emptyList())
            binding.proDetailsImagesRecyclerview.adapter = adapter
            val rad = resources.getDimension(R.dimen.radius)
            val dotsHeight = resources.getDimensionPixelSize(R.dimen.dots_height)
            val inactiveColor = ContextCompat.getColor(requireContext(), R.color.gray)
            val activeColor = ContextCompat.getColor(requireContext(), R.color.blue_accent_300)
            val itemDecoration = DotsIndicatorDecoration(rad, rad * 4, dotsHeight, inactiveColor, activeColor)
            binding.proDetailsImagesRecyclerview.addItemDecoration(itemDecoration)
            PagerSnapHelper().attachToRecyclerView(binding.proDetailsImagesRecyclerview)
        }
    }

    private fun setShoeSizeButtons() {
        binding.proDetailsSizesRadioGroup.apply {
            for ((_, v) in ShoeSizes) {
                if (product.availableSizes.contains(v)) {
                    val radioButton = RadioButton(context)
                    radioButton.id = v
                    radioButton.tag = v
                    val param = binding.proDetailsSizesRadioGroup.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(resources.getDimensionPixelSize(R.dimen.radio_margin_size))
                    param.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    param.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    radioButton.layoutParams = param
                    radioButton.background = ContextCompat.getDrawable(context, R.drawable.radio_selector)
                    radioButton.setButtonDrawable(R.color.transparent)
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                    radioButton.setTextColor(Color.BLACK)
                    radioButton.setTypeface(null, Typeface.BOLD)
                    radioButton.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    radioButton.text = "$v"
                    radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                        val tag = buttonView.tag.toString().toInt()
                        if (isChecked) {
                            selectedSize = tag
                        }
                    }
                    addView(radioButton)
                }
            }
            invalidate()
        }
    }

    private fun setShoeColorsButtons() {
        binding.proDetailsColorsRadioGroup.apply {
            var ind = 1
            for ((k, v) in ShoeColors) {
                if (product.availableColors.contains(k) == true) {
                    val radioButton = RadioButton(context)
                    radioButton.id = ind
                    radioButton.tag = k
                    val param =
                        binding.proDetailsColorsRadioGroup.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(resources.getDimensionPixelSize(R.dimen.radio_margin_size))
                    param.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    param.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    radioButton.layoutParams = param
                    radioButton.background =
                        ContextCompat.getDrawable(context, R.drawable.color_radio_selector)
                    radioButton.setButtonDrawable(R.color.transparent)
                    radioButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor(v))
                    if (k == "white") {
                        radioButton.backgroundTintMode = PorterDuff.Mode.MULTIPLY
                    } else {
                        radioButton.backgroundTintMode = PorterDuff.Mode.ADD
                    }
                    radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                        val tag = buttonView.tag.toString()
                        if (isChecked) {
                            selectedColor = tag
                        }
                    }
                    addView(radioButton)
                    ind++
                }
            }
            invalidate()
        }
    }
}
