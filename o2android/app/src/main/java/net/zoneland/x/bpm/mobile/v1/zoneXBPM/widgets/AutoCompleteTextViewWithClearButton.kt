package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.AutoCompleteTextView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R

/**
 * Created by fancy on 2017/4/17.
 */

class AutoCompleteTextViewWithClearButton : AutoCompleteTextView, View.OnFocusChangeListener, View.OnTouchListener, TextWatcher {

    private val clearIcon: Drawable by lazy { ContextCompat.getDrawable(context, R.mipmap.icon_off_round)  }
    var onTextChangedListener: OnTextChangedListener? = null

    constructor(context: Context): super(context) {
        initSomeThing()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initSomeThing()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        initSomeThing()
    }

    private fun initSomeThing() {
        clearIcon.setBounds(0, 0, clearIcon.intrinsicHeight, clearIcon.intrinsicHeight)
        visibleClearIcon(false)
        super.setOnTouchListener(this)
        super.setOnFocusChangeListener(this)
        addTextChangedListener(this)
    }

    private fun visibleClearIcon(visible: Boolean) {
        clearIcon.setVisible(visible, false)
        setCompoundDrawables(compoundDrawables[0],
                compoundDrawables[1],
                when(visible){
                    true-> clearIcon
                    false-> null},
                compoundDrawables[3])
    }


    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            visibleClearIcon(text.length>0)
        }else {
            visibleClearIcon(false)
        }

    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        val x: Float = event.x
        if (clearIcon.isVisible && x > width - paddingRight - clearIcon.intrinsicWidth) {
            if (event.action == MotionEvent.ACTION_UP) {
                setText("")
                return true
            }
        }
        return false
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
       if (isFocused) {
           visibleClearIcon(text.length>0)
           onTextChangedListener?.onTextChanged(text, start, lengthBefore, lengthAfter)
       }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }


    interface OnTextChangedListener {
        fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int)
    }
}