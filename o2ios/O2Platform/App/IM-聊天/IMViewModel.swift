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
}
