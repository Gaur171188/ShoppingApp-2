package com.shoppingapp.info.screens.login


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*

class LoginViewModel : ViewModel() {

    companion object{
        const val TAG = "Login"
    }

//    private val userRepositoryOnline = UserRepositoryOnline()
    private val scopeIO = CoroutineScope(Dispatchers.IO + Job())

    private var e: String = ""

    /** live data **/
    private val _inProgress = MutableLiveData<Boolean>()
    val inProgress: LiveData<Boolean> = _inProgress


    /** live data **/
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /** live data **/
    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin

    // firebase fire store
    private val _root = FirebaseFirestore.getInstance()
    private val _usersPath = _root.collection("users")

    // firebase authentication
    private val _auth = FirebaseAuth.getInstance()



//    fun login(email: String , password: String){
//        _inProgress.value = true
//        _error.value = null
//        scopeIO.launch {
//            try {
//                isUserExist(email){ isUserExist->
//                    if (isUserExist){ // user is exist
//                        scopeIO.launch {
//                            _auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task->
//                                if(task.isComplete){
//                                    val id = _auth.currentUser?.uid
//                                    if(id != null){
//                                        scopeIO.launch {
//                                            if (_auth.currentUser?.isEmailVerified!!){
//                                                userRepositoryOnline.getUser(_usersPath.document(id)).get().addOnSuccessListener {
//                                                    val user = it.toObject(User::class.java)
//                                                    if (user?.password == password){
//                                                        _inProgress.value = false
//                                                        _isLogin.value = true
//                                                        Log.i(TAG,"login is successfully!")
//                                                    }
//                                                }
//                                            }else{
//                                                withContext(Dispatchers.Main){
//                                                    _inProgress.value = false
//                                                    _error.value = "email is not verify!"
//                                                    Log.i(TAG, "email is not verify!")
//                                                }
//                                            }
//                                        }
//                                    }else{
//                                        val error = task.exception!!.message.toString()
//                                        _inProgress.value = false
//                                        _error.value = "password is incorrect!"
//                                        Log.i(TAG,error)
//                                    }
//
//                                }else{
//                                    val error = task.exception!!.message.toString()
//                                    _error.value = error
//                                    _inProgress.value = false
//                                    Log.i("server",error)
//                                }
//                            }
//
//                        }
//
//                    }else{ // user is not exist!
//                        _inProgress.value = false
//                        _error.value = "user is not exist!"
//                        Log.e(TAG,"user is not exist!")
//                    }
//
//                }
//
//            }catch (ex: Exception){
//                val error = ex.message.toString()
//                _inProgress.value = false
//                _error.value = error
//                Log.e(TAG,error)
//            }
//
//        }
//    }
//


//    private fun isUserExist(email: String,isExist:(Boolean) -> Unit){
//        try {
//            scopeIO.launch {
//                userRepositoryOnline.getUsers(_usersPath).get().addOnSuccessListener { documents ->
//                    val users = documents.toObjects(User::class.java)
//                    users.forEach { user ->
//                        if (user.email == email){
//                            e = email
//                        }
//                    }
//                    if (e == email){
//                        isExist(true) // user is exist
//                        Log.i(TAG,"user is exist!")
//                    }else{
//                        isExist(false) // user is not exist
//                    }
//                }
//            }
//        }catch (ex:Exception){
//            Log.i(TAG,ex.message.toString())
//            _inProgress.value = false
//        }
//    }



    override fun onCleared() {
        super.onCleared()



        scopeIO.cancel()
    }


}