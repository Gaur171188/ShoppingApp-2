package com.shoppingapp.info.screens.profile

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.robertlevonyan.components.picker.*
import com.shoppingapp.info.R
import com.shoppingapp.info.data.User
import com.shoppingapp.info.databinding.ProfileBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.DataStatus
import com.shoppingapp.info.utils.showMessage
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.net.URI


class Profile: Fragment() {

    companion object{
        const val TAG = "Profile"
    }

    private lateinit var binding: ProfileBinding
    private val homeViewModel: HomeViewModel by sharedViewModel()
    private lateinit var viewModel: ProfileViewModel
    private var imageUri: Uri? = null
    private var user: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.profile, container, false)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        initData()
        setViews()
        setObserves()






        return binding.root

    }

    private fun setViews() {
        binding.apply {

            userData = user

            /** profile label **/
            profileTopAppBar.topAppBar.title = getString(R.string.profile)

            /** button back **/
            profileTopAppBar.topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }


            /** button select image profile **/
            btnImageAccount.setOnClickListener {
                selectFromMedia()
            }


        }

    }

    private fun initData() {
        val data = homeViewModel.userData.value!!
        user = data
    }


//    private fun pdfPickIntent() {
//        val intent = Intent()
//        intent.type="image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        pdfActivityResultLauncher.launch(intent)
//    }
//
//    private val pdfActivityResultLauncher = registerForActivityResult (
//        ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
//            imageUri = result.data!!.data
//
//            binding.btnImageAccount.setImageURI(imageUri)
//
////            homeViewModel.updateImageProfile(imageUri)
//
//
//        } else {
//            // cancel picked.
//        }
//    }



    private fun selectFromMedia() {
        val items = setOf(
            ItemModel(
                ItemType.Camera,
                backgroundType = ShapeType.TYPE_ROUNDED_SQUARE,
                itemBackgroundColor = Color.RED),
            ItemModel(ItemType.ImageGallery()),

        )

        // in fragment or activity
        pickerDialog {
            setTitle("select media")          // String value or resource ID
            setTitleTextSize(20F)  // Text size of title
            setTitleTextColor(R.color.black) // Color of title text
            setListType(PickerDialog.ListType.TYPE_LIST)       // Type of the picker, must be PickerDialog.TYPE_LIST or PickerDialog.TYPE_Grid
            setItems(items)          // List of ItemModel-s which should be in picker
        }
            .setPickerCloseListener { type: ItemType, uris: List<Uri> ->

                // Getting the result
                when (type) {
                    is ItemType.Camera -> {
//                        homeViewModel.updateImageProfile(uris.first())
                        showMessage(requireContext(),uris.first().toString())
                    } // photo you've taken.
                    is ItemType.ImageGallery -> {   showMessage(requireContext(),uris.size.toString()) } /* images you've chosen */
                    else -> {}
                }
            }.show()

    }

    private fun setObserves() {

//        /** live data user data **/
//        homeViewModel.userData.observe(viewLifecycleOwner) {
//            if (it != null) {
//                binding.userName.text = it.name
//                binding.email.text = it.email
//                binding.phone.text = it.phone
//            }
//        }

//
//        homeViewModel.dataStatus.observe(viewLifecycleOwner){
//            if (it != null){
//                when(it){
//                    DataStatus.LOADING ->{
//                        binding.loader.root.visibility = View.VISIBLE
//                        binding.profileLayout.visibility = View.GONE
//                    }
//                    DataStatus.SUCCESS ->{
//                        binding.loader.root.visibility = View.GONE
//                        binding.profileLayout.visibility = View.VISIBLE
//                    }
//                    DataStatus.ERROR ->{
//
//                    }
//                }
//            }
//        }


    }

}