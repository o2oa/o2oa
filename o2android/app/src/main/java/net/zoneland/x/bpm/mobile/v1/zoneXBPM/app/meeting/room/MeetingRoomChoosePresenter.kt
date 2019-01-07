package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.room

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.Group
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.MeetingRoom
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MeetingRoomChoosePresenter : BasePresenterImpl<MeetingRoomChooseContract.View>(), MeetingRoomChooseContract.Presenter {

    override fun findBuildingListByTime(startTime: String, endTime: String) {
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            service.listBuildingsByTime(startTime, endTime)
                    .subscribeOn(Schedulers.io())
                    .flatMap { response ->
                        val resultList = response.data
                        val retList = ArrayList<Group<MeetingRoom.Building, MeetingRoom.Room>>()
                        resultList.map {
                            val building = it.copyToVO()
                            val roomList = ArrayList<MeetingRoom.Room>()
                            it.roomList.map { room ->
                                roomList.add(room.copyToVO())
                            }
                            retList.add(Group(building, roomList))
                        }
                        Observable.just(retList)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ list ->
                        mView?.findBuildingList(list)
                    },
                            { e ->
                                XLog.error("", e)
                                mView?.findError("获取会议室信息失败，请检查网络情况是否正常！")
                            })
        }
    }
}
