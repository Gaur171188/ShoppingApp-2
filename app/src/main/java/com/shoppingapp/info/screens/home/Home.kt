package com.shoppingapp.info.screens.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.shoppingapp.info.R
import com.shoppingapp.info.activities.RegistrationActivity
import com.shoppingapp.info.data.Ad
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.databinding.AdsBinding
import com.shoppingapp.info.databinding.HomeBinding
import com.shoppingapp.info.screens.add_edit_product.AddProductImagesAdapter
import com.shoppingapp.info.screens.profile.ProfileViewModel
import com.shoppingapp.info.screens.statistics.StatisticsViewModel
import com.shoppingapp.info.utils.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


@RequiresApi(Build.VERSION_CODES.Q)
class Home : Fragment() {

    companion object{ const val TAG = "Home" }

    private lateinit var binding: HomeBinding
    private val viewModel by sharedViewModel<HomeViewModel>()
    private val profileViewModel by sharedViewModel<ProfileViewModel>()
    private val statisticsViewModel by sharedViewModel<StatisticsViewModel>()


    private val productController by lazy { ProductController() }
    private var imagesList = mutableListOf<Uri>()

    private lateinit var bottomAdSheet: BottomSheetBehavior<FrameLayout>



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.home,container,false)

        bottomAdSheet = BottomSheetBehavior.from(binding.adManagerSheet.adManagerSheet)

        initData()
        setViews()
        setObservers()

        return binding.root
    }







    private fun initData() {
        val user = SharePrefManager(requireContext()).loadUser()
        viewModel.setData(user)
    }





    private fun setViews() {
        binding.apply {

            /** swipe to refresh **/
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.loadData()
                statisticsViewModel.loadAds()
            }


            setHomeTopAppBar()
            setProductsAdapter()


            /** button add product **/
            btnAddProduct.setOnClickListener {
                val data = bundleOf(Constants.KEY_IS_EDIT to false)
                findNavController().navigate(R.id.action_home_to_addEditProduct, data)
            }



        }

    }



    private fun adManagerSheet(isEdit: Boolean,oldAd: Ad?) {
        binding.apply {
            adManagerSheet.apply {
                imagesList.clear()

                bottomAdSheet.state = BottomSheetBehavior.STATE_EXPANDED

                manageAdAppBar.sheetLabel.text = resources.getString(R.string.ad_manager)

                val adapter = AddProductImagesAdapter(requireContext(), imagesList)
                addImages.rvImages.adapter = adapter

                if (isEdit){
                    addImages.addImagesLabel.text = "Update your Ad"
                    btnApply.text =  resources.getString(R.string.update)
                    imagesList.add(oldAd?.image!!.toUri())
                    adTitle.setText(oldAd.title)
                    btnDelete.show()

                }else{
                    btnDelete.hide()
                    addImages.addImagesLabel.text = "Select Image For You Ad"
                    adTitle.setText("")
                    btnApply.text =  resources.getString(R.string.apply)
                }

                /** button select images **/
                addImages.btnAddImage.setOnClickListener {
                    getImages.launch("image/*")
                }

                /** button close sheet **/
                manageAdAppBar.btnClose.setOnClickListener {
                    bottomAdSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                    hideKeyboard()
                }

                /** button remove ad **/
                btnDelete.setOnClickListener {
                    oldAd?.image = imagesList.first().toString()
                    statisticsViewModel.deleteAd(oldAd!!)
                }

                /** button apply **/
                btnApply.setOnClickListener {
                    if (imagesList.isNotEmpty()){
                        if (!isEdit){
                            val ad = Ad("",imagesList.first().toString(), title = adTitle.text?.trim().toString())
                            statisticsViewModel.insertAd(ad)
                        } else {
                            if (oldAd != null) {
                                val oldImage = oldAd.image
                                oldAd.title = adTitle.text?.trim().toString()
                                oldAd.image = imagesList.first().toString()
                                statisticsViewModel.updateAd(oldAd,oldImage)
                            }
                        }

                    }else {
                        showMessage(requireContext(),"required ad image")
                    }

                }

            }
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
                        setAddProductButtonStatus()
                        hideKeyboard()
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
                        setAddProductButtonStatus()
                        hideKeyboard()
                    }

                }
            }
        }
    }


    private fun setAddProductButtonStatus() {
        if (viewModel.userData.value?.userType == UserType.SELLER.name){
            binding.btnAddProduct.show()
        }else{
            binding.btnAddProduct.hide()
        }
    }

    private fun setUserViews() {
        binding.apply {
            when(SharePrefManager(requireContext()).loadUser().userType) {
                UserType.CUSTOMER.name -> { /** customer **/
                    homeTopAppBar.topAppBar.inflateMenu(R.menu.customer_home_app_bar_menu)
                    btnAddProduct.hide()
                }
                UserType.SELLER.name -> { /** seller **/
                    homeTopAppBar.topAppBar.inflateMenu(R.menu.seller_home_app_bar_menu)
                    btnAddProduct.show()
                }
                UserType.ADMIN.name ->{ /** admin **/
                    homeTopAppBar.topAppBar.inflateMenu(R.menu.admin_home_app_bar_menu)
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
                }
            }


            /** ads live data **/
            statisticsViewModel.ads.observe(viewLifecycleOwner) { ads ->
                if (!ads.isNullOrEmpty()){

                    setAdBanner(ads)
                    swipeRefreshLayout.isRefreshing = false
                }else{
                    adBanner.root.hide()
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
                        DataStatus.LOADING -> {}
                        DataStatus.SUCCESS -> { viewModel.resetProgress() }
                        DataStatus.ERROR -> {}
                    }
                }
            }



            /** is sign out **/
            profileViewModel.isSignOut.observe(viewLifecycleOwner){ isSignOut->
                if (isSignOut == true){
                    val intent = Intent(requireContext(), RegistrationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }


            /** insert ad status **/
            statisticsViewModel.insertAdStatus.observe(viewLifecycleOwner) { status->
                if (status != null){
                    when(status){
                        DataStatus.LOADING -> {
                            adManagerSheet.loader.root.show()
                        }
                        DataStatus.SUCCESS -> {
                            adManagerSheet.loader.root.hide()
                            statisticsViewModel.resetProgress()
                            bottomAdSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                            hideKeyboard()

                        }
                        DataStatus.ERROR -> {
                            adManagerSheet.loader.root.hide()
                        }
                    }
                }
            }



            /** update ad status **/
            statisticsViewModel.updateAdStatus.observe(viewLifecycleOwner) { status->
                if (status != null){
                    when(status) {
                        DataStatus.LOADING -> {
                            adManagerSheet.loader.root.show()
                        }
                        DataStatus.SUCCESS -> {
                            adManagerSheet.loader.root.hide()
                            statisticsViewModel.resetProgress()
                            bottomAdSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                            hideKeyboard()
                        }
                        DataStatus.ERROR -> {
                            adManagerSheet.loader.root.hide()
                        }
                    }
                }
            }



            /** update ad status **/
            statisticsViewModel.deleteAdStatus.observe(viewLifecycleOwner) { status->
                if (status != null){
                    when(status) {
                        DataStatus.LOADING -> {
                            adManagerSheet.loader.root.show()
                        }
                        DataStatus.SUCCESS -> {
                            adManagerSheet.loader.root.hide()
                            statisticsViewModel.resetProgress()
                            bottomAdSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                            hideKeyboard()
                        }
                        DataStatus.ERROR -> {
                            adManagerSheet.loader.root.hide()
                        }
                    }
                }
            }





        }



    }


    private fun setRate() {
        val rateValues = arrayOf("1","2","3","4","5")
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1, rateValues)
        binding.filterSheet.selectRate.setAdapter(adapter)
    }


    private val getImages = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { result ->
        imagesList.addAll(result)
        if (imagesList.size > 1) {
            imagesList = imagesList.subList(0, 1)
            showMessage(requireContext(),"you can set 1 ad as a maximum")
        }
        val adapter = context?.let { AddProductImagesAdapter(it, imagesList) }
        binding.adManagerSheet.addImages.rvImages.adapter = adapter
    }

    private fun setAppBarItemClicks(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.item_filter -> {
                productFilterSheet()
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


    private fun setAdBanner(list: List<Ad>) {
        binding.adBanner.apply {

            val imageList = ArrayList<SlideModel>()
            list.forEach { ad ->
                val slide = SlideModel(ad.image)
                if (!ad.title.isNullOrEmpty()) { // if ad does not have a title the gray ad bar it will be removed
                    slide.title = ad.title
                }
                imageList.add(slide)
            }

            imageSlider.setImageList(imageList)

            /** on Ad Clicked **/
            imageSlider.setItemClickListener( object : ItemClickListener {
                override fun onItemSelected(position: Int) {
                    val isAdmin = viewModel.userData.value?.userType == UserType.ADMIN.name
                    if(isAdmin){
                        val ad = list[position]
                        adManagerSheet(isEdit = true, ad)
                    }

                }
            })
            root.show()
        }


    }


    private fun setHomeTopAppBar() {
        binding.apply {

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

    // the icons in menu does not appear in android version below than 10
    private fun showAdminOptionMenu(v: View) {
        val popupMenu = PopupMenu(requireContext(),v)
        popupMenu.menuInflater.inflate(R.menu.admin_options_menu,popupMenu.menu)
        popupMenu.setForceShowIcon(true)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_notification -> {}
                R.id.item_productManager -> {}
                R.id.item_adManager ->{ adManagerSheet(isEdit = false,null) }
                R.id.item_signOut -> { profileViewModel.signOut() }
            }
            true
        }
        popupMenu.show()
    }



    private fun setProductsAdapter() {
        val likesList = viewModel.userLikes.value ?: emptyList()
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
                    findNavController().navigate(R.id.action_home_to_addEditProduct, data)
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

