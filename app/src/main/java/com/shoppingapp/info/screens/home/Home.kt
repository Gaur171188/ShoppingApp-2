package com.shoppingapp.info.screens.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shoppingapp.info.R
import com.shoppingapp.info.activities.RegistrationActivity
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.databinding.HomeBinding
import com.shoppingapp.info.screens.profile.ProfileViewModel
import com.shoppingapp.info.utils.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

@RequiresApi(Build.VERSION_CODES.Q)
class Home : Fragment() {

    companion object{
        const val TAG = "Home"
    }

    private lateinit var binding: HomeBinding
    private val viewModel by sharedViewModel<HomeViewModel>()
    private val profileViewModel by sharedViewModel<ProfileViewModel>()

//    private lateinit var favoritesViewModel: FavoritesViewModel

    private val focusChangeListener = MyOnFocusChangeListener()

    private val productController by lazy { ProductController() }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.home,container,false)


        initData()

        setViews()
        setObservers()

        return binding.root
    }



    private fun initData() {
//        val userType = SharePrefManager(requireContext()).getUserType().toString()
//        val isUserSeller = SharePrefManager(requireContext()).isUserSeller()
        val user = SharePrefManager(requireContext()).loadUser()


        viewModel.setData(user)
//        showMessage(requireContext(), user.userType)
    }


    override fun onResume() {
        super.onResume()

//        showMessage(requireContext(), viewModel.userType.value!!)

        viewModel.loadData()

    }



    private fun setViews() {
        binding.apply {

            /** swipe to refresh **/
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.loadData()
            }

            setHomeTopAppBar()
            setProductsAdapter()

//
//            /** button add product **/
//            btnAddProduct.setOnClickListener {
//                showDialogWithItems(ProductCategories, 0, false)
//            }




        }

    }



    private fun productFilterSheet() {
        binding.apply {
            filterSheet.apply {
                specificFiltersUser.root.hide()
                setRate()
                val bottom = BottomSheetBehavior.from(filterSheet)
                bottom.state = BottomSheetBehavior.STATE_EXPANDED
                btnAddProduct.hide()
                specificFiltersUser.apply {

                    /** button close sheet **/
                    btnClose.setOnClickListener {
                        bottom.state = BottomSheetBehavior.STATE_COLLAPSED
                        btnAddProduct.show()
                    }

                    /** button apply **/
                    btnApply.setOnClickListener {
                        val city = selectCity.text.toString()
                        val country = selectCountry.selectedCountryName.toString()
                        val rate = selectRate.text.toString()

                        val minPrice = specificFiltersProduct.minPrice.text.trim().toString()
                        val maxPrice = specificFiltersProduct.maxPrice.text.trim().toString()

                        val filter = viewModel.filter(city, country, rate,minPrice,maxPrice)
                        productController.setData(filter)

                        bottom.state = BottomSheetBehavior.STATE_COLLAPSED
                        btnAddProduct.show()
                    }

                }
            }
        }
    }


    private fun setUserViews() {
        binding.apply {

            when(SharePrefManager(requireContext()).loadUser().userType) {
                UserType.CUSTOMER.name -> { /** customer **/
                    homeTopAppBar.topAppBar.menu.removeItem(R.id.item_options)
                    btnAddProduct.hide()

                }
                UserType.SELLER.name -> { /** seller **/
                    homeTopAppBar.topAppBar.menu.removeItem(R.id.item_favorites)
                    homeTopAppBar.topAppBar.menu.removeItem(R.id.item_cart)
                    homeTopAppBar.topAppBar.menu.removeItem(R.id.item_options)
                    btnAddProduct.show()

                }
                UserType.ADMIN.name ->{ /** admin **/
                    homeTopAppBar.topAppBar.menu.removeItem(R.id.item_favorites)
                    homeTopAppBar.topAppBar.menu.removeItem(R.id.item_cart)
                    btnAddProduct.hide()

                }
            }

        }
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
                if (status != null){
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
            }


            /** add like status **/
                viewModel.addLikeStatus.observe(viewLifecycleOwner) { status ->
                if (status != null){
                    when(status){
                        DataStatus.SUCCESS -> { viewModel.resetProgress() }
                        DataStatus.LOADING -> {}
                        DataStatus.ERROR -> {}
                    }
                }
            }



            /** is sign out **/
            profileViewModel.isSignOut.observe(viewLifecycleOwner){isSignOut->
                if (isSignOut == true){
                    val intent = Intent(requireContext(), RegistrationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }



        }



    }


//    private fun setPrices() {
//        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1, prices)
//        binding.filterSheet.specificFiltersProduct.selectPrice.setAdapter(adapter)
//    }

        private fun setRate() {
            val rateValues = arrayOf("1","2","3","4","5")
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1, rateValues)
        binding.filterSheet.selectRate.setAdapter(adapter)
    }



    private fun setAppBarItemClicks(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.item_filter -> {
                productFilterSheet()
//                val extraFilters = arrayOf("All", "None")

//                // todo replace product categories with categories filter data.
//                val categoryList = ProductCategories.plus(extraFilters)
//                val checkedItem = categoryList.indexOf(viewModel.filterCategory.value)
//                showDialogWithItems(categoryList, checkedItem, true)
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
            R.id.item_options-> {
                val view = requireView().findViewById<View>(R.id.item_options)
                showAdminOptionMenu(view)
                true
            }
            else -> false
        }
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setHomeTopAppBar() {
        binding.apply {

            /** set app bar menu **/
            homeTopAppBar.topAppBar.inflateMenu(R.menu.home_app_bar_menu)

            setUserViews()


            /** set hint **/
            homeTopAppBar.homeSearchEditText.hint = resources.getString(R.string.search_product)

            /** button search **/
            homeTopAppBar.homeSearchEditText.setOnEditorActionListener { textView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = textView.text.trim().toString()
                    productController.setData(viewModel.searchByProductName(query))
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

    // the icons in menu does not appear in android version below 10
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showAdminOptionMenu(v: View) {
        val popupMenu = PopupMenu(requireContext(),v)
        popupMenu.menuInflater.inflate(R.menu.admin_options_menu,popupMenu.menu)
        popupMenu.setForceShowIcon(true)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_notification -> {}

                R.id.item_productManager -> {}

                R.id.item_signOut -> {
                    profileViewModel.signOut()
                }

            }
            true
        }
        popupMenu.show()
    }

    private fun setProductsAdapter() {
        val likesList = viewModel.userLikes.value ?: emptyList()
        val isSeller = viewModel.userData.value?.userType == UserType.SELLER.name
        val userType = viewModel.userData.value?.userType!!
        productController.userType = userType
        productController.likes = likesList
        productController.setData(emptyList())


        /** click listener **/
        productController.clickListener = object: ProductController.OnClickListener {

            override fun onAdClicked() {}

            override fun onProductClick(product: Product) {
                if (viewModel.userData.value?.userType == UserType.CUSTOMER.name) {
                    val isProductInCart = viewModel.cartItems.value?.map{ it.productId }?.contains(product.productId)
                    val isLiked = viewModel.likedProducts.value?.contains(product)!!
                    val data =  bundleOf(
                        Constants.KEY_PRODUCT to product,
                        Constants.KEY_IS_EDIT to false,
                        Constants.KEY_CHECK_IN_CART to isProductInCart,
                        Constants.KEY_IS_LIKED to isLiked)
                    findNavController().navigate(R.id.action_home_to_productDetails,data)
                }else{
                    val data = bundleOf(
                        Constants.KEY_IS_EDIT to true,
                        Constants.KEY_PRODUCT to product )
                    findNavController().navigate(R.id.action_goto_addProduct, data)
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



    private fun showDialogWithItems(categoryItems: Array<String>, checkedOption: Int = 0, ) {
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
                        val selectedItem = categoryItems[checkedItem]
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

