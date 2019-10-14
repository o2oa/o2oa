//
//  Response+HandyJSON.swift
//  o2app
//
//  Created by 刘振兴 on 2017/8/24.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import Moya
import HandyJSON


// MARK: - Response扩展
public extension Response {
    
     /// 整个 Data Model
     ///
     /// - Parameter type:
     /// - Returns:
    
    public func mapObject<T: HandyJSON>(_ type: T.Type) -> T? {
        
        guard let dataString = String.init(data: self.data, encoding: .utf8),
            let object = JSONDeserializer<T>.deserializeFrom(json: dataString)
            else {
                return nil
        }
        
        return object
    }
    
    
    /// 制定的某个 Key 对应的模型
    ///
    /// - Parameters:
    ///   - type:
    ///   - designatedPath:
    /// - Returns:
    public func mapObject<T: HandyJSON>(_ type: T.Type ,designatedPath: String) -> T?{
        
        guard let dataString = String(data: self.data, encoding: .utf8),
            let object = JSONDeserializer<T>.deserializeFrom(json: dataString, designatedPath: designatedPath)
            else {
                return nil
        }
        
        return object
    }
    
    /// Data 对应的 [Model]
    ///
    /// - Parameter type:
    /// - Returns:
    public func mapArray<T: HandyJSON>(_ type: T.Type)  -> [T?]? {
        
        guard let dataString = String(data: self.data, encoding: .utf8),
            let object = JSONDeserializer<T>.deserializeModelArrayFrom(json: dataString)
            else {
                return nil
        }
        return object
    }
    
    
    /// Data 某个Key 下对应的 的 [Model]
    public func mapArray<T: HandyJSON>(_ type: T.Type ,designatedPath: String )  -> [T?]? {
        guard let dataString = String(data: self.data, encoding: .utf8),
            let object = JSONDeserializer<T>.deserializeModelArrayFrom(json: dataString , designatedPath: designatedPath)
            else {
                return nil
        }
        return object
    }
    
}
