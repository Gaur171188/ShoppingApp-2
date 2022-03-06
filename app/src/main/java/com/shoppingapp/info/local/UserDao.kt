package com.shoppingapp.info.local

import androidx.room.*
import com.shoppingapp.info.data.UserData


@Dao
interface UserDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(uData: UserData)

	@Query("SELECT * FROM users WHERE userId = :userId")
	suspend fun getById(userId: String): UserData?

	@Query("SELECT * FROM users WHERE phone = :phone")
	suspend fun getByMobile(phone: String): UserData?

	@Update(entity = UserData::class)
	suspend fun updateUser(obj: UserData)

	@Query("DELETE FROM users")
	suspend fun clear()
}