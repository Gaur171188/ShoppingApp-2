package com.shoppingapp.info.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.data.UserData
import com.shoppingapp.info.utils.DateTypeConvertors
import com.shoppingapp.info.utils.ListTypeConverter
import com.shoppingapp.info.utils.ObjectListTypeConvertor


@Database(entities = [UserData::class, Product::class], version = 4)
@TypeConverters(ListTypeConverter::class, ObjectListTypeConvertor::class, DateTypeConvertors::class)
abstract class ShoppingAppDatabase : RoomDatabase() {
	abstract fun userDao(): UserDao
	abstract fun productsDao(): ProductsDao

	companion object {
		@Volatile
		private var INSTANCE: ShoppingAppDatabase? = null



		fun getInstance(context: Context): ShoppingAppDatabase =
			INSTANCE ?: synchronized(this) {
				INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
			}

		private fun buildDatabase(context: Context) =
			Room.databaseBuilder(
				context.applicationContext,
				ShoppingAppDatabase::class.java, "ShoppingAppDb"
			)
				.fallbackToDestructiveMigration()
				.allowMainThreadQueries()
				.build()


	}
}