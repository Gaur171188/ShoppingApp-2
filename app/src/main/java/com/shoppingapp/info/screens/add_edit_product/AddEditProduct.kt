package com.shoppingapp.info.screens.add_edit_product

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.shoppingapp.info.R
import com.shoppingapp.info.ShoeColors
import com.shoppingapp.info.ShoeSizes
import com.shoppingapp.info.data.Product
import com.shoppingapp.info.databinding.AddEditProductBinding
import com.shoppingapp.info.utils.*
import kotlin.properties.Delegates



class AddEditProduct : Fragment() {

    companion object{
        private const val TAG = "AddEditProduct"
    }

    private lateinit var binding: AddEditProductBinding
    private lateinit var viewModel: AddEditProductViewModel
    private val focusChangeListener = MyOnFocusChangeListener()

    // arguments
    private var isEdit by Delegates.notNull<Boolean>()
    private lateinit var catName: String
    private lateinit var product: Product

    var userId = ""


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
            binding.addProImagesRv.adapter = adapter
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.add_edit_product,container,false)
        viewModel = ViewModelProvider(this)[AddEditProductViewModel::class.java]


        isEdit = arguments?.getBoolean(Constants.KEY_IS_EDIT) == true
        catName = arguments?.getString(Constants.KEY_CATEGORY).toString()

        product = try { arguments?.getParcelable<Product>(Constants.KEY_PRODUCT)!! }
        catch (ex: Exception){ Product() }


        val sharePrefManager = SharePrefManager(requireContext())
        userId = sharePrefManager.getUserIdFromSession()!!



//        productId = arguments?.getString("productId").toString()


        setViews()
        setObservers()



        return binding.root
    }


    private fun setObservers() {

        binding.apply {


            /** product update status **/
            viewModel.productUpdateStatus.observe(viewLifecycleOwner) { status ->
                when (status) {
                    DataStatus.LOADING -> {
                        binding.loaderLayout.loaderFrameLayout.show()
//                    binding.loaderLayout.circularLoader.showAnimationBehavior
                    }
                    DataStatus.SUCCESS -> {
                        binding.loaderLayout.loaderFrameLayout.hide()
                        findNavController().navigate(R.id.action_addProductFragment_to_homeFragment)
//                    binding.loaderLayout.circularLoader.hideAnimationBehavior

                    }
                    DataStatus.ERROR ->{
                        loaderLayout.loaderFrameLayout.hide()
                    }
                }
            }


            /** product update status **/
            viewModel.productDeleteStatus.observe(viewLifecycleOwner) { status ->
                when (status) {
                    DataStatus.LOADING -> {
                        binding.loaderLayout.loaderFrameLayout.show()
//                    binding.loaderLayout.circularLoader.showAnimationBehavior
                    }
                    DataStatus.SUCCESS -> {
                        binding.loaderLayout.loaderFrameLayout.hide()
                        findNavController().navigate(R.id.action_addProductFragment_to_homeFragment)
//                    binding.loaderLayout.circularLoader.hideAnimationBehavior

                    }
                    DataStatus.ERROR ->{
                        loaderLayout.loaderFrameLayout.hide()
                    }
                }
            }



            /** product submit status **/
            viewModel.productSubmitStatus.observe(viewLifecycleOwner) { status ->
                when (status) {
                    DataStatus.LOADING -> {
                        binding.loaderLayout.loaderFrameLayout.show()
//                    binding.loaderLayout.circularLoader.showAnimationBehavior
                    }
                    DataStatus.SUCCESS -> {
                        binding.loaderLayout.loaderFrameLayout.hide()
                        findNavController().navigate(R.id.action_addProductFragment_to_homeFragment)
//                    binding.loaderLayout.circularLoader.hideAnimationBehavior

                    }
                    DataStatus.ERROR -> {
                        loaderLayout.loaderFrameLayout.hide()
                    }
                }
            }


        }



    }



    // todo put this in data binding
    private fun fillDataInAllViews() {
        binding.apply {
            if(isEdit){
                product.let { product ->
                    Log.d(TAG, "fill data in views")
                    addProAppBar.topAppBar.title = "Edit Product - ${product.name}"
                    productName.setText(product.name)
                    proPriceEditText.setText(product.price.toString())
                    proMrpEditText.setText(product.mrp.toString())
                    productDes.setText(product.description)

                    imgList = product.images.map { it.toUri() } as MutableList<Uri>
                    val adapter = AddProductImagesAdapter(requireContext(), imgList)
                    addProImagesRv.adapter = adapter

                    btnAddProduct.text = "Update"
                    btnDeleteProduct.show()

                    setShoeSizesChips(product.availableSizes)
                    setShoeColorsChips(product.availableColors)
                }
            }
        }
    }

    private fun setViews() {
        binding.apply {
            if (!isEdit) { // add new product
                addProAppBar.topAppBar.title = "Add Product - ${product.category}"

                val adapter = AddProductImagesAdapter(requireContext(), imgList)
                addProImagesRv.adapter = adapter
                btnAddProduct.text = "Add Product"
            }else{
                fillDataInAllViews()
            }
            btnAddImagesToProduct.setOnClickListener {
                getImages.launch("image/*")
            }


            /** button back **/
            addProAppBar.topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            loaderLayout.loaderFrameLayout.visibility = View.GONE

            setShoeSizesChips()
            setShoeColorsChips()
            addProductErrorMessage.visibility = View.GONE
            productName.onFocusChangeListener = focusChangeListener
            proPriceEditText.onFocusChangeListener = focusChangeListener
            proMrpEditText.onFocusChangeListener = focusChangeListener
            productDes.onFocusChangeListener = focusChangeListener

            btnAddProduct.setOnClickListener {
                onAddProduct()
            }

            /** button delete product **/
            btnDeleteProduct.setOnClickListener {
                viewModel.deleteProduct(product.productId)
            }


        }


    }

    private fun onAddProduct() {
        binding.apply {
            val name = productName.text.toString()
            val price = proPriceEditText.text.toString().toDoubleOrNull()
            val mrp = proMrpEditText.text.toString().toDoubleOrNull()
            val desc = productDes.text.toString()

            if (name.isEmpty()){
                productName.error = "Product Name required!"
                productName.requestFocus()
            }
            if (price.toString().isEmpty()){
                proPriceEditText.error = "Price required!"
                proPriceEditText.requestFocus()
            }
            if (mrp.toString().isEmpty()){
                proMrpEditText.error = "Mrc required!"
                proMrpEditText.requestFocus()
            }
            if (desc.isEmpty()){
                productDes.error = "Description required!"
                productDes.requestFocus()
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
            else{
                val newProduct = Product(getProductId(userId),name,userId,desc,catName, price ?: 0.0,mrp ?: 0.0,sizeList.toList(),colorsList.toList())
                if (!isEdit){
                    viewModel.submitProduct(newProduct,imgList)
                }else{
                    product.name = name
                    product.mrp = mrp!!
                    product.description = desc
                    product.availableColors = colorsList.toList()
                    product.availableSizes = sizeList.toList()
                    product.price = price!!.toDouble()
                    viewModel.updateProduct(product,imgList,product.images)
                }

            }

        }

    }

    private fun setShoeSizesChips(shoeList: List<Int>? = emptyList()) {
        binding.addProductSizeChipGroup.apply {
            removeAllViews()
            for ((_, v) in ShoeSizes) {
                val chip = Chip(context)
                chip.id = v
                chip.tag = v

                chip.text = "$v"
                chip.isCheckable = true

                if (shoeList?.contains(v) == true) {
                    chip.isChecked = true
                    sizeList.add(chip.tag.toString().toInt())
                }

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

    private fun setShoeColorsChips(colorList: List<String>? = emptyList()) {
        binding.addProductColorChipGroup.apply {
            removeAllViews()
            var ind = 1
            for ((k, v) in ShoeColors) {
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

                if (colorList?.contains(k) == true) {
                    chip.isChecked = true
                    colorsList.add(chip.tag.toString())
                }

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