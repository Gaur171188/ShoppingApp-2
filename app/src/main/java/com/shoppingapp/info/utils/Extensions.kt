package com.shoppingapp.info.utils


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shoppingapp.info.activities.MainActivity
import com.shoppingapp.info.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

fun showMessage(context: Context, text: String){
    Toast.makeText(context,text,Toast.LENGTH_SHORT).show()
}

fun View.show() { visibility = View.VISIBLE }

fun View.hide() { visibility = View.GONE }

fun <T> throttleLatest(
    intervalMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var throttleJob: Job? = null
    var latestParam: T
    return { param: T ->
        latestParam = param
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                delay(intervalMs)
                latestParam.let(destinationFunction)
            }
        }
    }
}

fun <T> debounce(
    waitMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(waitMs)
            destinationFunction(param)
        }
    }
}


class DotsIndicatorDecoration(
    private val radius: Float,
    private val indicatorItemPadding: Float,
    private val indicatorHeight: Int,
    @ColorInt private val colorInactive: Int,
    @ColorInt private val colorActive: Int
) : RecyclerView.ItemDecoration() {

    private val inactivePaint = Paint()
    private val activePaint = Paint()

    init {
        val width = Resources.getSystem().displayMetrics.density * 1
        inactivePaint.apply {
            strokeCap = Paint.Cap.ROUND
            strokeWidth = width
            style = Paint.Style.STROKE
            isAntiAlias = true
            color = colorInactive
        }

        activePaint.apply {
            strokeCap = Paint.Cap.ROUND
            strokeWidth = width
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            color = colorActive
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val adapter = parent.adapter ?: return

        val itemCount = adapter.itemCount

        val totalLength: Float = (radius * 2 * itemCount)
        val padBWItems = max(0, itemCount - 1) * indicatorItemPadding
        val indicatorTotalWidth = totalLength + padBWItems
        val indicatorStartX = (parent.width - indicatorTotalWidth) / 2F

        val indicatorPosY = parent.height - indicatorHeight / 2F

        drawInactiveDots(c, indicatorStartX, indicatorPosY, itemCount)

        val activePos: Int =
            (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (activePos == RecyclerView.NO_POSITION) {
            return
        }

        val activeChild =
            (parent.layoutManager as LinearLayoutManager).findViewByPosition(activePos)
                ?: return

        drawActiveDot(c, indicatorStartX, indicatorPosY, activePos)


    }

    private fun drawInactiveDots(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        itemCount: Int
    ) {
        val w = radius * 2 + indicatorItemPadding
        var st = indicatorStartX + radius
        for (i in 1..itemCount) {
            c.drawCircle(st, indicatorPosY, radius, inactivePaint)
            st += w
        }
    }

    private fun drawActiveDot(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        highlightPos: Int
    ) {
        val w = radius * 2 + indicatorItemPadding
        val highStart = indicatorStartX + radius + w * highlightPos
        c.drawCircle(highStart, indicatorPosY, radius, activePaint)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = indicatorHeight
    }

}

internal fun launchHome(context: Context) {
    val homeIntent = Intent(context, MainActivity::class.java)
    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(homeIntent)
}

//internal fun getCompleteAddress(address: UserData.Address): String {
//    return if (address.streetAddress2.isBlank()) {
//        "${address.streetAddress}, ${address.city}, ${address.state} - ${address.zipCode}, ${getISOCountriesMap()[address.countryISOCode]}"
//    } else {
//        "${address.streetAddress}, ${address.streetAddress2}, ${address.city}, ${address.state} - ${address.zipCode}, ${getISOCountriesMap()[address.countryISOCode]}"
//    }
//}

internal fun disableClickOnWindow(activity: Activity) {
    activity.window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

internal fun enableClickOnWindow(activity: Activity) {
    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}