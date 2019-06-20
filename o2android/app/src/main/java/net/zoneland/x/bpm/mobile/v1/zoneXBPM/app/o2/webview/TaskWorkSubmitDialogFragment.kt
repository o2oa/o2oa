package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import kotlinx.android.synthetic.main.fragment_task_work_submit.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.screenHeight
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.screenWidth
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.LoadingDialog

/**
 * Created by fancyLou on 2018/9/17.
 * Copyright © 2018 O2. All rights reserved.
 */

class TaskWorkSubmitDialogFragment: DialogFragment(), TaskWorkSubmitDialogContract.View {
    companion object {
        const val TAG = "TaskWorkSubmitDialogFragment"
        const val WORK_ID_KEY = "WORK_ID_KEY"
        const val FORM_DATA_KEY = "FORM_DATA_KEY"
        const val OPINION_TEXT_KEY = "OPINION_TEXT_KEY"
        const val TASK_DATA_KEY = "TASK_DATA_KEY"
        fun startWorkDialog(workId: String, taskData: String, formData: String?, opinionText: String?): TaskWorkSubmitDialogFragment {
            val dialog = TaskWorkSubmitDialogFragment()
            val arg = Bundle()
            arg.putString(WORK_ID_KEY, workId)
            arg.putString(FORM_DATA_KEY, formData)
            arg.putString(TASK_DATA_KEY, taskData)
            arg.putString(OPINION_TEXT_KEY, opinionText)
            dialog.arguments = arg
            return dialog
        }
    }

    val presenter: TaskWorkSubmitDialogPresenter = TaskWorkSubmitDialogPresenter()
    var isSignMode = false
    var workId: String = ""
    lateinit var taskData: TaskData
    var formData: String = ""
    var opinionText: String = ""
    val loadingDialog: LoadingDialog by lazy { LoadingDialog(activity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //NO_FRAME就是dialog无边框，0指的是默认系统Theme
        setStyle(android.app.DialogFragment.STYLE_NO_FRAME, R.style.customStyleDialogStyle)
        presenter.attachView(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_task_work_submit, container, false)
    }

    override fun onStart() {
        super.onStart()
        val window = dialog.window
        window!!.setLayout(activity.screenWidth(), activity.screenHeight())
        window.setGravity(Gravity.TOP)
        window.setWindowAnimations(R.style.DialogEmptyAnimation)//取消过渡动画 , 使DialogSearch的出现更加平滑
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workId = arguments.getString(WORK_ID_KEY) ?: ""
        val task = arguments.getString(TASK_DATA_KEY) ?: ""
        formData = arguments.getString(FORM_DATA_KEY) ?: ""
        opinionText = arguments.getString(OPINION_TEXT_KEY) ?: ""
        try {
            taskData = O2SDKManager.instance().gson.fromJson(task, TaskData::class.java)
        } catch (e: Exception) {
            XToast.toastShort(activity, "解析数据异常！")
            closeSelf()
        }

        //init view
        taskData.routeNameList?.let { list->
            list.forEachIndexed { index, s ->
                val tempButton = RadioButton(activity)
                tempButton.text = s
                tempButton.isChecked = list.size==1
                tempButton.id = index+ 100
                radio_group_task_work_submit_routers.addView(tempButton,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT)
            }
        }
        if (!TextUtils.isEmpty(opinionText)) {
            edit_task_work_submit_approve_opinion.setText(opinionText)
        }
        val layoutp = signature_view_task_work_submit_opinion.layoutParams
        val height = (activity?.screenWidth()?: 200) * 2 / 3
        XLog.info("paint screen height:$height")
        layoutp.height = height
        signature_view_task_work_submit_opinion.layoutParams = layoutp

        //event bind
        image_task_work_submit_close_btn.setOnClickListener { closeSelf() }
        tv_task_work_submit_btn.setOnClickListener {
            XLog.info("提交 流转。。。。")
            //检查空
            val sign = signature_view_task_work_submit_opinion.getSignatureBitmap()
            val radio = radio_group_task_work_submit_routers.findViewById<RadioButton>(radio_group_task_work_submit_routers.checkedRadioButtonId)
            if (radio == null) {
                XToast.toastShort(activity, "请选择决策！")
            }else {
                val routeName = radio.text.toString()
                val opinion =  edit_task_work_submit_approve_opinion.text.toString()
                if (activity is TaskWebViewActivity) {
                    (activity as TaskWebViewActivity).validateFormForSubmitDialog(routeName, opinion) {
                        result ->
                        if (result) {
                            taskData.routeName = routeName
                            taskData.opinion = opinion
                            loadingDialog.show()
                            presenter.submit(sign, taskData, workId, formData)
                        }else {
                            XToast.toastShort(activity, "表单校验不通过！")
                            closeSelf()
                        }
                    }
                }else {
                    XLog.error("activity 异常。。。。。无法校验表单！！！")
                }
            }
        }
        image_task_work_submit_sign_btn.setOnClickListener {
            if (isSignMode) {
                image_task_work_submit_sign_btn.setImageResource(R.mipmap.icon_shouxiesr)
                signature_view_task_work_submit_opinion.gone()
                edit_task_work_submit_approve_opinion.visible()
                image_task_work_submit_clear_btn.gone()
                tv_task_work_submit_opinion_label.text = getString(R.string.dialog_opinion_input_label)
            }else {
                image_task_work_submit_sign_btn.setImageResource(R.mipmap.icon_jianpansr)
                edit_task_work_submit_approve_opinion.gone()
                signature_view_task_work_submit_opinion.visible()
                image_task_work_submit_clear_btn.visible()
                tv_task_work_submit_opinion_label.text = getString(R.string.dialog_opinion_sign_label)
            }
            isSignMode = !isSignMode
        }
        image_task_work_submit_clear_btn.setOnClickListener {
            signature_view_task_work_submit_opinion.clear()
        }
    }


    override fun submitCallback(result: Boolean, site: String?) {
        loadingDialog.dismiss()
        if (result) {
            if (activity != null && activity is TaskWebViewActivity) {
                (activity as TaskWebViewActivity).finishSubmit(site)
            }
            closeSelf()
        }else {
            XToast.toastShort(activity, "提交失败！")
        }
    }

    private fun closeSelf() {
        dismissAllowingStateLoss()
    }
}