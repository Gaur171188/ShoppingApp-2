package com.shoppingapp.info.local.api

import androidx.room.*
import com.shoppingapp.info.data.User


@Dao
interface UserApi {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(u: User)

	@Query("SELECT * FROM user WHERE userId = :userId")
	suspend fun getUserById(userId: String): User?

	@Query("SELECT * FROM user WHERE phone = :phone")
	suspend fun getUserByMobile(phone: String): User?

	@Update(entity = User::class)
	suspend fun updateUser(obj: User)

	@Query("DELETE FROM user")
	suspend fun deleteAllUsers()

	@Query("delete from user where :userId")
	suspend fun deleteUserById(userId: String)

}