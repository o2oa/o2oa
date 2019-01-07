package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ListView
import android.widget.PopupWindow
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.ViewHolder

/**
 * Created by fancy on 2017/7/12.
 * Copyright © 2017 O2. All rights reserved.
 */

class CommonMenuPopupWindow : PopupWindow  {

    var onMenuItemClickListener: OnMenuItemClickListener? = null

    constructor(menuItemList: ArrayList<String>, context: Context): super(context) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        width = dm.widthPixels
        height = dm.heightPixels
        val menuContent = LayoutInflater.from(context).inflate(R.layout.pop_person_avatar_menu, null)
        contentView = menuContent
//        animationStyle =  R.style.dir_popupwindow_anim
        isTouchable = true
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setTouchInterceptor(View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                dismiss()
                return@OnTouchListener false
            }
            false
        })

        val avatarMenuListView:ListView = contentView.findViewById(R.id.person_info_avatar_menu_list_id)
        avatarMenuListView.adapter = object : CommonAdapter<String>(context, menuItemList, R.layout.item_person_mobile_menu) {
            override fun convert(holder: ViewHolder?, t: String?) {
                holder?.setText(R.id.person_info_mobile_menu_text_id, t)
            }
        }
        avatarMenuListView.setOnItemClickListener { _, _, position, _ ->
            onMenuItemClickListener?.itemClick(position)
        }

    }


    interface OnMenuItemClickListener {
        fun itemClick(position: Int)
    }

}