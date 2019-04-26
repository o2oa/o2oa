package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog

import android.app.Dialog
import android.content.Context
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import org.jetbrains.anko.dip
import org.jetbrains.annotations.NotNull

/**
 * Created by fancy on 2017/9/14.
 * Copyright © 2017 O2. All rights reserved.
 */


class O2AlertDialogBuilder(val mContext: Context)  {

    private var dialogTitle: CharSequence = mContext.getText(R.string.hint)
    private var dialogContent: CharSequence = "" //提示内容 和 dialogCustomView互相排斥
    private var dialogCustomView: View? = null //自定义view 和 dialogContent互相排斥
    private var dialogIcon: O2AlertIconEnum = O2AlertIconEnum.ALERT
    private var negativeText: CharSequence? = null
    private var positiveText: CharSequence = mContext.getText(R.string.positive)

    //event
    private var _onPositiveListener: ((dialog: O2Dialog) -> Unit)? = null
    private var _onNegativeListener: ((dialog: O2Dialog) -> Unit)? = null


    fun build(): O2Dialog{
        if (TextUtils.isEmpty(dialogContent) && dialogCustomView == null) {
            throw IllegalStateException("You must set one of content or customView !")
        }
        return  O2Dialog()
    }

    fun show(): O2Dialog {
        val dialog = build()
        dialog.show()
        return dialog
    }

    /**
     * 提醒标题
     */
    fun title(@StringRes titleRes: Int): O2AlertDialogBuilder {
        if (titleRes == 0) {
            return this
        }
        dialogTitle = mContext.getString(titleRes)
        return this
    }

    fun title(@NotNull titleChar: CharSequence): O2AlertDialogBuilder {
        dialogTitle = titleChar
        return this
    }

    /**
     * 提醒内容
     */
    fun content(@StringRes contentRes: Int): O2AlertDialogBuilder {
        if (contentRes == 0) {
            return this
        }
        if (dialogCustomView!=null) {
            throw IllegalStateException("You cannot set content When you already have CustomView !")
        }
        dialogContent = mContext.getString(contentRes)
        return this
    }

    fun content(@NotNull contentChar: CharSequence): O2AlertDialogBuilder {
        if (dialogCustomView!=null) {
            throw IllegalStateException("You cannot set content When you already have CustomView !")
        }
        dialogContent = contentChar
        return this
    }

    /**
     * 自定义内容界面
     */
    fun customView(@LayoutRes customRes: Int): O2AlertDialogBuilder {
        if (customRes == 0) {
            return this
        }
        if (!TextUtils.isEmpty(dialogContent)) {
            throw IllegalStateException("You cannot set customView When you already have content !")
        }
        val inflater = LayoutInflater.from(mContext)
        dialogCustomView = inflater.inflate(customRes, null)
        return this
    }
    fun customView(@NotNull customView: View): O2AlertDialogBuilder {
        if (!TextUtils.isEmpty(dialogContent)) {
            throw IllegalStateException("You cannot set customView When you already have content !")
        }
        dialogCustomView = customView
        return this
    }

    /**
     * 提醒显示图标
     * 只有alertDialog才有效果
     */
    fun icon(@NotNull icon: O2AlertIconEnum): O2AlertDialogBuilder {
        dialogIcon = icon
        return this
    }

    /**
     * 确定按钮文字
     */
    fun positive(@StringRes positiveRes: Int): O2AlertDialogBuilder {
        if (positiveRes == 0) {
            return this
        }
        positiveText = mContext.getText(positiveRes)
        return this
    }

    fun positive(@NotNull positiveChar: CharSequence): O2AlertDialogBuilder {
        positiveText = positiveChar
        return this
    }

    fun onPositiveListener(func: ((dialog: O2Dialog) -> Unit)): O2AlertDialogBuilder {
        _onPositiveListener = func
        return this
    }

    /**
     * 否定按钮文字
     */
    fun negative(@StringRes negativeRes: Int): O2AlertDialogBuilder {
        if (negativeRes == 0) {
            return this
        }
        negativeText = mContext.getText(negativeRes)
        return this
    }

    fun negative(@NotNull negativeChar: CharSequence): O2AlertDialogBuilder {
        negativeText = negativeChar
        return this
    }

    fun onNegativeListener(func: (dialog: O2Dialog) -> Unit): O2AlertDialogBuilder {
        _onNegativeListener = func
        return this
    }



    inner class O2Dialog : Dialog {
        private val singleButtonLength: Int by lazy { context.dip(185) }
        private val twoButtonLength: Int by lazy { context.dip(110) }

        // UI
        private var view: View? = null
        private var iconImage: ImageView? = null
        private var titleTV: TextView? = null
        private var negativeBtn: Button? = null
        private var positiveBtn: Button? = null
        constructor() : super(mContext, R.style.o2AlertDialogTheme) {
            val inflater = LayoutInflater.from(mContext)
            if (!TextUtils.isEmpty(dialogContent)) {
                view = inflater.inflate(R.layout.dialog_o2_alert, null)
            }else {
                view = inflater.inflate(R.layout.dialog_o2_custom, null)
            }
            buildUI()
            setContentView(view)
        }

        private fun buildUI() {
            iconImage = view?.findViewById<ImageView>(R.id.image_o2_dialog_icon)
            titleTV = view?.findViewById<TextView>(R.id.tv_o2_dialog_title)
            negativeBtn = view?.findViewById(R.id.btn_o2_dialog_negative)
            positiveBtn = view?.findViewById(R.id.btn_o2_dialog_positive)

            //title设置
            titleTV?.text = dialogTitle
            //content view 设置
            val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val contentView = view?.findViewById<LinearLayout>(R.id.linear_o2_dialog_content)
            if (!TextUtils.isEmpty(dialogContent)) {
                //只有提示dialog才有ICON图标
                when (dialogIcon) {
                    O2AlertIconEnum.ALERT -> {
                        val drawable = FancySkinManager.instance().getDrawable(mContext, O2AlertIconEnum.ALERT.iconRes)
                        iconImage?.setImageDrawable(drawable)
                    }
                    O2AlertIconEnum.CLEAR -> {
                        val drawable = FancySkinManager.instance().getDrawable(mContext, O2AlertIconEnum.CLEAR.iconRes)
                        iconImage?.setImageDrawable(drawable)
                    }
                    O2AlertIconEnum.UPDATE -> {
                        val drawable = FancySkinManager.instance().getDrawable(mContext, O2AlertIconEnum.UPDATE.iconRes)
                        iconImage?.setImageDrawable(drawable)
                    }
                    O2AlertIconEnum.SUCCESS -> {
                        val drawable = FancySkinManager.instance().getDrawable(mContext, O2AlertIconEnum.SUCCESS.iconRes)
                        iconImage?.setImageDrawable(drawable)
                    }
                    O2AlertIconEnum.FAILURE -> {
                        val drawable = FancySkinManager.instance().getDrawable(mContext, O2AlertIconEnum.FAILURE.iconRes)
                        iconImage?.setImageDrawable(drawable)
                    }
                }
                val contentTextView = TextView(mContext)
                contentTextView.text = dialogContent
                contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                contentTextView.setTextColor(FancySkinManager.instance().getColor(mContext, R.color.z_color_text_primary))
                contentTextView.gravity = Gravity.CENTER_HORIZONTAL
                contentView?.addView(contentTextView, layoutParam)
            }else {
                contentView?.addView(dialogCustomView, layoutParam)
            }
            //positive negative 设置
            positiveBtn?.text = positiveText
            val positiveLayoutP = positiveBtn?.layoutParams as LinearLayout.LayoutParams
            if (!TextUtils.isEmpty(negativeText)) {
                negativeBtn?.text = negativeText
                negativeBtn?.visible()
                negativeBtn?.setOnClickListener {
                    _onNegativeListener?.invoke(this)
                    dismiss()
                }
                positiveLayoutP.width = twoButtonLength
                positiveLayoutP.setMargins(context.dip(15), 0, context.dip(20), 0)
                positiveBtn?.layoutParams = positiveLayoutP
            } else {
                negativeBtn?.gone()
                positiveLayoutP.width = singleButtonLength
                positiveLayoutP.setMargins(context.dip(45), 0, context.dip(45), 0)
                positiveBtn?.layoutParams = positiveLayoutP
            }
            positiveBtn?.setOnClickListener {
                _onPositiveListener?.invoke(this)
                dismiss()
            }
        }
    }
}

