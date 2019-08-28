package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.fragment_start_process_step_two.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview.TaskWebViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.ProcessWOIdentityJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import org.jetbrains.anko.dip


class StartProcessStepTwoFragment : BaseMVPFragment<StartProcessStepTwoContract.View, StartProcessStepTwoContract.Presenter>(), StartProcessStepTwoContract.View {

    override var mPresenter: StartProcessStepTwoContract.Presenter = StartProcessStepTwoPresenter()

    override fun layoutResId(): Int = R.layout.fragment_start_process_step_two

    companion object {
        val PROCESS_ID_KEY = "processId"
        val PROCESS_NAME_KEY = "processName"
        fun newInstance(processId:String, processName:String): StartProcessStepTwoFragment {
            val stepTwo = StartProcessStepTwoFragment()
            val bundle = Bundle()
            bundle.putString(PROCESS_ID_KEY, processId)
            bundle.putString(PROCESS_NAME_KEY, processName)
            stepTwo.arguments = bundle
            return stepTwo
        }
    }
    val identityList = ArrayList<ProcessWOIdentityJson>()
    var processId = ""
    var processName = ""
    var identity = ""
//    var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    override fun initUI() {
        val startString = getString(R.string.title_activity_start_process_step_two)
        (activity as StartProcessActivity).setToolBarTitle(startString)
        processId = arguments?.getString(PROCESS_ID_KEY) ?: ""
        processName = arguments?.getString(PROCESS_NAME_KEY) ?: ""
        tv_start_process_step_two_process_title.text = "$startString-$processName"
        tv_start_process_step_two_time.text = DateHelper.nowByFormate("yyyy-MM-dd HH:mm")
        btn_start_process_step_two_positive.setOnClickListener { startProcess() }
        btn_start_process_step_two_cancel.setOnClickListener { (activity as StartProcessActivity).finish() }
//        edit_start_process_step_two_title.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                globalLayoutListener = this
//                val screenHeight = activity.window.decorView.rootView.height
//                val screenWidth = activity.window.decorView.rootView.width
//                val rect = Rect()
//                activity.window.decorView.getWindowVisibleDisplayFrame(rect)
//                val height = screenHeight - (rect.bottom -rect.top)
//                if (height > screenHeight/3) {
//                    emptySpaceView(true, screenWidth, height)
//                }else {
//                    emptySpaceView(false)
//                }
//            }
//        })

        mPresenter.loadCurrentPersonIdentityWithProcess(processId)
    }

    override fun onDestroyView() {
//        if (globalLayoutListener!=null) {
//            edit_start_process_step_two_title.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
//        }
        super.onDestroyView()
        XLog.debug("StartProcessStepTwoFragment onDestroyView...............")
    }

    var emptyView: View ? = null

    private fun emptySpaceView(isShow:Boolean, width:Int=-1, height:Int=-1){
        XLog.debug("emptySpaceView $isShow, $width, $height")
        if (isShow) {
            if (emptyView ==null) {
                emptyView = View(activity)
                val param = LinearLayout.LayoutParams(width, height)
                linear_start_process_step_two_content.addView(emptyView, param)
            }
            emptyView?.visible()
            scroll_start_process_step_two.postDelayed({
                scroll_start_process_step_two.fullScroll(View.FOCUS_DOWN)
            }, 200)
        }else {
            emptyView?.gone()
        }
    }

    override fun loadCurrentPersonIdentity(list: List<ProcessWOIdentityJson>) {
        radio_group_process_step_two_department.removeAllViews()
        identityList.clear()
        identityList.addAll(list)
        if (identityList.size>0) {
            identityList.mapIndexed { index, it ->
                val radio = layoutInflater.inflate(R.layout.snippet_radio_button, null) as RadioButton
                radio.text = if (TextUtils.isEmpty(it.unitName)) it.name else it.unitName
                if (index==0) {
                    radio.isChecked = true
                    tv_start_process_step_two_identity.text = it.name
                    identity = it.distinguishedName
                }
                radio.id = 100 + index//这里必须添加id 否则后面获取选中Radio的时候 group.getCheckedRadioButtonId() 拿不到id 会有空指针异常
                val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(0, activity.dip(10f), 0, 0)
                radio_group_process_step_two_department.addView(radio, layoutParams)
            }
        }
        radio_group_process_step_two_department.setOnCheckedChangeListener { _, checkedId ->
            val index = checkedId - 100
            tv_start_process_step_two_identity.text = identityList[index].name
            identity = identityList[index].distinguishedName

        }
    }

    override fun loadCurrentPersonIdentityFail() {
        XToast.toastShort(activity, "没有查询到当前用户的身份信息，无法启动流程！")
        (activity as StartProcessActivity).removeFragment()
    }

    override fun startProcessSuccess(workId: String) {
        hideLoadingDialog()
        val bundle = Bundle()
        bundle.putString(TaskWebViewActivity.WORK_WEB_VIEW_WORK, workId)
        bundle.putString(TaskWebViewActivity.WORK_WEB_VIEW_TITLE, "拟稿")
        (activity as StartProcessActivity).go<TaskWebViewActivity>(bundle)
        (activity as StartProcessActivity).finish()
    }

    override fun startProcessFail(message:String) {
        XToast.toastShort(activity, "启动流程失败, $message")
        hideLoadingDialog()
    }



    private fun startProcess() {
        var title = edit_start_process_step_two_title.text.toString()
        if (TextUtils.isEmpty(title)) {
//            XToast.toastShort(activity, "请输入文件标题")
//            return
            title = "无标题"
        }
        showLoadingDialog()
        mPresenter.startProcess(title, identity, processId)
    }

}
