package com.shoppingapp.info.screens.favorites

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.databinding.FavoritiesBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.screens.home.RecyclerViewPaddingItemDecoration
import com.shoppingapp.info.utils.StoreDataStatus

class Favorites : Fragment() {

    companion object{
        const val TAG = "Favorites"
    }

    private  val viewModel by activityViewModels<HomeViewModel>()
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var binding: FavoritiesBinding
    private lateinit var productsAdapter: LikedProductAdapter



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.favorities, container, false)

        favoritesViewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]
        binding.favTopAppBar.topAppBar.title = "Favorite Products"



//        setViews()
//        setObservers()



        initRecyclerView()
        observers()


        return binding.root
    }



    private fun observers(){

//        /** live data all products **/
//        favoritesViewModel.allProducts.observe(viewLifecycleOwner){ allProducts ->
//            if (allProducts != null){
//                favoritesViewModel.getLikedProducts(allProducts)
//            }
//        }



        /** live data liked products **/
        favoritesViewModel.likedProducts.observe(viewLifecycleOwner){ likedProducts ->
            Log.i(TAG,"liked products: ${likedProducts.size}")
            if (likedProducts != null){
                refreshAdapter(likedProducts)
            }else{
                binding.favoritesEmptyMessage.visibility = View.VISIBLE
            }
        }

        /** live data store status **/
        favoritesViewModel.dataStatus.observe(viewLifecycleOwner){status->
            if (status != null){
                when(status){
                    StoreDataStatus.LOADING ->{
                        binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                        binding.loaderLayout.circularLoader.showAnimationBehavior
                        binding.favoritesEmptyMessage.visibility = View.GONE
                    }

                    StoreDataStatus.DONE ->{
                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                        binding.loaderLayout.circularLoader.hideAnimationBehavior
                        binding.favoritesEmptyMessage.visibility = View.GONE

                    }

                    StoreDataStatus.ERROR ->{
                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                        binding.loaderLayout.circularLoader.hideAnimationBehavior
                        binding.favoritesEmptyMessage.visibility = View.VISIBLE
                    }
                }
            }

        }

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun refreshAdapter(likedProducts: List<Product>){
        productsAdapter.data = likedProducts
        binding.favoriteProductsRecyclerView.adapter = productsAdapter
        binding.favoriteProductsRecyclerView.adapter?.apply {
            notifyDataSetChanged()
            favoritesViewModel.loadingIsDone()
        }
    }


    private fun initRecyclerView(){
        val likedProducts = arguments?.get("userLikes")  as List<*>
        favoritesViewModel.initData(likedProducts as List<Product> )
        productsAdapter = LikedProductAdapter(arrayListOf(), requireContext())
        productsAdapter.onClickListener = object : LikedProductAdapter.OnClickListener {
            override fun onClick(product: Product) {
                Log.d(TAG, "Product: ${product.productId} clicked")
                findNavController().navigate(
                    R.id.action_favorites_to_productDetails,
                    bundleOf("productId" to product.productId)
                )
            }

            override fun onDeleteClick(productId: String) {
                // TODO: make the remove like require network.
                viewModel.toggleLikeByProductId(productId)
            }
        }

        binding.favoriteProductsRecyclerView.apply {
            val itemDecoration = RecyclerViewPaddingItemDecoration(requireContext())
            if (itemDecorationCount == 0) {
                addItemDecoration(itemDecoration)
            }
        }

    }







    private fun initRecyclerView2(){
        val proList = favoritesViewModel.likedProducts.value ?: emptyList()
        productsAdapter = LikedProductAdapter(proList, requireContext())
        productsAdapter.onClickListener = object : LikedProductAdapter.OnClickListener {
            override fun onClick(product: Product) {
                Log.d(TAG, "Product: ${product.productId} clicked")
                findNavController().navigate(
                    R.id.action_favorites_to_productDetails,
                    bundleOf("productId" to product.productId)
                )
            }

            override fun onDeleteClick(productId: String) {
                viewModel.toggleLikeByProductId(productId)
            }
        }
        binding.favoriteProductsRecyclerView.apply {
            val itemDecoration = RecyclerViewPaddingItemDecoration(requireContext())
            if (itemDecorationCount == 0) {
                addItemDecoration(itemDecoration)
            }
        }
    }

    private fun setViews() {
//        viewModel.setDataLoading()


        binding.favTopAppBar.topAppBar.title = "Favorite Products"
        binding.favTopAppBar.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.favoritesEmptyMessage.visibility = View.GONE
        if (context != null) {

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setObservers() {
        favoritesViewModel.dataStatus.observe(viewLifecycleOwner) { status ->
            if (status == StoreDataStatus.LOADING) {
                binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                binding.loaderLayout.circularLoader.showAnimationBehavior
                binding.favoritesEmptyMessage.visibility = View.GONE
            } else if (status != null) {

                favoritesViewModel.likedProducts.observe(viewLifecycleOwner) {
                    if (it != null) {
                        productsAdapter.data = favoritesViewModel.likedProducts.value!!
                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                        binding.loaderLayout.circularLoader.hideAnimationBehavior
                        productsAdapter.data = it
                        binding.favoriteProductsRecyclerView.adapter = productsAdapter
                        binding.favoriteProductsRecyclerView.adapter?.apply {
                            notifyDataSetChanged()
                        }
                    } else {
                        binding.favoritesEmptyMessage.visibility = View.VISIBLE
                        binding.favoriteProductsRecyclerView.visibility = View.GONE
                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                        binding.loaderLayout.circularLoader.hideAnimationBehavior
                    }
                }
            }
        }





    }



//    @SuppressLint("NotifyDataSetChanged")
//    private fun setObservers() {
//        viewModel.dataStatus.observe(viewLifecycleOwner) { status ->
//            if (status == StoreDataStatus.LOADING) {
//                binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
//                binding.loaderLayout.circularLoader.showAnimationBehavior
//                binding.favoritesEmptyMessage.visibility = View.GONE
//            } else if (status != null) {
//                viewModel.likedProducts.observe(viewLifecycleOwner) {
//                    if (it.isNotEmpty()) {
//                        productsAdapter.data = viewModel.likedProducts.value!!
//                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                        binding.loaderLayout.circularLoader.hideAnimationBehavior
//                        productsAdapter.data = it
//                        binding.favProductsRecyclerView.adapter = productsAdapter
//                        binding.favProductsRecyclerView.adapter?.apply {
//                            notifyDataSetChanged()
//                        }
//                    } else if (it.isEmpty()) {
//                        binding.favoritesEmptyMessage.visibility = View.VISIBLE
//                        binding.favProductsRecyclerView.visibility = View.GONE
//                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                        binding.loaderLayout.circularLoader.hideAnimationBehavior
//                    }
//                }
//            }
//        }
//    }

}