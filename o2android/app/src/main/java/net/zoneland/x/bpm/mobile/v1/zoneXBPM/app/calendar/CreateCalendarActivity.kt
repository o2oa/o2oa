package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bigkoo.pickerview.OptionsPickerView
import com.wugang.activityresult.library.ActivityResult
import kotlinx.android.synthetic.main.activity_create_calendar.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.CalendarOB.CalendarTypeUNIT
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.vm.CreateCalendarViewModel
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization.ContactPickerActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.databinding.ActivityCreateCalendarBinding
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CalendarPickerOption
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.ContactPickerResult
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ImmersedStatusBarUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.LoadingDialog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import org.jetbrains.anko.dip

class CreateCalendarActivity : AppCompatActivity() {


    private val viewModel: CreateCalendarViewModel by lazy { ViewModelProviders.of(this).get(CreateCalendarViewModel::class.java) }
    private val typeList = ArrayList<CalendarPickerOption>()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(this) }


    companion object {
        const val CALENDAR_ID_KEY = "CALENDAR_ID_KEY"
        fun startEdit(id: String): Bundle {
            val bundle = Bundle()
            bundle.putString(CALENDAR_ID_KEY, id)
            return bundle
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = DataBindingUtil.setContentView<ActivityCreateCalendarBinding>(this, R.layout.activity_create_calendar)
        bind.viewmodel = viewModel
        bind.setLifecycleOwner(this)
        // 沉浸式状态栏
        ImmersedStatusBarUtils.setImmersedStatusBar(this)

        initView()

        initData()
    }



    /************* MARK public ************/

    fun clickSaveBtn(view: View) {
        if (TextUtils.isEmpty(viewModel.calendarTitle.value)) {
            XToast.toastShort(this, "日历名称不能为空！")
            return
        }
        if (viewModel.calendarTypeKey.value == CalendarTypeUNIT) {
            if (TextUtils.isEmpty(viewModel.target.value)){
                XToast.toastShort(this, "日历所属组织不能为空！")
                return
            }
        }
        if (TextUtils.isEmpty(viewModel.calendarId.value)) {
            viewModel.saveCalendar()
        } else {
            viewModel.updateCalendar()
        }
    }

    fun clickClose(view: View) {
        finish()
    }

    fun clickDeleteBtn(view: View) {
        O2DialogSupport.openConfirmDialog(this, "确认要删除这个日历吗？", { _ ->
            viewModel.deleteCalendar()
        })
    }

    /**
     * 日历类型 选择
     */
    fun chooseType(view: View) {
        if (TextUtils.isEmpty(viewModel.calendarId.value)) {
            val picker = OptionsPickerView.Builder(this, OptionsPickerView.OnOptionsSelectListener { option1, _, _, _ ->
                viewModel.setCalendarType(typeList[option1])
            }).setTitleText(getString(R.string.calendar_type_choose))
                    .isDialog(true)
                    .build()
            picker.setPicker(typeList)
            var selectIndex = typeList.indexOfFirst { it.name == viewModel.calendarType.value }
            if (selectIndex < 0) {
                selectIndex = 0
            }
            picker.setSelectOptions(selectIndex)
            picker.showDialog()
        }
    }

    //选择所属组织
    fun chooseOrgTarget(view: View) {
        val bundle = ContactPickerActivity.startPickerBundle(arrayListOf(ContactPickerActivity.departmentPicker),multiple=false)
        ActivityResult.of(this)
                .className(ContactPickerActivity::class.java)
                .params(bundle)
                .greenChannel().forResult { _, data ->
                    val result = data?.getParcelableExtra<ContactPickerResult>(ContactPickerActivity.CONTACT_PICKED_RESULT)
                    if (result != null) {
                         viewModel.target.value = result.departments.firstOrNull()?.distinguishedName
                    }
                }
    }

    //选择管理者
    fun chooseManageablePersonList(view: View) {
        val bundle = ContactPickerActivity.startPickerBundle(arrayListOf("personPicker"),multiple=true)
        ActivityResult.of(this)
                .className(ContactPickerActivity::class.java)
                .params(bundle)
                .greenChannel().forResult { _, data ->
                    val result = data?.getParcelableExtra<ContactPickerResult>(ContactPickerActivity.CONTACT_PICKED_RESULT)
                    if (result != null) {
                        viewModel.manageablePersonList.value = result.users.map { it.distinguishedName }
                    }
                }
    }
    //选择可见范围
    fun chooseViewableList(view: View) {
        val bundle = ContactPickerActivity.startPickerBundle(arrayListOf(ContactPickerActivity.personPicker, ContactPickerActivity.departmentPicker,ContactPickerActivity.groupPicker), multiple=true)
        ActivityResult.of(this)
                .className(ContactPickerActivity::class.java)
                .params(bundle)
                .greenChannel().forResult { _, data ->
                    val result = data?.getParcelableExtra<ContactPickerResult>(ContactPickerActivity.CONTACT_PICKED_RESULT)
                    if (result != null) {
                        viewModel.viewablePersonList.value = result.users.map { it.distinguishedName }
                        viewModel.viewableUnitList.value = result.departments.map { it.distinguishedName }
                        viewModel.viewableGroupList.value = result.groups.map { it.distinguishedName }
                    }
                }
    }
    //选择可新建日程范围
    fun choosePublishableList(view: View) {
        val bundle = ContactPickerActivity.startPickerBundle(arrayListOf(ContactPickerActivity.personPicker, ContactPickerActivity.departmentPicker,ContactPickerActivity.groupPicker), multiple=true)
        ActivityResult.of(this)
                .className(ContactPickerActivity::class.java)
                .params(bundle)
                .greenChannel().forResult { _, data ->
                    val result = data?.getParcelableExtra<ContactPickerResult>(ContactPickerActivity.CONTACT_PICKED_RESULT)
                    if (result != null) {
                        viewModel.publishablePersonList.value = result.users.map { it.distinguishedName }
                        viewModel.publishableUnitList.value = result.departments.map { it.distinguishedName }
                        viewModel.publishableGroupList.value = result.groups.map { it.distinguishedName }
                    }
                }
    }



    /************* MARK private ************/

    private fun initView() {
        viewModel.oldCalendar.observe(this, Observer { calendar ->
            //更新 数据回填
            if (calendar != null) {
                viewModel.setBackCalendarInfo(calendar)
            } else {
                XToast.toastShort(this@CreateCalendarActivity, "获取日历数据出错！")
            }
        })
        viewModel.isLoadingLive().observe(this, Observer { loading ->
            if (loading == true) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        })
        viewModel.netResponseLive().observe(this, Observer { res ->
            if (res != null) {
                XLog.info("res:$res")
                XToast.toastShort(this, res.message)
                if (res.result) {
                    finish()
                }
            }
        })
        viewModel.calendarColorLive().observe(this, Observer { color ->
            if (color != null) {
                val size = ll_create_calendar_color_layout.childCount
                for (i in 0 until size) {
                    val card = ll_create_calendar_color_layout.getChildAt(i)
                    if (card is CardView) {
                        card.removeAllViews()
                        val tag = card.tag
                        if (tag != null) {
                            if (color == tag as String) {
                                //add image
                                val checkImage = ImageView(this@CreateCalendarActivity)
                                checkImage.setImageResource(R.mipmap.icon_calendar_check)
                                val lp = FrameLayout.LayoutParams(dip(16), dip(16))
                                lp.gravity = Gravity.CENTER
                                card.addView(checkImage, lp)
                            }
                        }
                    }
                }
            }
        })

        // calendar color
        ll_create_calendar_color_layout.removeAllViews()
        CalendarOB.deepColor.forEach { (_, value) ->
            val card = CardView(this@CreateCalendarActivity)
            card.setCardBackgroundColor(Color.parseColor(value))
            card.radius = dip(15).toFloat()
            card.tag = value
            val lp = LinearLayout.LayoutParams(dip(30), dip(30))
            val margin = dip(8)
            lp.setMargins(margin, margin, margin, margin)
            ll_create_calendar_color_layout.addView(card, lp)
            card.setOnClickListener {
                val tag = it?.tag
                if (tag != null) {
                    XLog.info("select color $tag")
                    viewModel.calendarColor.value = (tag as String)
                }
            }
        }
    }

    private fun initData() {
        viewModel.calendarColor.value = CalendarOB.deepColor[0]
        CalendarOB.calendarTypes.forEach { (key, value) ->
            typeList.add(CalendarPickerOption(value, key))
        }
        if (typeList.isNotEmpty()) {
            viewModel.setCalendarType(typeList[0])
        }

        val id = intent.extras?.getString(CALENDAR_ID_KEY, "")
        if (!TextUtils.isEmpty(id)) {
            viewModel.calendarId.value = id
        }
    }
}
