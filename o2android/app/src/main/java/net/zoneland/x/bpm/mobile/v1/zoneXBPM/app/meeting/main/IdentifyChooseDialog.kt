package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.main

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.dialog_identify_choose.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.ProcessWOIdentityJson
import org.jetbrains.anko.dip


/**
 * Created by 73419 on 2017/12/18 0018.
 */
class IdentifyChooseDialog(context: Context?, list: List<ProcessWOIdentityJson>, listener: DialogCallback) : Dialog(context), View.OnClickListener {

    private var callback: DialogCallback = listener
    private var identifyId: String = ""
    private val identityList = list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_identify_choose)
        this.setCanceledOnTouchOutside(false)
        radio_group_identify.removeAllViews()
        identityList.mapIndexed { index, it ->
            val radio = layoutInflater.inflate(R.layout.snippet_radio_button, null) as RadioButton
            radio.text = if (TextUtils.isEmpty(it.unitName)) it.name else it.unitName
            if (index==0) {
                radio.isChecked = true
                identifyId = it.distinguishedName
            }
            radio.id = 100 + index//这里必须添加id 否则后面获取选中Radio的时候 group.getCheckedRadioButtonId() 拿不到id 会有空指针异常
            val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(0, context.dip(10f), 0, 0)
            radio_group_identify.addView(radio, layoutParams)
        }
        radio_group_identify.setOnCheckedChangeListener { _, checkedId ->
            val index = checkedId - 100
            identifyId = identityList[index].distinguishedName
        }
        btn_identify_dialog_negative.setOnClickListener(this)
        btn_identify_dialog_positive.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_identify_dialog_negative -> callback.negativeCallback()
            R.id.btn_identify_dialog_positive -> callback.positiveCallback(identifyId)
        }
    }

    interface DialogCallback{
        fun positiveCallback(identifyId: String)
        fun negativeCallback()
    }
}