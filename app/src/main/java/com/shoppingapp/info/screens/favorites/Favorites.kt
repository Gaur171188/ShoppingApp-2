package com.shoppingapp.info.screens.favorites

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FavoritiesBinding
    private lateinit var productsAdapter: LikedProductAdapter



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.favorities, container, false)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]


        setViews()
        setObservers()

        return binding.root
    }

    private fun setViews() {
//        viewModel.setDataLoading()
        viewModel.getLikedProducts()
        binding.favTopAppBar.topAppBar.title = "Favorite Products"
        binding.favTopAppBar.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.favoritesEmptyMessage.visibility = View.GONE
        if (context != null) {
            val proList = viewModel.likedProducts.value ?: emptyList()
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
            binding.favProductsRecyclerView.apply {
                val itemDecoration = RecyclerViewPaddingItemDecoration(requireContext())
                if (itemDecorationCount == 0) {
                    addItemDecoration(itemDecoration)
                }
            }
        }
    }



    @SuppressLint("NotifyDataSetChanged")
    private fun setObservers() {

        /** live data - data status **/
        viewModel.dataStatus.observe(viewLifecycleOwner) { status ->
            if (status != null){
                when(status){
                    StoreDataStatus.LOADING->{
                        Log.i(TAG,"liked products is loading..")
                        binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                        binding.loaderLayout.circularLoader.showAnimationBehavior
                        binding.favoritesEmptyMessage.visibility = View.GONE

                    }
                    StoreDataStatus.DONE ->{
                        Log.i(TAG,"Getting liked products success..")
                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                        binding.loaderLayout.circularLoader.hideAnimationBehavior
                    }
                    StoreDataStatus.ERROR ->{
                        Log.i(TAG,"Error happening during getting the products likes")
                    }
                }
            }
        }

        /** liked products **/
        viewModel.likedProducts.observe(viewLifecycleOwner) {
            if (it != null) {
//                productsAdapter.data = viewModel.likedProducts.value!!

                productsAdapter.data = it
                binding.favProductsRecyclerView.adapter = productsAdapter
                binding.favProductsRecyclerView.adapter?.apply {
                    notifyDataSetChanged()
                }
            } else {
                Log.i(TAG,"no liked products found")
                binding.favoritesEmptyMessage.visibility = View.VISIBLE
                binding.favProductsRecyclerView.visibility = View.GONE
                binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                binding.loaderLayout.circularLoader.hideAnimationBehavior
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