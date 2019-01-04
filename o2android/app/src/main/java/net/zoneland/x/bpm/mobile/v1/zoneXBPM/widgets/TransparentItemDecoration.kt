package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.jetbrains.anko.dip


/**
 * 透明分割线 给GridLayoutManager的列表进行分割
 * Created by fancy on 2017/4/27.
 */

class TransparentItemDecoration(context: Context, orientation: Int) : RecyclerView.ItemDecoration() {

    private var mOrientation = orientation
    private val mWidth by lazy { context.dip( 8f) }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        //啥也不干，只在getItemOffsets中进行偏移
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val layoutManger = parent.layoutManager
        when(layoutManger){
            is GridLayoutManager -> {
                val childPosition = parent.getChildAdapterPosition(view)
                val lp = view.layoutParams as GridLayoutManager.LayoutParams
                val spanCount = layoutManger.spanCount
                if (mOrientation == LinearLayoutManager.VERTICAL) {
                    if (childPosition + lp.spanSize - 1 < spanCount) {//第一排的需要上面
                        outRect.top = mWidth
                    }else {
                        outRect.top = 0
                    }
                    if (lp.spanIndex + lp.spanSize == spanCount) {//最边上的需要右边,这里需要考虑到一个合并项的问题
                        outRect.right = mWidth
                    }else {
                        outRect.right = 0
                    }
                    outRect.bottom = mWidth
                    outRect.left = mWidth
                }else {
                    if (childPosition + lp.spanSize - 1 < spanCount) {//第一排的需要left
                        outRect.left = mWidth
                    }else {
                        outRect.left = 0
                    }
                    if (lp.spanIndex + lp.spanSize == spanCount) {//最边上的需要bottom
                        outRect.bottom = mWidth
                    }else {
                        outRect.bottom = 0
                    }
                    outRect.right = mWidth
                    outRect.top = mWidth
                }
            }
            else -> outRect.set(0,0,0,0)
        }
    }



}