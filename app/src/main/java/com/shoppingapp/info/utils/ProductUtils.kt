package com.shoppingapp.info.utils

import java.util.*

val ShoeSizes = mapOf(
	"UK4" to 4,
	"UK5" to 5,
	"UK6" to 6,
	"UK7" to 7,
	"UK8" to 8,
	"UK9" to 9,
	"UK10" to 10,
	"UK11" to 11,
	"UK12" to 12
)

val ShoeColors = mapOf(
	"black" to "#000000",
	"white" to "#FFFFFF",
	"red" to "#FF0000",
	"green" to "#00FF00",
	"blue" to "#0000FF",
	"yellow" to "#FFFF00",
	"cyan" to "#00FFFF",
	"magenta" to "#FF00FF"
)




val sortOrderItems = arrayOf(
	Sort.NAME.name.lowercase(),
	Sort.DATE.name.lowercase(),
	OrderStatus.CONFIRMED.name.lowercase(),
	OrderStatus.BINDING.name.lowercase(),
	OrderStatus.ARRIVING.name.lowercase(),
	OrderStatus.DELIVERED.name.lowercase(),
	OrderStatus.REJECTED.name.lowercase()
)

//val prices = arrayOf(
//	Prices.LESS_THAN_100.name.lowercase(Locale.ROOT),
//	Prices.FROM_50_TO_250.name.lowercase(Locale.ROOT),
//	Prices.FROM_500_TO_1500.name.lowercase(Locale.ROOT),
//	Prices.MORE_THAN_1500.name.lowercase(Locale.ROOT)
//)