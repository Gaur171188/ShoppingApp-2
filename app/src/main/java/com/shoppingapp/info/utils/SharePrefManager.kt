package com.shoppingapp.info.utils

import android.content.Context
import android.content.SharedPreferences

class SharePrefManager(context: Context) {

	var userSession: SharedPreferences =
		context.getSharedPreferences("userSessionData", Context.MODE_PRIVATE)
	var editor: SharedPreferences.Editor = userSession.edit()


	fun createLoginSession(
		id: String,
		name: String,
		phone: String,
		isRemOn: Boolean,
		isSeller: Boolean
	) {
		editor.putBoolean(IS_LOGIN, true)
		editor.putString(KEY_ID, id)
		editor.putString(KEY_NAME, name)
		editor.putString(KEY_PHONE, phone)
		editor.putBoolean(KEY_REMEMBER_ME, isRemOn)
		editor.putBoolean(KEY_IS_SELLER, isSeller)


		editor.commit()
	}



	fun isUserSeller(): Boolean = userSession.getBoolean(KEY_IS_SELLER, false)

	fun isRememberMeOn(): Boolean = userSession.getBoolean(KEY_REMEMBER_ME, false)

	fun getPhoneNumber(): String? = userSession.getString(KEY_PHONE, null)

	fun getUserDataFromSession(): HashMap<String, String?> {
		return hashMapOf(
			KEY_ID to userSession.getString(KEY_ID, null),
			KEY_NAME to userSession.getString(KEY_NAME, null),
			KEY_PHONE to userSession.getString(KEY_PHONE, null)
		)
	}


	fun getUserIdFromSession(): String? = userSession.getString(KEY_ID, "")

	fun isLoggedIn(): Boolean = userSession.getBoolean(IS_LOGIN, false)

	fun signOut() {
		editor.clear()
		editor.commit()
	}

	companion object {
		private const val IS_LOGIN = "isLoggedIn"
		private const val KEY_NAME = "userName"
		private const val KEY_PHONE = "userMobile"
		private const val KEY_ID = "userId"
		private const val KEY_REMEMBER_ME = "isRemOn"
		private const val KEY_IS_SELLER = "isSeller"
		private const val KEY_FAV_PRODUCTS_IDs = "favoriteProductsIds"
	}
}