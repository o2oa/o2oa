package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im.fm

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMConversationInfo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class O2IMConversationPresenter : BasePresenterImpl<O2IMConversationContract.View>(), O2IMConversationContract.Presenter {


    override fun createConversation(type: String, users: ArrayList<String>) {
        val service = getMessageCommunicateService(mView?.getContext())
        if (service != null) {
            val info = IMConversationInfo()
            info.type = type
            info.personList = users
            service.createConversation(info)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            if (it.data!= null) {
                                mView?.createConvSuccess(it.data)
                            }else{
                                mView?.createConvFail("创建会话失败！")
                            }
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.createConvFail("创建会话失败！${e?.message}")
                        }
                    }
        }
    }

    override fun getMyInstantMessageList() {
        val service = getMessageCommunicateService(mView?.getContext())
        service?.let { ser ->
            ser.instantMessageList(100)
                    .subscribeOn(Schedulers.io())
                    .flatMap { res ->
                        val list = res.data
                        if (list != null && list.isNotEmpty()) {
                            val newList = list.sortedBy { it.createTime }
                            Observable.just(newList)
                        }else {
                            Observable.just(ArrayList())
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { list->
                            if (list != null) {
                                mView?.myInstantMessageList(list)
                            }else{
                                mView?.myInstantMessageList(ArrayList())
                            }
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.myInstantMessageList(ArrayList())
                        }
                    }

        }
    }

    override fun getMyConversationList() {
        val service = getMessageCommunicateService(mView?.getContext())
        service?.let {
            it.myConversationList().subscribeOn(Schedulers.io())
                    .flatMap { res ->
                        val list = res.data
                        if ( list != null && list.isNotEmpty()) {
                            val newList = list.sortedByDescending { c -> c.lastMessage?.createTime  }
                            Observable.just(newList)
                        }else {
                            Observable.just(ArrayList())
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { list->
                            if (list != null) {
                                mView?.myConversationList(list)
                            }else{
                                mView?.myConversationList(ArrayList())
                            }
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.myConversationList(ArrayList())
                        }
                    }
        }
    }


}