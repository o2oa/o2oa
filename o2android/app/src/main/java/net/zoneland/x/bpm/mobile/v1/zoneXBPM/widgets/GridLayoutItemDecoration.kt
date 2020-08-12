package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

/**
 * Created by fancyLou on 2020-07-20.
 * Copyright © 2020 O2. All rights reserved.
 */


class GridLayoutItemDecoration(val leftSpace: Int, val bottomSpace: Int, val spanCount: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = leftSpace
        outRect.bottom = bottomSpace
        val i = parent.getChildLayoutPosition(view) ?: 0
        if ((i % spanCount)==0) {
            outRect.left = 0
        }
    }
}