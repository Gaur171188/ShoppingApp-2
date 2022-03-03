package com.shoppingapp.info.screens.registration


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*


class RegistrationViewModel : ViewModel() {

    companion object{
        const val TAG = "Registration"
    }

//    private val repository = UserRepositoryOnline()
    private val scopeIO = CoroutineScope(Dispatchers.IO + Job())

    private var e = ""

    /** live data **/
    private val _inProgress = MutableLiveData<Boolean>()
    val inProgress: LiveData<Boolean> = _inProgress

    /** live data **/
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

//    /** live data **/
//    private val _isRegistered = MutableLiveData<User?>()
//    val isRegistered: LiveData<User?> = _isRegistered
//

    // firebase fire store
    private val _root = FirebaseFirestore.getInstance()
    private val _usersPath = _root.collection("users")

    // firebase authentication
    private val _auth = FirebaseAuth.getInstance()





//    fun registration(user: User){
//        _inProgress.value = true
//        try {
//            isUserExist(user.email){
//                if (it){ // user is exist
//                    _inProgress.value = false
//                    _error.value = "User is Exist!"
//                }else{ // user is not exist
//                    scopeIO.launch {
//                        createUserAccount(user)
//                    }
//                }
//            }
//
//
//        }catch (ex: Exception){
//            Log.i(TAG,ex.message.toString())
//            _inProgress.value = false
//        }
//    }


//    private fun createUserAccount(user: User){
//        scopeIO.launch {
//            _auth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener { it->
//                if (it.isComplete){
//                    Log.i(TAG,"create new account is done!")
//                    sendEmailVerify().addOnCompleteListener { task ->
//                        if(task.isComplete){
//                            Log.i(TAG,"send email verify has been done!")
//                            val userId = FirebaseAuth.getInstance().currentUser!!.uid
//                            user.id = userId
//                            addNewUser(user)
//                            _isRegistered.value = user
//                        }else{
//                            val error = it.exception!!.message.toString()
//                            _error.value = error
//                            _inProgress.value = false
//                            Log.i("server",error)
//                        }
//                    }
//
//                }else{
//                    _inProgress.value = false
//                    val error = it.exception!!.message.toString()
//                    _error.value = error
//                    Log.i("server",error)
//                }
//            }
//        }
//    }

//    private fun isUserExist(email: String,isExist:(Boolean) -> Unit){
//        try {
//            scopeIO.launch {
//                repository.getUsers(_usersPath).get().addOnSuccessListener { documents ->
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
//                        Log.i(TAG,"user is not exist!")
//                    }
//                }
//            }
//        }catch (ex:Exception){
//            Log.i(TAG,ex.message.toString())
//            _inProgress.value = false
//        }
//    }
//

    private fun sendEmailVerify() = _auth.currentUser!!.sendEmailVerification()




//    private fun addNewUser(data: Any){
//        scopeIO.launch {
//            val userPath = _usersPath.document(_auth.currentUser!!.uid)
//            repository.addNewUser(userPath,data).addOnCompleteListener { task ->
//                if (task.isComplete){ // new user has been added
//                    _inProgress.value = false
//                    Log.i("Server","new user has been added!")
//                }else{
//                    val error = task.exception!!.message.toString()
//                    _error.value = error
//                    _inProgress.value = false
//                    Log.i("server",task.exception!!.message.toString())
//                }
//            }
//        }
//    }


    override fun onCleared() {
        super.onCleared()

        scopeIO.cancel()
    }
}