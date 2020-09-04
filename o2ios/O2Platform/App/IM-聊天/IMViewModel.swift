//
//  IMViewModel.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/4.
//  Copyright © 2020 zoneland. All rights reserved.
//

import Promises
import CocoaLumberjack

class IMViewModel: NSObject {
    override init() {
        super.init()
    }


    private let communicateAPI = OOMoyaProvider<CommunicateAPI>()
    private let workAPI = OOMoyaProvider<OOWorkAPI>()
}

extension IMViewModel {
    
    //创建会话 @param type: single group
    func createConversation(type: String, users: [String]) -> Promise<IMConversationInfo> {
        let conversation = IMConversationInfo()
        conversation.type = type
        conversation.personList = users
        return Promise  { fulfill, reject in
            self.communicateAPI.request(.createConversation(conversation), completion: { result in
                let response = OOResult<BaseModelClass<IMConversationInfo>>(result)
                if response.isResultSuccess() {
                    if let info = response.model?.data {
                        fulfill(info)
                    } else {
                        reject(OOAppError.apiEmptyResultError)
                    }
                } else {
                    reject(response.error!)
                }
            })
        }
        
    }
    
    //修改标题
    func updateConversationTitle(id: String, title: String) -> Promise<IMConversationInfo> {
        return Promise { fulfill, reject in
            self.communicateAPI.request(.updateConversationTitle(id, title), completion: { result in
                let response = OOResult<BaseModelClass<IMConversationInfo>>(result)
                if response.isResultSuccess() {
                    if let info = response.model?.data {
                        fulfill(info)
                    } else {
                        reject(OOAppError.apiEmptyResultError)
                    }
                } else {
                    reject(response.error!)
                }
            })
        }
    }
    //修改成员列表
    func updateConversationPeople(id: String, users: [String]) -> Promise<IMConversationInfo> {
        return Promise { fulfill, reject in
            self.communicateAPI.request(.updateConversationPeople(id, users), completion: { result in
                let response = OOResult<BaseModelClass<IMConversationInfo>>(result)
                if response.isResultSuccess() {
                    if let info = response.model?.data {
                        fulfill(info)
                    } else {
                        reject(OOAppError.apiEmptyResultError)
                    }
                } else {
                    reject(response.error!)
                }
            })
        }
    }
    
    //阅读会话
    func readConversation(conversationId: String?) {
        guard let id = conversationId else {
            DDLogError("阅读会话失败, 传入id为空")
            return
        }
        self.communicateAPI.request(.readConversation(id), completion: {result in
            let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
            if response.isResultSuccess() {
                DDLogDebug("阅读当前会话成功！")
            }else {
                DDLogError("阅读会话失败！")
            }
        })
    }

    //发送消息
    func sendMsg(msg: IMMessageInfo) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.communicateAPI.request(.sendMsg(msg), completion: { result in
                    let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
                    if response.isResultSuccess() {
                        if let _ = response.model?.data {
                            fulfill(true)
                        } else {
                            reject(OOAppError.apiEmptyResultError)
                        }
                    } else {
                        reject(response.error!)
                    }
                })
        }
    }
    
    //上传文件
    func uploadFile(conversationId: String, type: String, fileName: String, file: Data) -> Promise<IMUploadBackModel> {
       return Promise { fulfill, reject in
           self.communicateAPI.request(.imUploadFile(conversationId, type, fileName, file), completion: { (result) in
               let response = OOResult<BaseModelClass<IMUploadBackModel>>(result)
               if response.isResultSuccess() {
                   if let back = response.model?.data {
                       fulfill(back)
                   }else {
                       reject(OOAppError.apiEmptyResultError)
                   }
               }else {
                   reject(response.error!)
               }
           })
       }
   }
   

    //查询会话列表
    func myConversationList() -> Promise<[IMConversationInfo]> {
        return Promise { fulfill, reject in
            self.communicateAPI.request(.myConversationList, completion: { result in
                let response = OOResult<BaseModelClass<[IMConversationInfo]>>(result)
                if response.isResultSuccess() {
                    if let list = response.model?.data {
                        let rList = list.sorted { (f, s) -> Bool in
                            let ft = f.lastMessage?.createTime ?? ""
                            let st = s.lastMessage?.createTime ?? ""
                            if ft == "" {
                                return true
                            }
                            if st == "" {
                                return false
                            }
                            return ft.toDate(formatter: "yyyy-MM-dd HH:mm:ss") > st.toDate(formatter: "yyyy-MM-dd HH:mm:ss")
                        }
                        fulfill(rList)
                    } else {
                        reject(OOAppError.apiEmptyResultError)
                    }
                } else {
                    reject(response.error!)
                }
            })
        }
    }
    //查询消息列表
    func myMsgPageList(page: Int, conversationId: String) -> Promise<[IMMessageInfo]> {
        return Promise { fulfill, reject in
            self.communicateAPI.request(.msgListByPaging(page, 15, conversationId), completion: { result in
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
                        } else {
                            reject(OOAppError.apiEmptyResultError)
                        }
                    } else {
                        reject(response.error!)
                    }
                })
        }
    }
    
    func getInstantMsgList() -> Promise<[InstantMessage]> {
        return Promise { fulfill, reject in
            self.communicateAPI.request(.instantMessageList(50), completion: { result in
                    let response = OOResult<BaseModelClass<[InstantMessage]>>(result)
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
                        } else {
                            reject(OOAppError.apiEmptyResultError)
                        }
                    } else {
                        reject(response.error!)
                    }
                })
        }
    }
    
    ///判断是否工作已经完成
    func isWorkCompleted(work: String) -> Promise<Bool> {
        return Promise { fulfill, reject in
            self.workAPI.request(.getWork(work), completion: {result in
                let response = OOResult<BaseModelClass<WorkInfoResData>>(result)
                if response.isResultSuccess() {
                    fulfill(false)
                }else {
                    fulfill(true)
                }
            })
        }
    }
}
