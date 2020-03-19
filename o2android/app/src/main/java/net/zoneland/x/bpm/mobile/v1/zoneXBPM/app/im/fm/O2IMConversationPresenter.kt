package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im.fm

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class O2IMConversationPresenter : BasePresenterImpl<O2IMConversationContract.View>(), O2IMConversationContract.Presenter {

    override fun getMyConversationList() {
        val service = getMessageCommunicateService(mView?.getContext())
        service?.let {
            it.myConversationList().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { res->
                            val list = res.data
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