package com.shoppingapp.info.utils

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.shoppingapp.info.data.User.Address
import  com.shoppingapp.info.data.User.CartItem
import  com.shoppingapp.info.data.User.OrderItem

class ObjectListTypeConvertor {


	@TypeConverter
	fun stringToAddressObjectList(data: String?): List<Address> {
		if (data.isNullOrBlank()) {
			return emptyList()
		}
		val listType = object : TypeToken<List<Address>>() {}.type
		val gson = Gson()
		return gson.fromJson(data, listType)
	}

	@TypeConverter
	fun addressObjectListToString(addressList: List<Address>): String {
		if (addressList.isEmpty()) {
			return ""
		}
		val gson = Gson()
		val listType = object : TypeToken<List<Address>>() {}.type
		return gson.toJson(addressList, listType)
	}


	@TypeConverter
	fun stringToCartObjectList(data: String?): List<CartItem> {
		if (data.isNullOrBlank()) {
			return emptyList()
		}
		val listType = object : TypeToken<List<CartItem>>() {}.type
		val gson = Gson()
		return gson.fromJson(data, listType)
	}

	@TypeConverter
	fun cartObjectListToString(cartList: List<CartItem>): String {
		if (cartList.isEmpty()) {
			return ""
		}
		val gson = Gson()
		val listType = object : TypeToken<List<CartItem>>() {}.type
		return gson.toJson(cartList, listType)
	}

	@TypeConverter
	fun stringToOrderObjectList(data: String?): List<OrderItem> {
		if (data.isNullOrBlank()) {
			return emptyList()
		}
		val listType = object : TypeToken<List<OrderItem>>() {}.type
		val gson = Gson()
		return gson.fromJson(data, listType)
	}

	@TypeConverter
	fun orderObjectListToString(orderList: List<OrderItem>): String {
		if (orderList.isEmpty()) {
			return ""
		}
		val gson = Gson()
		val listType = object : TypeToken<List<OrderItem>>() {}.type
		return gson.toJson(orderList, listType)
	}
}