//package com.shoppingapp.info.screens.users
//
//import com.airbnb.epoxy.TypedEpoxyController
//import com.shoppingapp.info.data.User
//import com.shoppingapp.info.user
//import com.shoppingapp.info.utils.UserType
//
//
//class UserController(): TypedEpoxyController<List<User>>() {
//
//    lateinit var clickListener: OnClickListener
//
//
//    override fun buildModels(list: List<User>?) {
//
//        list?.forEachIndexed { index , user ->
//
//            val isCustomer = user.userType == UserType.CUSTOMER.name
//            user {
//                id(user.userId)
//                user(user)
//                isCustomer(isCustomer)
//
//
//            }
//
//
//            }
//
//        }
//
//
//
//    interface OnClickListener {
//        fun onUserClicked(user: User, index: Int)
//    }
//
//
//    }
//
//
//
