//package com.shoppingapp.info.screens.favorites
//
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.core.os.bundleOf
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import com.shoppingapp.info.R
//import com.shoppingapp.info.data.Product
//import com.shoppingapp.info.databinding.FavoritiesBinding
//import com.shoppingapp.info.screens.home.Home
//import com.shoppingapp.info.screens.home.HomeViewModel
//import com.shoppingapp.info.screens.home.RecyclerViewPaddingItemDecoration
//import org.koin.android.viewmodel.ext.android.sharedViewModel
//
//class Favorites : Fragment() {
//
//    companion object{
//        const val TAG = "Favorites"
//    }
//
//
//    private val viewModel by sharedViewModel<FavoritesViewModel>()
//    private val homeViewModel by sharedViewModel<HomeViewModel>()
//
//
//    private lateinit var binding: FavoritiesBinding
//    private val favoritesController by lazy { FavoritesController() }
//    private lateinit var likedProducts: List<Product>
//
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        binding = DataBindingUtil.inflate(inflater, R.layout.favorities, container, false)
//
//        binding.favTopAppBar.topAppBar.title = "Favorite Products"
//        likedProducts = arguments?.get("userLikes") as List<Product>
//
//
//
//        setViews()
//        setObserves()
//
////        viewModel.initData(likedProducts)
//
//        return binding.root
//
//
//
//    }
//
//
//
//
//    private fun setObserves(){
//
////        /** live data liked products **/
////        viewModel.likedProducts.observe(viewLifecycleOwner){ likedProducts ->
////            Log.i(TAG,"liked products: ${likedProducts.size}")
////            if (likedProducts != null){
////                favoritesController.setData(likedProducts)
////            }else{
////                favoritesController.setData(emptyList())
////                binding.favoritesEmptyMessage.visibility = View.VISIBLE
////            }
////        }
//
//
//    }
//
//
//    private fun setProductsAdapter() {
//        val products = likedProducts.toMutableSet()
//
//        favoritesController.setData(likedProducts)
//
//        /** click listener **/
//        favoritesController.clickListener = object : FavoritesController.OnClickListener{
//
//            override fun onProductClick(product: Product) {
//
//                findNavController().navigate(
//                    R.id.action_favorites_to_productDetails,
//                    bundleOf("productId" to product.productId)
//                )
//
//            }
//
//
//            override fun onLikeClick(product: Product) {
//
//                products.remove(product)
//                favoritesController.setData(products.toList())
////                homeViewModel.toggleLikeByProductId(product.productId)
//
//
//
//            }
//
//
//        }
//
//
//    }
//
//
//
//    private fun setViews(){
//
//        setProductsAdapter()
//
//        binding.favoriteProductsRecyclerView.apply {
//            val itemDecoration = RecyclerViewPaddingItemDecoration(requireContext())
//            if (itemDecorationCount == 0) {
//                addItemDecoration(itemDecoration)
//            }
//            adapter = favoritesController.adapter
//        }
//
//
//
//    }
//
//
//    private fun removeLike(product: Product){
//        viewModel.toggleLikeByProductId(product,
//            onError = { error ->
//                Toast.makeText(requireContext(),error,Toast.LENGTH_SHORT).show()
//            })
//    }
//
//
//
//
//}