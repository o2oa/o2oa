//
//  BaseModel.swift
//  o2app
//
//  Created by 刘振兴 on 2017/8/21.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import HandyJSON
import Moya

// MARK:- 通用过程处理回调闭包定义
public typealias ProgressBlock = (_ progressResponse:ProgressResponse) -> Void


// MARK:- 通用结果回调闭包定义
public typealias ResultBlock<T:BaseModel> = (_ result:OOResult<T>) -> Void


// MARK:- 结果类型定义
public enum ResultType:String,HandyJSONEnum {
    case SUCCESS = "success"
    case FAIL = "error"
}

// MARK:- 返回的基本数据格式协议
public protocol BaseModel:HandyJSON,CustomStringConvertible {
    
    //数据头一致，不同的data的数据类型
    associatedtype T
    
    var count:Int { get set }
    
    var date:String? {get set}
    
    var message:String? {get set}
    
    var position:Int {get set}
    
    var size:String? {get set}
    
    var spent:Int {get set}
    
    var type:ResultType? {get set}
    
    var userMessage:String? {get set}
    
    var data:T? {get set}
    
    func isSuccess() -> Bool
    
}


public protocol DataModel:HandyJSON,CustomStringConvertible {
    
}



// MARK:- 数据格式模型基本实现
open class BaseModelClass<U:CustomStringConvertible>:BaseModel,CustomStringConvertible {

    public typealias T = U
    
    open var position: Int = 0
    
    open var type: ResultType?

    open var count: Int = 0
    
    open var date: String?
    
    open var message: String?
    
    open var size: String?
    
    open var spent: Int = -1
    
    open var userMessage: String?
    
    open var data:T?
    
    required public init() {
        
    }
    
    public func isSuccess() -> Bool {
        guard let t = type else {
            return false
        }
        if t == .FAIL  {
            return false
        }
        return true
    }
    
    public var description: String {
        return "type=\(String(describing: type)),position=\(position),count=\(count),date=\(String(describing: date)),message=\(String(describing: message)),size=\(String.init(describing: size) ),spent=\(spent),userMessage=\(userMessage!),data=\(String(describing: data))"
    }
    
}

public class OOCommonValueModel:DataModel {
    
    var value:String?
    
    required public init() {
        
    }
    
    public var description: String {
        return "OOCommonValueModel"
    }
}

public class OOCommonValueBoolModel:DataModel {
    
    var value:Bool?
    
    required public init() {
        
    }
    
    public var description: String {
        return "OOCommonValueBoolModel"
    }
}

public class OOCommonValueIntModel:DataModel {
    
    var value:Int?
    
    required public init() {
        
    }
    
    public var description: String {
        return "OOCommonValueIntModel"
    }
}

public class OOCommonIdModel:DataModel {
    public var description: String {
        return "OOCommonIdModel"
    }
    
    var id:String?
    
    required public init() {
        
    }
}

public class OOCommonModel:DataModel {
    
    required public init() {
        
    }
    
    public var description: String {
        return "CommonModel"
    }
}
