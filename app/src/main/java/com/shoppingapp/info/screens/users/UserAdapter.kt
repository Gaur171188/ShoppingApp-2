package com.shoppingapp.info.screens.users

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.ItemUserBinding
import com.shoppingapp.info.utils.UserType

class UserAdapter() : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

	lateinit var onClickListener: OnClickListener
	var data: List<User> = emptyList()

	inner class ViewHolder(private val binding: ItemUserBinding) :
		RecyclerView.ViewHolder(binding.root) {
		fun bind(data: User) {
            binding.apply {
                val mIsCustomer = data.userType == UserType.CUSTOMER.name
				user = data
                isCustomer = mIsCustomer
				btnItem.setOnClickListener {
					onClickListener.onItemClicked(data,adapterPosition)
				}
				binding.executePendingBindings()
            }

		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(
			ItemUserBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(data[position])
	}

	override fun getItemCount() = data.size



	@SuppressLint("NotifyDataSetChanged")
	fun putData(data: List<User>) {
		this.data = data
		notifyDataSetChanged()
	}

	interface OnClickListener {
		fun onItemClicked(user: User, index: Int)
	}

}