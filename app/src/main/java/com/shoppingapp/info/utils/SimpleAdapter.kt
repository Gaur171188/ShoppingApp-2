package com.shoppingapp.info.utils


//import android.view.LayoutInflater
//import android.widget.ImageView
//import androidx.core.net.toUri
//import androidx.databinding.DataBindingUtil
//import com.bumptech.glide.Glide
//import com.shoppingapp.info.R
//import com.shoppingapp.info.data.Ads
//import com.shoppingapp.info.data.CustomBean
//import com.shoppingapp.info.databinding.AdsBinding
//import com.shoppingapp.info.databinding.ItemBannerBinding
//import com.zhpan.bannerview.BaseBannerAdapter
//import com.zhpan.bannerview.BaseViewHolder
//
//
//class SimpleAdapter : BaseBannerAdapter<CustomBean>() {
//
//    override fun bindData(holder: BaseViewHolder<CustomBean>, data: CustomBean?, position: Int, pageSize: Int) {
//        val imageStart: ImageView = holder.findViewById(R.id.iv_logo)
////        holder.setImageResource(R.id.banner_image, data!!.imageRes)
//
//
//
//
//        Glide.with(imageStart.context)
//            .load(data!!.imgUrl!!.toUri())
//            .into(imageStart)
//    }
//
//    override fun getLayoutId(viewType: Int): Int {
//        return R.layout.item_custom_view;
//    }
//}