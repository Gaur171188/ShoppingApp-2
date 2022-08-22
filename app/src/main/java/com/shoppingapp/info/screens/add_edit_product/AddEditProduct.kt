package com.shoppingapp.info.screens.add_edit_product

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.shoppingapp.info.R
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.databinding.AddEditProductBinding
import com.shoppingapp.info.screens.home.HomeViewModel
import com.shoppingapp.info.utils.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.properties.Delegates



class AddEditProduct : Fragment() {

    companion object{
        private const val TAG = "AddEditProduct"
    }

    private lateinit var binding: AddEditProductBinding
    private val viewModel by sharedViewModel<AddEditProductViewModel>()
    private val homeViewModel by sharedViewModel<HomeViewModel>()
    private val focusChangeListener = MyOnFocusChangeListener()

    // arguments
    private var isEdit by Delegates.notNull<Boolean>()
    private lateinit var catName: String
    private lateinit var product: Product


    private var sizeList = mutableSetOf<Int>()
    private var colorsList = mutableSetOf<String>()
    private var imgList = mutableListOf<Uri>()

    private val getImages = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { result ->
            imgList.addAll(result)
            if (imgList.size > 3) {
                imgList = imgList.subList(0, 3)
                makeToast("Maximum 3 images are allowed!")
            }
            val adapter = context?.let { AddProductImagesAdapter(it, imgList) }
            binding.addImagesLayout.rvImages.adapter = adapter
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.add_edit_product,container,false)
//        viewModel = ViewModelProvider(this)[AddEditProductViewModel::class.java]



        initData()
        setViews()
        setObservers()



        return binding.root
    }


    private fun initData() {
        isEdit = arguments?.getBoolean(Constants.KEY_IS_EDIT) == true
//        catName = arguments?.getString(Constants.KEY_CATEGORY).toString()

        catName = try { arguments?.getString(Constants.KEY_CATEGORY).toString() }
        catch (ex: Exception){ ""}

        product = try { arguments?.getParcelable<Product>(Constants.KEY_PRODUCT)!! }
        catch (ex: Exception){ Product() }
    }


    private fun setObservers() {

        binding.apply {


            /** product update status **/
            homeViewModel.productUpdateStatus.observe(viewLifecycleOwner) { status ->
                if (status != null){
                    when (status) {
                        DataStatus.LOADING -> {
                            binding.loaderLayout.loaderFrameLayout.show()
                        }
                        DataStatus.SUCCESS -> {
                            binding.loaderLayout.loaderFrameLayout.hide()
                            homeViewModel.resetProgress()
                            findNavController().navigate(R.id.action_addProductFragment_to_homeFragment)
                        }
                        DataStatus.ERROR ->{
                            loaderLayout.loaderFrameLayout.hide()
                        }
                    }
                }
            }


            /** product update status **/
            homeViewModel.productDeleteStatus.observe(viewLifecycleOwner) { status ->
                if (status != null){
                    when (status) {
                        DataStatus.LOADING -> {
                            binding.loaderLayout.loaderFrameLayout.show()
                        }
                        DataStatus.SUCCESS -> {
                            binding.loaderLayout.loaderFrameLayout.hide()
                            homeViewModel.resetProgress()
                            findNavController().navigate(R.id.action_addProductFragment_to_homeFragment)
                        }
                        DataStatus.ERROR -> {
                            loaderLayout.loaderFrameLayout.hide()
                        }
                    }
                }
            }



            /** product submit status **/
            homeViewModel.productSubmitStatus.observe(viewLifecycleOwner) { status ->
                if (status != null){
                    when (status) {
                        DataStatus.LOADING -> {
                            binding.loaderLayout.loaderFrameLayout.show()
                        }
                        DataStatus.SUCCESS -> {
                            binding.loaderLayout.loaderFrameLayout.hide()
                            homeViewModel.resetProgress()
                            findNavController().navigate(R.id.action_addProductFragment_to_homeFragment)
                        }
                        DataStatus.ERROR -> {
                            loaderLayout.loaderFrameLayout.hide()
                        }
                    }
                }

            }


        }



    }



    // todo put this in data binding
    private fun fillDataInAllViews() {
        binding.apply {
            addEditProductAppBar.topAppBar.title = "Edit Product - ${product.name}"
            productName.setText(product.name)
            productPrice.setText(product.price.toString())
            productMrp.setText(product.mrp.toString())
            productDescription.setText(product.description)


            imgList = product.images.map { it.toUri() } as MutableList<Uri>
            val adapter = AddProductImagesAdapter(requireContext(), imgList)
            addImagesLayout.rvImages.adapter = adapter

            btnAddEditProduct.text = "Update"
            btnDeleteProduct.show()
//
            setColors(product.availableColors)
            setSizes(product.availableSizes)

        }
    }

    private fun setViews() {
        binding.apply {
            if (!isEdit) { // add new product
                addEditProductAppBar.topAppBar.title = "Add Product - ${product.category}"

                addImagesLayout.addImagesLabel.text = "Add Images (Max 3)"
                addSizes.labelChipGroup.text = "Available Sizes"
                addColors.labelChipGroup.text = "Available Colors"

                val adapter = AddProductImagesAdapter(requireContext(), imgList)
                addImagesLayout.rvImages.adapter = adapter
                btnAddEditProduct.text = "Add Product"

                setSizes()
                setColors()

            }else {
                fillDataInAllViews()
            }
            addImagesLayout.btnAddImage.setOnClickListener {
                getImages.launch("image/*")
            }


            /** button back **/
            addEditProductAppBar.topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            loaderLayout.loaderFrameLayout.visibility = View.GONE



            errorMessage.visibility = View.GONE
            productName.onFocusChangeListener = focusChangeListener
            productPrice.onFocusChangeListener = focusChangeListener
            productMrp.onFocusChangeListener = focusChangeListener
            productDescription.onFocusChangeListener = focusChangeListener

            /** button add edit product **/
            btnAddEditProduct.setOnClickListener {
                onAddProduct()
            }

            /** button delete product **/
            btnDeleteProduct.setOnClickListener {
                homeViewModel.deleteProduct(product)
            }


        }


    }

    private fun onAddProduct() {
        binding.apply {
            val name = productName.text.toString()
            val price = productPrice.text.toString().toDoubleOrNull()
            val mrp = productMrp.text.toString().toDoubleOrNull()
            val desc = productDescription.text.toString()

            if (name.isEmpty()){
                productName.error = "Product Name required!"
                productName.requestFocus()
            }
            if (price.toString().isEmpty()){
                productPrice.error = "Price required!"
                productPrice.requestFocus()
            }
            if (mrp.toString().isEmpty()){
                productMrp.error = "Mrc required!"
                productMrp.requestFocus()
            }
            if (desc.isEmpty()){
                productDescription.error = "Description required!"
                productDescription.requestFocus()
            }
            if (colorsList.isEmpty()){
                showMessage(requireContext(),"colors required!")
            }
            if (sizeList.isEmpty()){
                showMessage(requireContext(), "sizes required!")
            }
            if (imgList.isEmpty() || imgList.size <= 0) {
                showMessage(requireContext(), "one image at least required!")
            }
            else {
                val user = homeViewModel.userData.value!!
                val userId = user.userId
                val country = user.country
                val newProduct = Product(getProductId(userId),
                    name,
                    userId,
                    desc,
                    catName,
                    price ?: 0.0,
                    mrp ?: 0.0 ,
                    sizeList.toList(),
                    colorsList.toList(),
                    country = country
                )
                if (!isEdit){ // insert new product
                    homeViewModel.submitProduct(newProduct,imgList)
                }else{ // update product
                    product.name = name
                    product.mrp = mrp!!
                    product.description = desc
                    product.availableColors = colorsList.toList()
                    product.availableSizes = sizeList.toList()
                    product.price = price!!.toDouble()
                    homeViewModel.updateProduct(product,imgList,product.images)
                }

            }

        }

    }

    private fun setSizes(sizes: List<Int?> = emptyList()) {
        binding.addSizes.addChipGroup.apply {
            removeAllViews()
            for ((k, v) in shoeSizes) {
                val chip = Chip(context)
                chip.id = v
                chip.tag = v

                chip.text = "$v"
                chip.isCheckable = true


                if (sizes.contains(v)) {
                    chip.isChecked = true
                    sizeList.add(chip.tag.toString().toInt())
                }



                /** select size button **/
                chip.setOnCheckedChangeListener { buttonView, isChecked ->
                    val tag = buttonView.tag.toString().toInt()
                    if (!isChecked) {
                        sizeList.remove(tag)
                    } else {
                        sizeList.add(tag)
                    }
                }
                addView(chip)
            }
            invalidate()
        }
    }



    private fun setColors(colors: List<String?> = emptyList()) {
        binding.addColors.addChipGroup.apply {
            removeAllViews()
            var ind = 1
            for ((k, v) in shoeColors) {
                val chip = Chip(context)
                chip.id = ind
                chip.tag = k

                chip.chipStrokeColor = ColorStateList.valueOf(Color.BLACK)
                chip.chipStrokeWidth = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    1F,
                    context.resources.displayMetrics
                )
                chip.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(v))
                chip.isCheckable = true


                if (colors.contains(k)) {
                    chip.isChecked = true
                    colorsList.add(chip.tag.toString())
                }



                /** select color button **/
                chip.setOnCheckedChangeListener { buttonView, isChecked ->
                    val tag = buttonView.tag.toString()
                    if (!isChecked) {
                        colorsList.remove(tag)
                    } else {
                        colorsList.add(tag)
                    }
                }
                addView(chip)
                ind++
            }
            invalidate()
        }
    }



    private fun makeToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}