
package com.shoppingapp.info.screens.cart
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shoppingapp.info.R
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.*
import com.shoppingapp.info.priceCardLayout
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.screens.orders.OrdersViewModel
import com.shoppingapp.info.utils.StoreDataStatus
import org.koin.android.viewmodel.ext.android.sharedViewModel


class Cart : Fragment() {

    companion object{
        const val TAG = "Cart"
    }

//    private val ordersViewModel by sharedViewModel<OrdersViewModel>()
    private val homeViewModel by sharedViewModel<HomeViewModel>()


    private val viewModel by sharedViewModel<CartViewModel>()


    private lateinit var binding: CartBinding
    private val cartController by lazy {CartController()}
    private val priceCartController by lazy {PriceCartController()}


//    private lateinit var itemsAdapter: CartItemAdapter
    private lateinit var concatAdapter: ConcatAdapter




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.cart, container, false)


        // TODO: improve the cart item design.


//        cartController = CartController()



        setViews()
        setObservers()
        viewModel.getCartItems()

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        viewModel.getCartItems()
    }

    private fun setViews() {




        /** controller listener **/
        cartController.clickListener = object : CartController.OnClickListener{

            override fun onItemClick(cartItem: User.CartItem, position: Int) {
                Toast.makeText(requireContext(),"onCartClick",Toast.LENGTH_SHORT).show()
            }

            override fun onDeleteClick(cartItem: User.CartItem, position: Int) {
                showDeleteDialog(cartItem.itemId)
            }

        }


//        binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
//        binding.loaderLayout.circularLoader.showAnimationBehavior
        binding.cartAppBar.topAppBar.title = getString(R.string.cart_fragment_label)
        binding.cartEmptyMessage.visibility = View.GONE
        binding.cartCheckOutBtn.setOnClickListener {
            navigateToSelectAddress()
        }


        if (context != null) {
            try {
                concatAdapter = ConcatAdapter(cartController.adapter, priceCartController.adapter)
                binding.cartRecyclerView.adapter = concatAdapter
            }catch (ex: Exception){

            }

//            setItemsAdapter(ordersViewModel.cartItems.value)
//            concatAdapter = ConcatAdapter(itemsAdapter, PriceCardAdapter())
//            binding.cartProductsRecyclerView.adapter = concatAdapter
        }



    }

    private fun setObservers() {


        // cart items
        viewModel.cartItems.observe(viewLifecycleOwner){ cartItems ->
            if (cartItems != null){
                val items = viewModel.cartItems.value ?: emptyList()
                val likeList = homeViewModel.userLikes.value ?: emptyList()
                val prosList = viewModel.cartProducts.value ?: emptyList()
                cartController.products = prosList
                cartController.likes = likeList
                cartController.setData(items)

//                binding.cartRecyclerView.adapter?.notifyDataSetChanged()

                Log.d(TAG,"cartItems: ${cartItems.size}")
                Log.d(TAG,"likes: ${likeList.size}")
                Log.d(TAG,"prosList: ${prosList.size}")

            }else{
                cartController.setData(emptyList())
            }
        }


//        // likes
//        homeViewModel.userLikes.observe(viewLifecycleOwner){ likes ->
//            if(likes != null){
//                val items = viewModel.cartItems.value ?: emptyList()
//                val likeList = homeViewModel.userLikes.value ?: emptyList()
//                val prosList = viewModel.cartProducts.value ?: emptyList()
//                cartController.products = prosList
//                cartController.likes = likeList
//                cartController.setData(items)
////                Log.d(TAG,"likes in cart : ${likes.size}")
//            }
//        }

        // price list
        viewModel.priceList.observe(viewLifecycleOwner) { priceList ->
            if (priceList != null){
                val items = viewModel.cartItems.value ?: emptyList()
                val likeList = homeViewModel.userLikes.value ?: emptyList()
                val prosList = viewModel.cartProducts.value ?: emptyList()
                cartController.products = prosList
                cartController.likes = likeList
                cartController.setData(items)
                priceCartController.setData(priceList)
            }
        }


//
//        viewModel.dataStatus.observe(viewLifecycleOwner) { status ->
//            when (status) {
//                StoreDataStatus.LOADING -> {
//                    binding.cartRecyclerView.visibility = View.GONE
//                    binding.cartCheckOutBtn.visibility = View.GONE
//                    binding.cartEmptyMessage.visibility = View.GONE
//                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
//                    binding.loaderLayout.circularLoader.showAnimationBehavior
//                }
//                else -> {
//                    binding.loaderLayout.circularLoader.hideAnimationBehavior
//                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                }
//            }
//        }
//
//        ordersViewModel.dataStatus.observe(viewLifecycleOwner) { status ->
//            if (status != null && status != StoreDataStatus.LOADING) {
//                ordersViewModel.cartProducts.observe(viewLifecycleOwner) { itemList ->
//                    if (itemList.isNotEmpty()) {
//                        initData()
//                        binding.cartEmptyMessage.visibility = View.GONE
//                        binding.cartRecyclerView.visibility = View.VISIBLE
//                        binding.cartCheckOutBtn.visibility = View.VISIBLE
//                    } else if (itemList.isEmpty()) {
//                        binding.cartRecyclerView.visibility = View.GONE
//                        binding.cartCheckOutBtn.visibility = View.GONE
//                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                        binding.loaderLayout.circularLoader.hideAnimationBehavior
//                        binding.cartEmptyMessage.visibility = View.VISIBLE
//                    }
//                }
//            }
//        }
    }




//    fun initData(){
//        val items = ordersViewModel.cartItems.value ?: emptyList()
//        val likeList = homeViewModel.userLikes.value ?: emptyList()
//        val prosList = ordersViewModel.cartProducts.value ?: emptyList()
//        cartController.products = prosList
//        cartController.likes = likeList
//        cartController.setData(items)
//        concatAdapter = ConcatAdapter(cartController.adapter, PriceCardAdapter())
//        binding.cartRecyclerView.adapter = concatAdapter
////        binding.cartRecyclerView.adapter?.notifyDataSetChanged()
//    }
//



    private fun navigateToSelectAddress() {
//        findNavController().navigate(R.id.action_cartFragment_to_selectAddressFragment)
    }


    private fun showDeleteDialog(itemId: String) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getString(R.string.delete_dialog_title_text))
                .setMessage(getString(R.string.delete_cart_item_message_text))
                .setNegativeButton(getString(R.string.pro_cat_dialog_cancel_btn)) { dialog, _ ->
                    dialog.cancel()
                }
                // TODO: make delete item from cart require network
                .setPositiveButton(getString(R.string.delete_dialog_delete_btn_text)) { dialog, _ ->
                    viewModel.deleteItemFromCart(itemId)
                    dialog.cancel()
                }
                .show()
        }
    }
    inner class PriceCartController(): TypedEpoxyController<Map<String, Double>>() {


        override fun buildModels(data: Map<String, Double>) {
            val totalItemsPrice = viewModel.getItemsPriceTotal(data)
            val itemsCount = viewModel.getItemsCount()


            val shippingPrice = 0
            val importCharges = 0
            val totalPrice = 0



            priceCardLayout {
                id("price")
                itemsPriceTotal(totalItemsPrice)
                itemsCount(itemsCount)
            }


        }




//        priceCardBinding.priceItemsLabelTv.text = context?.getString(
//        R.string.price_card_items_string,
//        viewModel.getItemsCount().toString()
//        )
//        priceCardBinding.priceItemsAmountTv.text = context?.getString(R.string.price_text, viewModel.getItemsPriceTotal().toString())
//        priceCardBinding.priceShippingAmountTv.text = context?.getString(R.string.price_text, "0")
//        priceCardBinding.priceChargesAmountTv.text = context?.getString(R.string.price_text, "0")
//        priceCardBinding.priceTotalAmountTv.text = context?.getString(R.string.price_text, viewModel.getItemsPriceTotal().toString())



    }



//    inner class PriceCardAdapter : RecyclerView.Adapter<PriceCardAdapter.ViewHolder>() {
//
//        inner class ViewHolder(private val priceCardBinding: PriceCardLayoutBinding) :
//            RecyclerView.ViewHolder(priceCardBinding.root) {
//            fun bind() {
//                priceCardBinding.priceItemsLabelTv.text = context?.getString(
//                    R.string.price_card_items_string,
//                    viewModel.getItemsCount().toString()
//                )
//                priceCardBinding.priceItemsAmountTv.text = context?.getString(R.string.price_text, viewModel.getItemsPriceTotal().toString())
//                priceCardBinding.priceShippingAmountTv.text = context?.getString(R.string.price_text, "0")
//                priceCardBinding.priceChargesAmountTv.text = context?.getString(R.string.price_text, "0")
//                priceCardBinding.priceTotalAmountTv.text = context?.getString(R.string.price_text, viewModel.getItemsPriceTotal().toString())
//
//            }
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            return ViewHolder(PriceCardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            )
//        }
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.bind()
//        }
//
//        override fun getItemCount() = 1
//    }
}












//package com.shoppingapp.info.screens.cart
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.recyclerview.widget.ConcatAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import com.shoppingapp.info.R
//import com.shoppingapp.info.data.User
//import com.shoppingapp.info.databinding.*
//import com.shoppingapp.info.screens.home.HomeViewModel
//import com.shoppingapp.info.screens.orders.OrdersViewModel
//import com.shoppingapp.info.utils.StoreDataStatus
//import org.koin.android.viewmodel.ext.android.sharedViewModel
//
//
//class Cart : Fragment() {
//
//    companion object{
//        const val TAG = "Cart"
//    }
//
//    private val ordersViewModel by sharedViewModel<OrdersViewModel>()
//    private val homeViewModel by sharedViewModel<HomeViewModel>()
//    private lateinit var binding: CartBinding
//    private lateinit var itemsAdapter: CartItemAdapter
//    private lateinit var concatAdapter: ConcatAdapter
//
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//
//        binding = DataBindingUtil.inflate(inflater, R.layout.cart, container, false)
//
//
//        // TODO: improve the cart item design.
//
//        // todo here you will user just the data inside the cart of the user..
//
//
//
//
//        setViews()
//        setObservers()
//
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        ordersViewModel.getCartItems()
//    }
//
//    private fun setViews() {
//        binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
//        binding.loaderLayout.circularLoader.showAnimationBehavior
//        binding.cartAppBar.topAppBar.title = getString(R.string.cart_fragment_label)
//        binding.cartEmptyMessage.visibility = View.GONE
//        binding.cartCheckOutBtn.setOnClickListener {
//            navigateToSelectAddress()
//        }
//        if (context != null) {
//            setItemsAdapter(ordersViewModel.cartItems.value)
//            concatAdapter = ConcatAdapter(itemsAdapter, PriceCardAdapter())
//            binding.cartProductsRecyclerView.adapter = concatAdapter
//        }
//    }
//
//    private fun setObservers() {
//        ordersViewModel.dataStatus.observe(viewLifecycleOwner) { status ->
//            when (status) {
//                StoreDataStatus.LOADING -> {
//                    binding.cartProductsRecyclerView.visibility = View.GONE
//                    binding.cartCheckOutBtn.visibility = View.GONE
//                    binding.cartEmptyMessage.visibility = View.GONE
//                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
//                    binding.loaderLayout.circularLoader.showAnimationBehavior
//                }
//                else -> {
//                    binding.loaderLayout.circularLoader.hideAnimationBehavior
//                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                }
//            }
//        }
//        ordersViewModel.dataStatus.observe(viewLifecycleOwner) { status ->
//            if (status != null && status != StoreDataStatus.LOADING) {
//                ordersViewModel.cartProducts.observe(viewLifecycleOwner) { itemList ->
//                    if (itemList.isNotEmpty()) {
//                        updateAdapter()
//                        binding.cartEmptyMessage.visibility = View.GONE
//                        binding.cartProductsRecyclerView.visibility = View.VISIBLE
//                        binding.cartCheckOutBtn.visibility = View.VISIBLE
//                    } else if (itemList.isEmpty()) {
//                        binding.cartProductsRecyclerView.visibility = View.GONE
//                        binding.cartCheckOutBtn.visibility = View.GONE
//                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
//                        binding.loaderLayout.circularLoader.hideAnimationBehavior
//                        binding.cartEmptyMessage.visibility = View.VISIBLE
//                    }
//                }
//            }
//        }
//        ordersViewModel.cartItems.observe(viewLifecycleOwner) { items ->
//            if (items.isNotEmpty()) {
//                updateAdapter()
//            }
//        }
//        ordersViewModel.priceList.observe(viewLifecycleOwner) {
//            if (it.isNotEmpty()) {
//                updateAdapter()
//            }
//        }
//        homeViewModel.userLikes.observe(viewLifecycleOwner) {
//            if (it != null) {
//                updateAdapter()
//            }
//        }
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    private fun updateAdapter() {
//        val items = ordersViewModel.cartItems.value ?: emptyList()
//        val likeList = homeViewModel.userLikes.value ?: emptyList()
//        val prosList = ordersViewModel.cartProducts.value ?: emptyList()
//
//        Log.i(TAG,"items: ${items.size}")
//        Log.i(TAG,"likeList: ${likeList.size}")
//        Log.i(TAG,"productList: ${prosList.size}")
//        itemsAdapter.apply {
//            data = items
//            proList = prosList
//            likesList = likeList
//        }
//        concatAdapter = ConcatAdapter(itemsAdapter, PriceCardAdapter())
//        binding.cartProductsRecyclerView.adapter = concatAdapter
//        binding.cartProductsRecyclerView.adapter?.notifyDataSetChanged()
//    }
//
//    private fun setItemsAdapter(itemList: List<User.CartItem>?) {
//        val items = itemList ?: emptyList()
//        val likesList = homeViewModel.userLikes.value ?: emptyList()
//        val proList = ordersViewModel.cartProducts.value ?: emptyList()
//        itemsAdapter = CartItemAdapter(requireContext(), items, proList, likesList)
//        itemsAdapter.onClickListener = object : CartItemAdapter.OnClickListener {
//
//            // TODO: make the like require network
//            override fun onLikeClick(productId: String) {
//                Log.d(TAG, "onToggle Like Clicked")
//
//                homeViewModel.toggleLikeByProductId(productId)
////                viewModel.toggleLikeProduct(productId)
//
//            }
//
//            override fun onDeleteClick(itemId: String, itemBinding: CircularLoaderLayoutBinding) {
//                Log.d(TAG, "onDelete: initiated")
//                showDeleteDialog(itemId, itemBinding)
//            }
//
//            // TODO: make increase quantity require network
//            override fun onPlusClick(itemId: String) {
//
//                Log.d(TAG, "onPlus: Increasing quantity")
//                ordersViewModel.setQuantityOfItem(itemId, 1)
//            }
//
//            // TODO: make decrease quantity require network
//            override fun onMinusClick(itemId: String, currQuantity: Int,itemBinding: CircularLoaderLayoutBinding) {
//                Log.d(TAG, "onMinus: decreasing quantity")
//                if (currQuantity == 1) {
//                    showDeleteDialog(itemId, itemBinding)
//                } else {
//                    ordersViewModel.setQuantityOfItem(itemId, -1)
//                }
//            }
//        }
//    }
//
//    private fun navigateToSelectAddress() {
////        findNavController().navigate(R.id.action_cartFragment_to_selectAddressFragment)
//    }
//
//
//    private fun showDeleteDialog(itemId: String, itemBinding: CircularLoaderLayoutBinding) {
//        context?.let {
//            MaterialAlertDialogBuilder(it)
//                .setTitle(getString(R.string.delete_dialog_title_text))
//                .setMessage(getString(R.string.delete_cart_item_message_text))
//                .setNegativeButton(getString(R.string.pro_cat_dialog_cancel_btn)) { dialog, _ ->
//                    dialog.cancel()
//                    itemBinding.loaderFrameLayout.visibility = View.GONE
//                }
//                // TODO: make delete item from cart require network
//                .setPositiveButton(getString(R.string.delete_dialog_delete_btn_text)) { dialog, _ ->
//                    ordersViewModel.deleteItemFromCart(itemId)
//                    dialog.cancel()
//                }.setOnCancelListener {
//                    itemBinding.loaderFrameLayout.visibility = View.GONE
//                }
//                .show()
//        }
//    }
//
//    inner class PriceCardAdapter : RecyclerView.Adapter<PriceCardAdapter.ViewHolder>() {
//
//        inner class ViewHolder(private val priceCardBinding: PriceCardLayoutBinding) :
//            RecyclerView.ViewHolder(priceCardBinding.root) {
//            fun bind() {
//                priceCardBinding.priceItemsLabelTv.text = getString(
//                    R.string.price_card_items_string,
//                    ordersViewModel.getItemsCount().toString()
//                )
//                priceCardBinding.priceItemsAmountTv.text =
//                    getString(R.string.price_text, ordersViewModel.getItemsPriceTotal().toString())
//                priceCardBinding.priceShippingAmountTv.text = getString(R.string.price_text, "0")
//                priceCardBinding.priceChargesAmountTv.text = getString(R.string.price_text, "0")
//                priceCardBinding.priceTotalAmountTv.text =
//                    getString(R.string.price_text, ordersViewModel.getItemsPriceTotal().toString())
//            }
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            return ViewHolder(
//                PriceCardLayoutBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//            )
//        }
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.bind()
//        }
//
//        override fun getItemCount() = 1
//    }
//}