//
//  IMViewModel.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/4.
//  Copyright © 2020 zoneland. All rights reserved.
//

import Promises


class IMViewModel: NSObject {
    override init() {
        super.init()
    }
    
    
    let communicateAPI = OOMoyaProvider<CommunicateAPI>()
}

extension IMViewModel {
    //查询会话列表
    func myConversationList() -> Promise<[IMConversationInfo]> {
        return Promise { fulfill, reject in
            self.communicateAPI.request(.myConversationList, completion: { result in
                let response = OOResult<BaseModelClass<[IMConversationInfo]>>(result)
                if response.isResultSuccess() {
                    if let list = response.model?.data {
                        fulfill(list)
                    }else {
                        reject(OOAppError.apiEmptyResultError)
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    //查询消息列表
    func myMsgPageList(page: Int, conversationId: String) -> Promise<[IMMessageInfo]> {
        return Promise { fulfill, reject in
            self.communicateAPI.request(.msgListByPaging(page, 40, conversationId), completion: { result in
                let response = OOResult<BaseModelClass<[IMMessageInfo]>>(result)
                if response.isResultSuccess() {
                    if let list = response.model?.data {
                        //列表翻转
                        let rList = list.sorted { (f, s) -> Bool in
                            if let ft = f.createTime, let st = s.createTime {
                                return ft.toDate(formatter: "yyyy-MM-dd HH:mm:ss") < st.toDate(formatter: "yyyy-MM-dd HH:mm:ss")
                            }
                            return true
                        }
                        fulfill(rList)
                    }else {
                        reject(OOAppError.apiEmptyResultError)
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
}
