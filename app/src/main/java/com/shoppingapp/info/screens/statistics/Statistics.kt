package com.shoppingapp.info.screens.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.shoppingapp.info.R
import com.shoppingapp.info.databinding.StatisticsBinding
import com.shoppingapp.info.utils.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

@Suppress("SameParameterValue")
class Statistics : Fragment() {

    companion object{
        private const val TAG = "Statistics"
    }

    private lateinit var binding: StatisticsBinding
    private val viewModel by sharedViewModel<StatisticsViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

//        viewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]
        binding = DataBindingUtil.inflate(inflater,R.layout.statistics, container, false)


        setViews()
        setObserves()



        return binding.root
    }



    private fun setObserves() {

      viewModel.apply {
          binding.apply {



              /** users countries **/
              viewModel.usersCountries.observe(viewLifecycleOwner) { countries->
                  if(!countries.isNullOrEmpty()){
                      configureUsersChart(AAChartType.Column)
                  }
              }

              /** cart countries **/
              viewModel.cartsCountries.observe(viewLifecycleOwner) { countries->
                  if(!countries.isNullOrEmpty()){
                      configureCartItemsChart(AAChartType.Column)
                  }
              }

              /** products countries **/
              viewModel.productsCountries.observe(viewLifecycleOwner) { countries->
                  if(!countries.isNullOrEmpty()){
                      configureProductsChart(AAChartType.Column)
                  }
              }




              /** orders **/
              viewModel.orders.observe(viewLifecycleOwner) { orders->
                  if(!orders.isNullOrEmpty()){
                      configureOrdersChart(AAChartType.Pie)
                  }
              }


              /** cartItems **/
              viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
                  if(!cartItems.isNullOrEmpty()){
//                      configureCartItemsChart(AAChartType.Column)
                  }
              }



              /** users **/
              viewModel.users.observe(viewLifecycleOwner) { users->
                  if (!users.isNullOrEmpty()){
                      swipeRefreshLayout.isRefreshing = false
                  }
              }


              /** users **/
              viewModel.products.observe(viewLifecycleOwner) { products->
                  if (!products.isNullOrEmpty()){
                      swipeRefreshLayout.isRefreshing = false
                  }
              }



              /** users status **/
              usersStatus.observe(viewLifecycleOwner) { status ->
                  if (status != null){
                      when(status){
                          DataStatus.LOADING -> {
                              loaderUsers.root.show()
                              loaderCartItems.root.show()
                              loaderOrders.root.show()
                          }
                          DataStatus.SUCCESS -> {
                              loaderUsers.root.hide()
                              loaderCartItems.root.hide()
                              loaderOrders.root.hide()
                          }
                          DataStatus.ERROR -> {
                              loaderUsers.root.hide()
                              loaderCartItems.root.hide()
                              loaderOrders.root.hide()
                          }
                      }
                  }
              }


              /** products status **/
              productsStatus.observe(viewLifecycleOwner) { status ->
                  if (status != null){
                      when(status){
                          DataStatus.LOADING -> {
                              loaderProducts.root.show()
                          }
                          DataStatus.SUCCESS -> {
                              loaderProducts.root.hide()
                          }
                          DataStatus.ERROR -> {
                              loaderProducts.root.hide()
                          }
                      }
                  }
              }



          }
      }

    }


    private fun setViews() {
        binding.apply {

            statisticsTopAppBar.topAppBar.title = resources.getString(R.string.statistics)


            /** refresh layout **/
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.loadUsers()
                viewModel.loadProducts()
            }


            /** button radio group **/
            chartsGroup.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId){
                    R.id.users -> {
                        hideAllCharts()
                        usersCartLayout.show()
                    }
                    R.id.products -> {
                        hideAllCharts()
                        productsChartLayout.show()
                    }
                    R.id.cartItems -> {
                        hideAllCharts()
                        cartItemsChartLayout.show()
                    }
                    R.id.orders -> {
                        hideAllCharts()
                        ordersChartLayout.show()
                    }
                }
            }

        }

    }


    private fun hideAllCharts(){
        binding.apply {
            usersCartLayout.hide()
            productsChartLayout.hide()
            cartItemsChartLayout.hide()
            ordersChartLayout.hide()
        }
    }


   private fun getOrderChartData(): Array<Any> {
        return arrayOf (
            arrayOf(OrderStatus.BINDING, viewModel.getOrdersByStatus(OrderStatus.BINDING.name).size),
            arrayOf(OrderStatus.ARRIVING, viewModel.getOrdersByStatus(OrderStatus.ARRIVING.name).size),
            arrayOf(OrderStatus.REJECTED, viewModel.getOrdersByStatus(OrderStatus.REJECTED.name).size),
            arrayOf(OrderStatus.CONFIRMED, viewModel.getOrdersByStatus(OrderStatus.CONFIRMED.name).size),
            arrayOf(OrderStatus.DELIVERED, viewModel.getOrdersByStatus(OrderStatus.DELIVERED.name).size)
        )
    }



    private fun configureOrdersChart(chartType: AAChartType) {
        val model = Chart.configureChartModel(chartType,
            charTitle = "Orders",
            itemName = "number of orders",
            arrayOf(),
            Chart.ordersStatusColor,
            data = getOrderChartData())
        binding.chartOrders.chartView.aa_drawChartWithChartModel(model)
    }



    private fun configureCartItemsChart(chartType: AAChartType) {
        val countries = viewModel.cartsCountries.value?.toTypedArray()!!
        val cartsCountries: Array<Any> = viewModel.getCartsCountInEachCountry().toTypedArray()
        val model = Chart.configureChartModel(chartType,
            charTitle = "Cart Items",
            itemName = "number of carts",
            countries,
            Chart.ordersStatusColor,
            cartsCountries
        )
        binding.chartCartItems.chartView.aa_drawChartWithChartModel(model)
    }



    private fun configureProductsChart(chartType: AAChartType) {
        val countries = viewModel.productsCountries.value?.toTypedArray()!!
        val cartsCountries: Array<Any> = viewModel.getProductsCountInEachCountry().toTypedArray()

        showMessage(requireContext(),cartsCountries.size.toString())
        val model = Chart.configureChartModel(chartType,
            charTitle = "Products",
            itemName = "number of products",
            countries,
            Chart.ordersStatusColor,
            cartsCountries
        )
        binding.chartProducts.chartView.aa_drawChartWithChartModel(model)
    }



    private fun configureUsersChart(chartType: AAChartType) {
        val countries = viewModel.usersCountries.value?.toTypedArray()!!
        val usersCounties: Array<Any> = viewModel.getUsersCountInEachCountry().toTypedArray()
        val model = Chart.configureChartModel(chartType,
            charTitle = "Users",
            itemName = "number of users",
            countries,
            Chart.ordersStatusColor,
            usersCounties
        )
        binding.chartUsers.chartView.aa_drawChartWithChartModel(model)
    }






//
//    fun configureUsersChart() {
//
//        val countries = viewModel.countries.value
//        val usersCounties = viewModel.getUsersCountInEachCountry()
//
//        val backgroundColorGradientColor = AAGradientColor.linearGradient(
//            AALinearGradientDirection.ToBottom,
//            "#4F00BC",
//            "#29ABE2"// Color string setting supports hex type and rgba type
//        )
//
//        val fillColorGradientColor = AAGradientColor.linearGradient(
//            AALinearGradientDirection.ToBottom,
//            "rgba(256,256,256,0.3)",
//            "rgba(256,256,256,1.0)"// Color string setting supports hex type and rgba type
//        )
//
//
//        val aaChartModel = AAChartModel()
//            .chartType(AAChartType.Areaspline)
//            .title("Users")
//            .subtitle("")
//            .backgroundColor(backgroundColorGradientColor)
//            .yAxisVisible(true)
//            .yAxisTitle("number of users")
//            .categories(countries!!.toTypedArray())
////            .categories(arrayOf("Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"))
////            .categories(arrayOf(countries!![0],countries[1]))
//            .markerRadius(0f)
//            .series(arrayOf(
//                AASeriesElement()
//                    .name(resources.getString(R.string.user))
//                    .color(AAColor.White)
//                    .lineWidth(7f)
//                    .fillColor(fillColorGradientColor)
//                    .data(usersCounties.toTypedArray())
////                    .data(arrayOf(usersCounties))
////                    .data(arrayOf(usersCounties[0],usersCounties[1]))
////                    .data(arrayOf(7.0, 6.9, 2.5, 14.5, 18.2, 21.5, 5.2, 26.5, 23.3, 45.3, 13.9, 9.6))
//            ))
//        val aaOptions = aaChartModel.aa_toAAOptions()
//        aaOptions.plotOptions?.areaspline
//            ?.dataLabels(
//                AADataLabels()
//                    .enabled(true)
//                    .style(
//                        AAStyle()
//                            .color(AAColor.Black)
//                            .fontSize(14f)
//                            .fontWeight(AAChartFontWeightType.Thin)))
//
//        /** Cross Hair option **/
//        val aaCrossHair = AACrosshair()
//            .dashStyle(AAChartLineDashStyleType.LongDashDot)
//            .color(AAColor.White)
//            .width(1f)
//
//        val aaLabels = AALabels()
//            .useHTML(true)
//            .style(
//                AAStyle()
//                    .fontSize(10f)
//                    .fontWeight(AAChartFontWeightType.Bold)
//                    .color(AAColor.White))
//
//        /** y axis option **/
//        aaOptions.yAxis?.apply {
//            opposite(false)
//                .tickWidth(2f)
//                .lineWidth(1.5f)
//                .lineColor(AAColor.White)
//                .gridLineWidth(0f)
//                .crosshair(aaCrossHair)
//                .labels(aaLabels)
//        }
//
//        /** x axis option **/
//        aaOptions.xAxis?.apply {
//            tickWidth(1f)// X axis thick Width
//                .lineWidth(1.0f)//X axis width
//                .lineColor(AAColor.White)//color of axis X
//                .crosshair(aaCrossHair)
//                .labels(aaLabels)
//        }
//
//        aaOptions.legend?.apply {
//            itemStyle(
//                AAItemStyle()
//                    .color(AAColor.White)
//                    .fontSize(13f)
//                    .fontWeight("thin")
//            )}
//
//        binding.chartUsers.chartView.aa_drawChartWithChartOptions(aaOptions)
//
//    }



}