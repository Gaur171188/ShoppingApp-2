package com.shoppingapp.info.screens.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shoppingapp.info.ProductCategories
import com.shoppingapp.info.R
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.databinding.HomeBinding
import com.shoppingapp.info.screens.favorites.FavoritesViewModel
import com.shoppingapp.info.utils.*
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


class Home : Fragment() {

    companion object{
        const val TAG = "Home"
    }

    private lateinit var binding: HomeBinding
    private val viewModel by sharedViewModel<HomeViewModel>()

//    private lateinit var favoritesViewModel: FavoritesViewModel

    private val focusChangeListener = MyOnFocusChangeListener()

    private val productController by lazy { ProductController() }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.home,container,false)
//        favoritesViewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]


        setViews()
        setObservers()

        return binding.root
    }



    override fun onResume() {
        super.onResume()

        viewModel.loadData()

    }



    private fun setViews() {

        /** swipe to refresh **/
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadData()
        }

        setUserViews()
        setHomeTopAppBar()
        setProductsAdapter()


        binding.btnAddProduct.setOnClickListener {
            showDialogWithItems(ProductCategories, 0, false)
        }

    }

    private fun setUserViews() {
        binding.homeTopAppBar.homeViewModel = viewModel
        binding.homeViewModel = viewModel
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setObservers() {

        binding.apply {


            /** products live data **/
            viewModel.products.observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()){
                    val mix = getMixedDataList(it, getAdsList())
                    val likes = viewModel.userLikes.value ?: emptyList()
                    productController.likes = likes
                    productController.setData(mix)
                    swipeRefreshLayout.isRefreshing = false
                }else{

                }

            }


            /** products status **/
            viewModel.productsStatus.observe(viewLifecycleOwner) { status ->
                when (status){
                    DataStatus.LOADING -> {
                        val isFirstLoad = viewModel.products.value.isNullOrEmpty()
                        if (isFirstLoad){ // show progress only for first time load
                            loaderLayout.root.show()
                        }
                    }
                    DataStatus.SUCCESS -> {loaderLayout.root.hide()}
                    DataStatus.ERROR -> {loaderLayout.root.hide()}
                }
            }


            /** add like status **/
                viewModel.addLikeStatus.observe(viewLifecycleOwner) { status ->
                if (status != null){
                    when(status){
                        DataStatus.SUCCESS -> { viewModel.resetProgress() }
                        DataStatus.LOADING -> {}
                        DataStatus.ERROR -> {}
                        else -> {}
                    }
                }
            }




        }



    }

    private fun performSearch(query: String) {
//        viewModel.filterBySearch(query)
    }


    private fun setAppBarItemClicks(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.item_filter -> {
                val extraFilters = arrayOf("All", "None")
                val categoryList = ProductCategories.plus(extraFilters)
                val checkedItem = categoryList.indexOf(viewModel.filterCategory.value)
                showDialogWithItems(categoryList, checkedItem, true)
                true
            }
            R.id.item_favorites -> {
                findNavController().navigate(R.id.action_homeFragment_to_favoritesFragment)
                true
            }
            R.id.item_cart -> {
                findNavController().navigate(R.id.action_home_to_cart)
                true
            }
            else -> false
        }
    }

    private fun setHomeTopAppBar() {
        binding.apply {

            /** set app bar menu **/
            homeTopAppBar.topAppBar.inflateMenu(R.menu.home_app_bar_menu)

            /** button search **/
            homeTopAppBar.homeSearchEditText.setOnEditorActionListener { text, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(text.text.toString())
                    homeTopAppBar.homeSearchEditText.setText("")
                    hideKeyboard()
                    true
                } else {
                    false
                }
            }

            /** button clear search edit text **/
            homeTopAppBar.searchOutlinedTextLayout.setEndIconOnClickListener {
                homeTopAppBar.homeSearchEditText.setText("")
                hideKeyboard()
            }

            /** app bar menu item **/
            homeTopAppBar.topAppBar.setOnMenuItemClickListener { menuItem ->
                setAppBarItemClicks(menuItem)
            }

        }
    }

    private fun setProductsAdapter() {
        val likesList = viewModel.userLikes.value ?: emptyList()
        val isSeller = viewModel.isUserSeller.value!!
        productController.isSeller = isSeller
        productController.likes = likesList
        productController.setData(emptyList())


        /** click listener **/
        productController.clickListener = object: ProductController.OnClickListener {

            override fun onAdClicked() {}

            override fun onProductClick(product: Product) {

                if (isSeller) { // go to add edit product
                    val data = bundleOf(Constants.KEY_IS_EDIT to true , Constants.KEY_PRODUCT to product )
                    findNavController().navigate(R.id.action_goto_addProduct, data)
                }else { // go to product details

                    val isProductInCart = viewModel.cartItems.value?.map{ it.productId }?.contains(product.productId)
                    val isLiked = viewModel.likedProducts.value?.contains(product)!!
                    val data =  bundleOf(
                        Constants.KEY_PRODUCT to product,
                        Constants.KEY_IS_EDIT to false,
                        Constants.KEY_CHECK_IN_CART to isProductInCart,
                        Constants.KEY_IS_LIKED to isLiked)
                    findNavController().navigate(R.id.action_home_to_productDetails,data)
                }

            }

            override fun onLikeClick(product: Product) {

                val isProductLiked = viewModel.likedProducts.value?.contains(product)!!
                if (!isProductLiked) { // insert like
                    viewModel.insertLikeByProductId(product)
                }else{ // remove like
                    viewModel.removeLikeByProductId(product)
                }

            }






        }

        binding.productsRecyclerView.apply {
            val gridLayoutManager = GridLayoutManager(context, 2)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {

                    return when (productController.currentData?.get(position)) {
                        2 -> 2 //ad
                        else -> {
                            val proCount = productController.currentData?.count { it is Product }// number of products
                            val adCount = productController.currentData?.size?.minus(proCount!!) // number of ads
                            val totalCount = proCount?.plus((adCount?.times(2)!!))
                            // product, full for last item
                            if (position + 1 == productController.currentData?.size && totalCount!! % 2 == 1) 2 else 1
                        }
                    }
                }
            }
            layoutManager = gridLayoutManager
            adapter = productController.adapter
            val itemDecoration = RecyclerViewPaddingItemDecoration(requireContext())
            if (itemDecorationCount == 0) {
                addItemDecoration(itemDecoration)
            }
        }

    }



    private fun showDialogWithItems(
        categoryItems: Array<String>,
        checkedOption: Int = 0,
        isFilter: Boolean
    ) {
        var checkedItem = checkedOption
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getString(R.string.pro_cat_dialog_title))
                .setSingleChoiceItems(categoryItems, checkedItem) { _, which ->
                    checkedItem = which
                }
                .setNegativeButton(getString(R.string.pro_cat_dialog_cancel_btn)) { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton(getString(R.string.pro_cat_dialog_ok_btn)) { dialog, _ ->
                    if (checkedItem == -1) {
                        dialog.cancel()
                    } else {
                        if (isFilter) {
//                            viewModel.filterProducts(categoryItems[checkedItem])
                        } else {
                            val data = bundleOf(Constants.KEY_IS_EDIT to false , Constants.KEY_CATEGORY to categoryItems[checkedItem] )
                            findNavController().navigate(R.id.action_goto_addProduct,data)

                        }
                    }
                    dialog.cancel()
                }
                .show()
        }
    }

//    private fun navigateToAddEditProductScreen(isEdit: Boolean, catName: String? = null) {
//        findNavController().navigate(
//            R.id.action_goto_addProduct,
//            bundleOf("isEdit" to isEdit, Constants.KEY_CATEGORY to catName)
//        )
//    }

    private fun getMixedDataList(data: List<Product>, adsList: List<Int>): List<Any> {
        val itemsList = mutableListOf<Any>()
        itemsList.addAll(data.sortedBy { it.productId })
        var currPos = 0
        if (itemsList.size >= 4) {
            adsList.forEach label@{ ad ->
                if (itemsList.size > currPos + 1) {
                    itemsList.add(currPos, ad)
                } else {
                    return@label
                }
                currPos += 5
            }
        }
        return itemsList
    }

    private fun getAdsList(): List<Int> {
        return listOf(R.drawable.ad_ex_2, R.drawable.ad_ex_1, R.drawable.ad_ex_3)
    }
}

