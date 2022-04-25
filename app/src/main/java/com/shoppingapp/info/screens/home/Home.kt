

package com.shoppingapp.info.screens.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shoppingapp.info.ProductCategories
import com.shoppingapp.info.R
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.databinding.HomeBinding
import com.shoppingapp.info.utils.MyOnFocusChangeListener
import com.shoppingapp.info.utils.StoreDataStatus
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


class Home : Fragment() {

    companion object{
        const val TAG = "Home"
    }

    private lateinit var binding: HomeBinding
    private val viewModel by sharedViewModel<HomeViewModel>()
    private val focusChangeListener = MyOnFocusChangeListener()

    private val productController by lazy { ProductController() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.home,container,false)


        setViews()
        setObservers()


        return binding.root
    }


    override fun onResume() {
        super.onResume()

        if (viewModel.isUserSeller()){
            refreshData()
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserLikes()
    }


    private fun refreshData() {
        if (viewModel.isUserSeller()){
            viewModel.getProductByOwner()
        }else{
            viewModel.refreshProducts()
        }
    }


    // TODO: 4/22/2022 update the local data base before app is open ..
    // TODO: 4/22/2022 you can update the database in backend using work manager

    private fun setViews() {

        /** swipe to refresh **/
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }


        setProductsAdapter()
        setHomeTopAppBar()

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


        if (!viewModel.isUserSeller()) {
            binding.btnAddProduct.visibility = View.GONE
        }

        binding.btnAddProduct.setOnClickListener {
            showDialogWithItems(ProductCategories, 0, false)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setObservers() {


        /** products live data **/
        viewModel.products.observe(viewLifecycleOwner) {
            val mix = getMixedDataList(it, getAdsList())
            val likes = viewModel.userLikes.value ?: emptyList()
            productController.likes = likes
            productController.setData(mix)
            binding.swipeRefreshLayout.isRefreshing = false
        }




//        viewModel.userLikes.observe(viewLifecycleOwner) { likes ->
//            if (likes != null) {
//                productController.likes = likes
//            }
//            binding.swipeRefreshLayout.isRefreshing = false
//        }




    }

    private fun performSearch(query: String) {
        viewModel.filterBySearch(query)
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
                // show favorite products list
                val likedProducts = viewModel.likedProducts.value
                val userLikes = bundleOf("userLikes" to likedProducts)
                findNavController().navigate(R.id.action_homeFragment_to_favoritesFragment,userLikes)

                true
            }
            else -> false
        }
    }

    private fun setHomeTopAppBar() {
        var lastInput = ""
        val debounceJob: Job? = null
        val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        binding.homeTopAppBar.topAppBar.inflateMenu(R.menu.home_app_bar_menu)
        if (viewModel.isUserSeller()) {
            binding.homeTopAppBar.topAppBar.menu.removeItem(R.id.item_favorites)
        }
        binding.homeTopAppBar.homeSearchEditText.onFocusChangeListener = focusChangeListener
        binding.homeTopAppBar.homeSearchEditText.doAfterTextChanged { editable ->
            if (editable != null) {
                val newtInput = editable.toString()
                debounceJob?.cancel()
                if (lastInput != newtInput) {
                    lastInput = newtInput
                    uiScope.launch {
                        delay(500)
                        if (lastInput == newtInput) {
                            performSearch(newtInput)
                        }
                    }
                }
            }
        }
        binding.homeTopAppBar.homeSearchEditText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                textView.clearFocus()
                val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(textView.windowToken, 0)
                performSearch(textView.text.toString())
                true
            } else {
                false
            }
        }
        binding.homeTopAppBar.searchOutlinedTextLayout.setEndIconOnClickListener {
            it.clearFocus()
            binding.homeTopAppBar.homeSearchEditText.setText("")
            val inputManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(it.windowToken, 0)
//			viewModel.filterProducts("All")
        }
        binding.homeTopAppBar.topAppBar.setOnMenuItemClickListener { menuItem ->
            setAppBarItemClicks(menuItem)
        }
    }

    private fun setProductsAdapter() {
        val likesList = viewModel.userLikes.value ?: emptyList()
        productController.isSeller = viewModel.isUserSeller()
        productController.likes = likesList
        productController.setData(emptyList())


        /** click listener **/
        productController.clickListener = object: ProductController.OnClickListener{

            override fun onAdClicked() {}

            override fun onProductClick(product: Product) {

                if (viewModel.isUserSeller()){
                    navigateToAddEditProductScreen(isEdit = true, productId = product.productId)
                }else {
                    findNavController().navigate(
                        R.id.action_home_to_productDetails,
                        bundleOf("productId" to product.productId)
                    )
                }
            }


            override fun onLikeClick(product: Product) {
                viewModel.toggleLikeByProductId(product.productId)
                Toast.makeText(requireContext(),"liked",Toast.LENGTH_SHORT).show()
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
                            viewModel.filterProducts(categoryItems[checkedItem])
                        } else {
                            navigateToAddEditProductScreen(
                                isEdit = false,
                                catName = categoryItems[checkedItem]
                            )
                        }
                    }
                    dialog.cancel()
                }
                .show()
        }
    }

    private fun navigateToAddEditProductScreen(isEdit: Boolean, catName: String? = null, productId: String? = null) {
        findNavController().navigate(
            R.id.action_goto_addProduct,
            bundleOf("isEdit" to isEdit, "categoryName" to catName, "productId" to productId)
        )
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

