//
//  OOResult.swift
//  o2app
//
//  Created by 刘振兴 on 2017/8/24.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import HandyJSON
import Moya
import Result

public final class OOResult<T:BaseModel> {
    
    var model:T?
    
    var error:OOAppError?
    
    fileprivate var sourceError:Swift.Error?
    
    init(_ result:Result<Response,MoyaError>) {
        switch result {
        case .success(let resp):
            self.model = resp.mapObject(T.self)
            if let _ = model {
                if model?.isSuccess() == false {
                    self.error = OOAppError.common(type: "APPError", message: model?.message ?? "", statusCode: 10001)
                }
            }else{
                self.error = OOAppError.common(type: "systemError", message: "转换出错", statusCode: 10001)
            }
            break
        case .failure(let err):
            self.sourceError = err
            transError()
            break
        }
    }
    
    func isResultSuccess() -> Bool {
        guard let _ = error else {
            return true
        }
        return false
    }
    
    
    
    
    fileprivate func transError() {
        guard let err = self.sourceError else {
            return
        }
        
        if err is MoyaError {
            let mErr = err as! MoyaError
            switch mErr {
            case .imageMapping(let resp):
                self.error = OOAppError.imageMapping(message: mErr.errorDescription!, statusCode: (mErr.response?.statusCode)!, data: resp.data)
                break
            case .jsonMapping(let resp):
                self.error = OOAppError.jsonMapping(message: mErr.errorDescription!, statusCode: (mErr.response?.statusCode)!, data: resp.data)
                break
            case .requestMapping(let str):
                self.error = OOAppError.requestMapping(str)
                break
            case .statusCode(let resp):
                self.error = OOAppError.statusCode(message: mErr.errorDescription!, statusCode: (mErr.response?.statusCode)!, data: resp.data)
                break
            case .stringMapping(let resp):
                self.error = OOAppError.stringMapping(message: mErr.errorDescription!, statusCode: (mErr.response?.statusCode)!, data: resp.data)
                break
            case .underlying(let uErr):
                self.error = OOAppError.underlying(uErr.0 as! Error)
                break
            case .objectMapping(let err,let resp):
                self.error = OOAppError.objectMapping(err, resp)
                break
            case .encodableMapping(let err):
                self.error = OOAppError.encodableMapping(err)
                break
            case .parameterEncoding(let err):
                self.error = OOAppError.parameterEncoding(err)
                break
            }
        }
        
        
    }
}
