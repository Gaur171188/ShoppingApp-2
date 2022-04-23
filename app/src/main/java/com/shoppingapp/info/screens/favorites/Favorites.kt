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
import androidx.navigation.fragment.findNavController
import com.shoppingapp.info.R
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.databinding.FavoritiesBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.screens.home.ProductController
import com.shoppingapp.info.screens.home.RecyclerViewPaddingItemDecoration
import com.shoppingapp.info.utils.StoreDataStatus
import org.koin.android.viewmodel.ext.android.sharedViewModel

class Favorites : Fragment() {

    companion object{
        const val TAG = "Favorites"
    }


    private val viewModel by sharedViewModel<FavoritesViewModel>()
    private val homeViewModel: HomeViewModel by sharedViewModel()
    private lateinit var binding: FavoritiesBinding
    private val favoritesController by lazy { FavoritesController() }
//    private lateinit var productsAdapter: LikedProductAdapter
    private lateinit var likedProducts: List<Product>






    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.favorities, container, false)

//        viewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]
        binding.favTopAppBar.topAppBar.title = "Favorite Products"
        likedProducts = arguments?.get("userLikes") as List<Product>


        // TODO: fix issue the favorites data does not displayed in first time.
        // TODO: get the data from the room



        setViews()
        setObserves()

        viewModel.initData(likedProducts)

        return binding.root
    }




    private fun setObserves(){

        /** live data liked products **/
        viewModel.likedProducts.observe(viewLifecycleOwner){ likedProducts ->
            Log.i(TAG,"liked products: ${likedProducts.size}")
            if (likedProducts != null){
                favoritesController.setData(likedProducts)
            }else{
                favoritesController.setData(emptyList())
                binding.favoritesEmptyMessage.visibility = View.VISIBLE
            }
        }


//        /** live data store status **/
//        viewModel.dataStatus.observe(viewLifecycleOwner){ status->
//            if (status != null){
//                when(status){
//                    StoreDataStatus.LOADING ->{
//                        binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
//                        binding.loaderLayout.circularLoader.showAnimationBehavior
//                        binding.favoritesEmptyMessage.visibility = View.GONE
//                    }
//
//                    StoreDataStatus.DONE ->{
//                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                        binding.loaderLayout.circularLoader.hideAnimationBehavior
//                        binding.favoritesEmptyMessage.visibility = View.GONE
//
//                    }
//
//                    StoreDataStatus.ERROR ->{
//                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                        binding.loaderLayout.circularLoader.hideAnimationBehavior
//                        binding.favoritesEmptyMessage.visibility = View.VISIBLE
//                    }
//                }
//            }
//
//        }

    }



//    @SuppressLint("NotifyDataSetChanged")
//    private fun refreshAdapter(likedProducts: List<Product>){
//        productsAdapter.data = likedProducts
//        binding.favoriteProductsRecyclerView.adapter = productsAdapter
//
//        binding.favoriteProductsRecyclerView.adapter?.apply {
//            notifyDataSetChanged()
//            viewModel.loadingIsDone()
//        }
//    }


    private fun setProductsAdapter() {


        favoritesController.setData(emptyList())


        /** click listener **/
        favoritesController.clickListener = object : FavoritesController.OnClickListener{

            override fun onProductClick(product: Product) {

                findNavController().navigate(
                    R.id.action_favorites_to_productDetails,
                    bundleOf("productId" to product.productId)
                )

            }


            override fun onLikeClick(product: Product) {

                viewModel.toggleLikeByProductId(product){
                    Toast.makeText(requireContext(),"liked",Toast.LENGTH_SHORT).show()
                }

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

//        productsAdapter = LikedProductAdapter(likedProducts, requireContext())
//
//        productsAdapter.onClickListener = object : LikedProductAdapter.OnClickListener {
//            override fun onClick(product: Product) {
//                Log.d(TAG, "Product: ${product.productId} clicked")
//                findNavController().navigate(
//                    R.id.action_favorites_to_productDetails,
//                    bundleOf("productId" to product.productId)
//                )
//            }
//
//            override fun onDeleteClick(product: Product) {
//                removeLike(product)
//            }
//
//        }


//        viewModel.loadingIsDone()
    }


    private fun removeLike(product: Product){
        viewModel.toggleLikeByProductId(product,
            onError = { error ->
                Toast.makeText(requireContext(),error,Toast.LENGTH_SHORT).show()
            })
    }




}