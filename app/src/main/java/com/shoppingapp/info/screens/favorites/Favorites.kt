package com.shoppingapp.info.screens.favorites


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.databinding.FavoritiesBinding
import com.shoppingapp.info.screens.home.Home
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.screens.home.RecyclerViewPaddingItemDecoration
import com.shoppingapp.info.utils.Constants
import com.shoppingapp.info.utils.DataStatus
import com.shoppingapp.info.utils.hide
import com.shoppingapp.info.utils.show
import org.koin.android.viewmodel.ext.android.sharedViewModel

class Favorites : Fragment() {

    companion object{ const val TAG = "Favorites" }

    private val homeViewModel by sharedViewModel<HomeViewModel>()

    private lateinit var binding: FavoritiesBinding
    private val favoritesController by lazy { FavoritesController() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.favorities, container, false)

        binding.favTopAppBar.topAppBar.title = "Favorite Products"


        setViews()
        setObserves()

        return binding.root



    }




    private fun setObserves() {

        /** liked products **/
        homeViewModel.likedProducts.observe(viewLifecycleOwner) { likedProducts ->
            if (!likedProducts.isNullOrEmpty()){
                favoritesController.setData(likedProducts)
                binding.tvEmptyFavorits.hide()
            }else {
                favoritesController.setData(emptyList())
                binding.tvEmptyFavorits.show()
            }
        }



            /** remove like status **/
            homeViewModel.removeLikeStatus.observe(viewLifecycleOwner) { status ->
                if (status != null){
                    when (status) {
                        DataStatus.LOADING -> {}
                        DataStatus.SUCCESS -> {homeViewModel.resetProgress()}
                        DataStatus.ERROR -> {}
                    }
                }
            }



    }



    private fun setViews(){

        setProductsAdapter()

//        binding.favoriteProductsRecyclerView.apply {
//            val itemDecoration = RecyclerViewPaddingItemDecoration(requireContext())
//            if (itemDecorationCount == 0) {
//                addItemDecoration(itemDecoration)
//            }
//            adapter = favoritesController.adapter
//        }


        // back button
        binding.favTopAppBar.topAppBar.setOnClickListener {
            findNavController().navigateUp()
        }



    }



    private fun setProductsAdapter() {
        binding.apply {
            /** click listener **/
            favoritesController.clickListener = object : FavoritesController.OnClickListener {

                override fun onProductClick(product: Product) {
                    val data = bundleOf(Constants.KEY_PRODUCT to product)
                    findNavController().navigate(R.id.action_favorites_to_productDetails, data)
                }

                override fun onLikeClick(product: Product) {
                    homeViewModel.removeLikeByProductId(product)
                }

            }

            rvFavorites.adapter = favoritesController.adapter
        }



    }






}