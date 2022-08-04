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
import org.koin.android.viewmodel.ext.android.sharedViewModel

class Favorites : Fragment() {

    companion object{
        const val TAG = "Favorites"
    }


    private lateinit var viewModel: FavoritesViewModel
    private val homeViewModel by sharedViewModel<HomeViewModel>()


    private lateinit var binding: FavoritiesBinding
    private val favoritesController by lazy { FavoritesController() }
    private var likedProducts = ArrayList<Product>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.favorities, container, false)

        binding.favTopAppBar.topAppBar.title = "Favorite Products"
        viewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]

//        likedProducts = arguments?.get("userLikes") as List<Product>





        setViews()
        setObserves()

//        viewModel.initData(likedProducts)

        return binding.root



    }




    private fun setObserves(){

        with(viewModel){

            /** remove like status **/
            removeLikeStatus.observe(viewLifecycleOwner) { status ->
                when(status){
                    DataStatus.SUCCESS -> { favoritesController.setData(likedProducts) } // once the data it will updated once the item is removed.
                    DataStatus.LOADING -> {}
                    DataStatus.ERROR -> {}
                    else -> {}
                }
            }



        }

    }


    private fun setProductsAdapter() {
        val products = homeViewModel.likedProducts.value ?: emptyList()
        likedProducts.addAll(products)
        favoritesController.setData(products)


        /** click listener **/
        favoritesController.clickListener = object : FavoritesController.OnClickListener{

            override fun onProductClick(product: Product) {

                val data = bundleOf(Constants.KEY_PRODUCT to product)
                findNavController().navigate(R.id.action_favorites_to_productDetails, data)

            }


            override fun onLikeClick(product: Product) {
                val userId = homeViewModel.userData.value?.userId!!
                viewModel.removeLikeByProductId(product.productId,userId)
                likedProducts.remove(product)

            }

        }

    }



    private fun setViews(){

        setProductsAdapter()

        binding.favoriteProductsRecyclerView.apply {
            val itemDecoration = RecyclerViewPaddingItemDecoration(requireContext())
            if (itemDecorationCount == 0) {
                addItemDecoration(itemDecoration)
            }
            adapter = favoritesController.adapter
        }



    }






}