package com.shoppingapp.info.screens.cart
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shoppingapp.info.R
import com.shoppingapp.info.data.UserData
import com.shoppingapp.info.databinding.*
import com.shoppingapp.info.screens.orders.OrdersViewModel
import com.shoppingapp.info.utils.StoreDataStatus



class Cart : Fragment() {

    companion object{
        const val TAG = "Cart"
    }

    private lateinit var viewModel: OrdersViewModel
    private lateinit var binding: CartBinding
    private lateinit var itemsAdapter: CartItemAdapter
    private lateinit var concatAdapter: ConcatAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.cart, container, false)

        viewModel = ViewModelProvider(this)[OrdersViewModel::class.java]


        setViews()
        setObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserLikes()
        viewModel.getCartItems()
    }

    private fun setViews() {
        binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
        binding.loaderLayout.circularLoader.showAnimationBehavior
        binding.cartAppBar.topAppBar.title = getString(R.string.cart_fragment_label)
        binding.cartEmptyMessage.visibility = View.GONE
        binding.cartCheckOutBtn.setOnClickListener {
            navigateToSelectAddress()
        }
        if (context != null) {
            setItemsAdapter(viewModel.cartItems.value)
            concatAdapter = ConcatAdapter(itemsAdapter, PriceCardAdapter())
            binding.cartProductsRecyclerView.adapter = concatAdapter
        }
    }

    private fun setObservers() {
        viewModel.dataStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                StoreDataStatus.LOADING -> {
                    binding.cartProductsRecyclerView.visibility = View.GONE
                    binding.cartCheckOutBtn.visibility = View.GONE
                    binding.cartEmptyMessage.visibility = View.GONE
                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                    binding.loaderLayout.circularLoader.showAnimationBehavior
                }
                else -> {
                    binding.loaderLayout.circularLoader.hideAnimationBehavior
                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                }
            }
        }
        viewModel.dataStatus.observe(viewLifecycleOwner) { status ->
            if (status != null && status != StoreDataStatus.LOADING) {
                viewModel.cartProducts.observe(viewLifecycleOwner) { itemList ->
                    if (itemList.isNotEmpty()) {
                        updateAdapter()
                        binding.cartEmptyMessage.visibility = View.GONE
                        binding.cartProductsRecyclerView.visibility = View.VISIBLE
                        binding.cartCheckOutBtn.visibility = View.VISIBLE
                    } else if (itemList.isEmpty()) {
                        binding.cartProductsRecyclerView.visibility = View.GONE
                        binding.cartCheckOutBtn.visibility = View.GONE
                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                        binding.loaderLayout.circularLoader.hideAnimationBehavior
                        binding.cartEmptyMessage.visibility = View.VISIBLE
                    }
                }
            }
        }
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            if (items.isNotEmpty()) {
                updateAdapter()
            }
        }
        viewModel.priceList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                updateAdapter()
            }
        }
        viewModel.userLikes.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                updateAdapter()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapter() {
        val items = viewModel.cartItems.value ?: emptyList()
        val likeList = viewModel.userLikes.value ?: emptyList()
        val prosList = viewModel.cartProducts.value ?: emptyList()

        Log.i(TAG,"items: ${items.size}")
        Log.i(TAG,"likeList: ${likeList.size}")
        Log.i(TAG,"productList: ${prosList.size}")
        itemsAdapter.apply {
            data = items
            proList = prosList
            likesList = likeList
        }
        concatAdapter = ConcatAdapter(itemsAdapter, PriceCardAdapter())
        binding.cartProductsRecyclerView.adapter = concatAdapter
        binding.cartProductsRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun setItemsAdapter(itemList: List<UserData.CartItem>?) {
        val items = itemList ?: emptyList()
        val likesList = viewModel.userLikes.value ?: emptyList()
        val proList = viewModel.cartProducts.value ?: emptyList()
        itemsAdapter = CartItemAdapter(requireContext(), items, proList, likesList)
        itemsAdapter.onClickListener = object : CartItemAdapter.OnClickListener {

            // TODO: make the like require network
            override fun onLikeClick(productId: String) {
                Log.d(TAG, "onToggle Like Clicked")
                viewModel.toggleLikeProduct(productId)
            }

            override fun onDeleteClick(itemId: String, itemBinding: CircularLoaderLayoutBinding) {
                Log.d(TAG, "onDelete: initiated")
                showDeleteDialog(itemId, itemBinding)
            }

            // TODO: make increase quantity require network
            override fun onPlusClick(itemId: String) {

                Log.d(TAG, "onPlus: Increasing quantity")
                viewModel.setQuantityOfItem(itemId, 1)
            }

            // TODO: make decrease quantity require network
            override fun onMinusClick(itemId: String, currQuantity: Int,itemBinding: CircularLoaderLayoutBinding) {
                Log.d(TAG, "onMinus: decreasing quantity")
                if (currQuantity == 1) {
                    showDeleteDialog(itemId, itemBinding)
                } else {
                    viewModel.setQuantityOfItem(itemId, -1)
                }
            }
        }
    }

    private fun navigateToSelectAddress() {
//        findNavController().navigate(R.id.action_cartFragment_to_selectAddressFragment)
    }


    private fun showDeleteDialog(itemId: String, itemBinding: CircularLoaderLayoutBinding) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getString(R.string.delete_dialog_title_text))
                .setMessage(getString(R.string.delete_cart_item_message_text))
                .setNegativeButton(getString(R.string.pro_cat_dialog_cancel_btn)) { dialog, _ ->
                    dialog.cancel()
                    itemBinding.loaderFrameLayout.visibility = View.GONE
                }
                // TODO: make delete item from cart require network
                .setPositiveButton(getString(R.string.delete_dialog_delete_btn_text)) { dialog, _ ->
                    viewModel.deleteItemFromCart(itemId)
                    dialog.cancel()
                }.setOnCancelListener {
                    itemBinding.loaderFrameLayout.visibility = View.GONE
                }
                .show()
        }
    }

    inner class PriceCardAdapter : RecyclerView.Adapter<PriceCardAdapter.ViewHolder>() {

        inner class ViewHolder(private val priceCardBinding: PriceCardLayoutBinding) :
            RecyclerView.ViewHolder(priceCardBinding.root) {
            fun bind() {
                priceCardBinding.priceItemsLabelTv.text = getString(
                    R.string.price_card_items_string,
                    viewModel.getItemsCount().toString()
                )
                priceCardBinding.priceItemsAmountTv.text =
                    getString(R.string.price_text, viewModel.getItemsPriceTotal().toString())
                priceCardBinding.priceShippingAmountTv.text = getString(R.string.price_text, "0")
                priceCardBinding.priceChargesAmountTv.text = getString(R.string.price_text, "0")
                priceCardBinding.priceTotalAmountTv.text =
                    getString(R.string.price_text, viewModel.getItemsPriceTotal().toString())
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                PriceCardLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind()
        }

        override fun getItemCount() = 1
    }
}