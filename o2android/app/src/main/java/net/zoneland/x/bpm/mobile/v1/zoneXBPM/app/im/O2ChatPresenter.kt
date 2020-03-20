package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessageForm
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class O2ChatPresenter : BasePresenterImpl<O2ChatContract.View>(), O2ChatContract.Presenter  {


    override fun getConversation(id: String) {
        val service = getMessageCommunicateService(mView?.getContext())
        service?.conversation(id)?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        if (it.data != null) {
                            mView?.conversationInfo(it.data)
                        }else {
                            mView?.conversationGetFail()
                        }
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.conversationGetFail()
                    }
                }
    }

    override fun sendTextMessage(msg: IMMessage) {
        val service = getMessageCommunicateService(mView?.getContext())
        service?.sendMessage(msg)?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())?.o2Subscribe {
            onNext {
                val id = it.data.id
                if (id != null) {
                    mView?.sendMessageSuccess(id)
                }else {
                    mView?.sendFail(msg.id)
                }
            }
            onError { e, _ ->
                XLog.error("", e)
                mView?.sendFail(msg.id)
            }
        }
    }

    override fun getMessage(page: Int, conversationId: String) {
        val service = getMessageCommunicateService(mView?.getContext())
        service?.messageByPage(page, O2.DEFAULT_PAGE_NUMBER, IMMessageForm(conversationId))
                ?.subscribeOn(Schedulers.io())
                ?.flatMap { res->
                    val list = res.data
                    val result = ArrayList<IMMessage>()
                    if (list != null && list.isNotEmpty()) {
                        result.addAll(list.sortedBy { it.createTime })
                    }
                    Observable.just(result)
                }
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        val list = it
                        if (list != null) {
                            mView?.backPageMessages(list)
                        }else {
                            mView?.backPageMessages(ArrayList())
                        }
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.backPageMessages(ArrayList())
                    }
                }
    }

    override fun readConversation(conversationId: String) {
        val service = getMessageCommunicateService(mView?.getContext())
        service?.let {
            it.readConversation(conversationId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            XLog.debug("read success")
                        }
                        onError { e, _ ->
                            XLog.error("read error", e)
                        }
                    }
        }
    }
}