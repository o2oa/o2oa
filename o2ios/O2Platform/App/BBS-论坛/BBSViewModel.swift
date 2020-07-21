//
//  BBSViewModel.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/28.
//  Copyright © 2020 zoneland. All rights reserved.
//

import Promises



class BBSViewModel : NSObject {
    override init() {
        super.init()
    }
    private let bbsAPI = OOMoyaProvider<O2BBSAPI>()
}

extension BBSViewModel {
    
    //获取附件列表
    func getSubjectAttachmentList(subjectId: String) -> Promise<[O2BBSSubjectAttachmentInfo]> {
        return Promise{ fulfill, reject in
            self.bbsAPI.request(.getSubjectAttachmentList(subjectId), completion: { result in
                let response = OOResult<BaseModelClass<[O2BBSSubjectAttachmentInfo]>>(result)
                if response.isResultSuccess() {
                    if let data = response.model?.data {
                        fulfill(data)
                    }else {
                        reject(OOAppError.apiEmptyResultError)
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    //下载附件
    func downloadAttachment(att: O2BBSSubjectAttachmentInfo) -> Promise<URL> {
        return Promise { fulfill, reject in
            self.bbsAPI.request(.downloadAttachForSubject(att), completion: {result in
                switch result {
                case .success(_):
                    //下载成功
                    let fileURL = O2.cloudFileLocalFolder().appendingPathComponent(att.fileName!)
                    fulfill(fileURL)
                case .failure(let err):
                    reject(err)
                }
            })
        }
    }
    
}
