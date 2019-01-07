package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.main


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import kotlinx.android.synthetic.main.activity_meeting.*
import kotlinx.android.synthetic.main.fragment_meeting_bottom_bar.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.invited.MeetingInvitedFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.room.MeetingRoomFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonFragmentPagerAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.addOnPageChangeListener




class MeetingMainActivity : BaseMVPActivity<MeetingMainContract.View, MeetingMainContract.Presenter>(), MeetingMainContract.View, View.OnClickListener {

    private val fragmentList: ArrayList<Fragment> = ArrayList(3)
    private val fragmentTitles: ArrayList<String> = ArrayList(3)
    private val adapter: CommonFragmentPagerAdapter by lazy { CommonFragmentPagerAdapter(supportFragmentManager,fragmentList, fragmentTitles) }
    var meetingConfig:String = ""//会议配置信息
    var isViewer = false//是否有查看权限
    var isCheckViewer = false//是否已经查询过查看权限了

    override var mPresenter: MeetingMainContract.Presenter = MeetingMainPresenter()


    override fun layoutResId(): Int = R.layout.activity_meeting

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar(getString(R.string.title_activity_meeting), true, true)

        fragmentList.add(MeetingMainFragment())
        fragmentList.add(MeetingInvitedFragment())
        fragmentList.add(MeetingRoomFragment())

        fragmentTitles.add(getString(R.string.tab_meeting))
        fragmentTitles.add(getString(R.string.tab_meeting_invited))
        fragmentTitles.add(getString(R.string.tab_meeting_room))
//        meeting_content_view_pager.canSlide = true
        meeting_content_view_pager.adapter = adapter
        meeting_content_view_pager.offscreenPageLimit = 3
        meeting_content_view_pager.addOnPageChangeListener{

            onPageSelected { position ->
                selectTab(position)
            }
        }

        icon_meeting_tab.setOnClickListener(this)
        icon_meeting_invited_tab.setOnClickListener(this)
        icon_meeting_room_tab.setOnClickListener(this)

        selectTab(0)
    }

    private fun selectTab(i: Int) {
//        when(i) {
//            0-> meeting_content_view_pager.canSlide = true
//            1-> meeting_content_view_pager.canSlide = false
//            2-> meeting_content_view_pager.canSlide = true
//        }
        setItemView(i)
        changeBottomIcon(i)
    }

    private fun setItemView(i : Int){
        meeting_content_view_pager.setCurrentItem(i,false)
    }

    private fun changeBottomIcon(i: Int) {
        resetBottomBtnAlpha()
        when (i) {
            0 -> {
                image_icon_meeting_tab.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_meeting_tab_red))
                tv_icon_meeting_tab.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_primary))
            }
            1 -> {
                image_icon_meeting_invited_tab.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_meeting_invited_tab_red))
                tv_icon_meeting_invited_tab.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_primary))
            }
            2 -> {
                image_icon_meeting_room_tab.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_meeting_room_tab_red))
                tv_icon_meeting_room_tab.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_primary))
            }
        }
    }

    private fun resetBottomBtnAlpha() {
        image_icon_meeting_tab.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_meeting_tab))
        tv_icon_meeting_tab.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_text_primary))
        image_icon_meeting_invited_tab.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_meeting_invited_tab))
        tv_icon_meeting_invited_tab.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_text_primary))
        image_icon_meeting_room_tab.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_meeting_room_tab))
        tv_icon_meeting_room_tab.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_text_primary))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.icon_meeting_tab -> selectTab(0)
            R.id.icon_meeting_invited_tab -> selectTab(1)
            R.id.icon_meeting_room_tab -> selectTab(2)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_scale_in, R.anim.activity_scale_out)
    }

}
