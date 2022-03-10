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


    private lateinit var viewModel: FavoritesViewModel
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FavoritiesBinding
    private lateinit var productsAdapter: LikedProductAdapter



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.favorities, container, false)

        viewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]
        binding.favTopAppBar.topAppBar.title = "Favorite Products"


        // TODO: fix issue the favorites data does not displayed in first time.



        setViews()
        setObserves()


        return binding.root
    }



    private fun setObserves(){

        /** live data liked products **/
        viewModel.likedProducts.observe(viewLifecycleOwner){ likedProducts ->
            Log.i(TAG,"liked products: ${likedProducts.size}")
            if (likedProducts != null){
                refreshAdapter(likedProducts)
            }else{
                binding.favoritesEmptyMessage.visibility = View.VISIBLE
            }
        }


        /** live data store status **/
        viewModel.dataStatus.observe(viewLifecycleOwner){ status->
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
            viewModel.loadingIsDone()
        }
    }


    private fun setViews(){
        val likedProducts = arguments?.get("userLikes") as List<Product>
        viewModel.initData(likedProducts )
        productsAdapter = LikedProductAdapter(likedProducts, requireContext())
        productsAdapter.onClickListener = object : LikedProductAdapter.OnClickListener {
            override fun onClick(product: Product) {
                Log.d(TAG, "Product: ${product.productId} clicked")
                findNavController().navigate(
                    R.id.action_favorites_to_productDetails,
                    bundleOf("productId" to product.productId)
                )
            }

            override fun onDeleteClick(product: Product) {
                removeLike(product)
            }
        }

        binding.favoriteProductsRecyclerView.apply {
            val itemDecoration = RecyclerViewPaddingItemDecoration(requireContext())
            if (itemDecorationCount == 0) {
                addItemDecoration(itemDecoration)
            }
        }
        viewModel.loadingIsDone()
    }


    private fun removeLike(product: Product){
        val isConnected = homeViewModel.isConnected.value
        if (isConnected != null){
            if (isConnected){
                viewModel.toggleLikeByProductId(product,
                    onError = { error ->
                        Toast.makeText(requireContext(),error,Toast.LENGTH_SHORT).show()
                    })
            }else{
                Toast.makeText(requireContext(),"error connection!",Toast.LENGTH_SHORT).show()
            }
        }
    }




}