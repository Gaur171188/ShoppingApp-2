//package com.shoppingapp.info.screens.orders
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.shoppingapp.info.R
//import com.shoppingapp.info.data.User
//import com.shoppingapp.info.databinding.OrderSummaryCardLayoutBinding
//import java.time.Month
//import java.util.*
//
//class OrdersAdapter(ordersList: List<User.OrderItem>, private val context: Context) :
//	RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {
//
//	lateinit var onClickListener: OnClickListener
//	var data: List<User.OrderItem> = ordersList
//
//	inner class ViewHolder(private val binding: OrderSummaryCardLayoutBinding) :
//		RecyclerView.ViewHolder(binding.root) {
//		fun bind(order: User.OrderItem) {
//			binding.orderSummaryCard.setOnClickListener { onClickListener.onCardClick(order.orderId) }
//			binding.orderSummaryId.text = order.orderId
//			val calendar = Calendar.getInstance()
//			calendar.time = order.orderDate
//			binding.orderSummaryDateTv.text =
//				context.getString(
//					R.string.order_date_text,
//					Month.values()[(calendar.get(Calendar.MONTH))].name,
//					calendar.get(Calendar.DAY_OF_MONTH).toString(),
//					calendar.get(Calendar.YEAR).toString()
//				)
//			binding.orderSummaryStatusValueTv.text = order.status
//			val totalItems = order.items.map { it.quantity }.sum()
//			binding.orderSummaryItemsCountTv.text =
//				context.getString(R.string.order_items_count_text, totalItems.toString())
//			var totalAmount = 0.0
//			order.itemsPrices.forEach { (itemId, price) ->
//				totalAmount += price * (order.items.find { it.itemId == itemId }?.quantity ?: 1)
//			}
//			binding.orderSummaryTotalAmountTv.text =
//				context.getString(R.string.price_text, totalAmount.toString())
//		}
//	}
//
//	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//		return ViewHolder(
//			OrderSummaryCardLayoutBinding.inflate(
//				LayoutInflater.from(parent.context),
//				parent,
//				false
//			)
//		)
//	}
//
//	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//		holder.bind(data[position])
//	}
//
//	override fun getItemCount() = data.size
//
//	interface OnClickListener {
//		fun onCardClick(orderId: String)
//	}
//}