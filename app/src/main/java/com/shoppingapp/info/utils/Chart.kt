package com.shoppingapp.info.utils

import android.graphics.Color
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

object Chart {

    private const val BINDING_COLOR = "#0c9674"
    private const val ARRIVING_COLOR = "#7dffc0"
    private const val REJECTED_COLOR = "#d11b5f"
    private const val CONFIRMED_COLOR = "#facd32"
    private const val DELIVERED_COLOR = "#ffffa0"


    val ordersStatusColor = arrayOf<Any>( BINDING_COLOR, ARRIVING_COLOR, REJECTED_COLOR, CONFIRMED_COLOR, DELIVERED_COLOR)

    fun configureChartModel(
        chartType: AAChartType,
        charTitle: String,
        itemName: String,
        categories: Array<String>, // set list of string categories
        colorsTheme: Array<Any>, // set list of string colors
        data: Array<Any>
    ): AAChartModel {

        val model =  AAChartModel()
            .chartType(chartType)
            .backgroundColor("#ffffff")
            .title(charTitle)
            .colorsTheme(colorsTheme)
            .dataLabelsEnabled(true)
            .yAxisTitle("℃")
            .series(arrayOf(AASeriesElement().name(itemName).data(data)))

        if (chartType != AAChartType.Pie){
            model.categories(categories)
        }

        return model
    }


    //                    .data(arrayOf(
//                        arrayOf(OrderStatus.BINDING, viewModel.getOrdersByStatus(OrderStatus.BINDING.name).size),
//                        arrayOf(OrderStatus.ARRIVING, viewModel.getOrdersByStatus(OrderStatus.ARRIVING.name).size),
//                        arrayOf(OrderStatus.REJECTED, viewModel.getOrdersByStatus(OrderStatus.REJECTED.name).size),
//                        arrayOf(OrderStatus.CONFIRMED, viewModel.getOrdersByStatus(OrderStatus.CONFIRMED.name).size),
//                        arrayOf(OrderStatus.DELIVERED, viewModel.getOrdersByStatus(OrderStatus.DELIVERED.name).size)
//                    ))))

}