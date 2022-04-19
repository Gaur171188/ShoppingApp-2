package com.shoppingapp.info.utils

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.shoppingapp.info.data.User

class ObjectListTypeConvertor {


	@TypeConverter
	fun stringToCartObjectList(data: String?): List<User.CartItem> {
		if (data.isNullOrBlank()) {
			return emptyList()
		}
		val listType = object : TypeToken<List<User.CartItem>>() {}.type
		val gson = Gson()
		return gson.fromJson(data, listType)
	}

	@TypeConverter
	fun cartObjectListToString(cartList: List<User.CartItem>): String {
		if (cartList.isEmpty()) {
			return ""
		}
		val gson = Gson()
		val listType = object : TypeToken<List<User.CartItem>>() {}.type
		return gson.toJson(cartList, listType)
	}

	@TypeConverter
	fun stringToOrderObjectList(data: String?): List<User.OrderItem> {
		if (data.isNullOrBlank()) {
			return emptyList()
		}
		val listType = object : TypeToken<List<User.OrderItem>>() {}.type
		val gson = Gson()
		return gson.fromJson(data, listType)
	}

	@TypeConverter
	fun orderObjectListToString(orderList: List<User.OrderItem>): String {
		if (orderList.isEmpty()) {
			return ""
		}
		val gson = Gson()
		val listType = object : TypeToken<List<User.OrderItem>>() {}.type
		return gson.toJson(orderList, listType)
	}
}