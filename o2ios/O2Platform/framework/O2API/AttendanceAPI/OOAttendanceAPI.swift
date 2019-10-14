//
//  OOAttendanceAPI.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/16.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import Foundation
import Moya
import O2OA_Auth_SDK


// MARK:- 所有调用的API枚举
enum OOAttendanceAPI {
    case attendanceDetailCheckIn(OOAttandanceMobileCheckinForm) //打卡
    case myAttendanceDetailMobileByPage(CommonPageModel,OOAttandanceMobileQueryBean) //获取打卡数据
    case myWorkplace //我的打卡地点列表
    case addWorkplace(OOAttandanceNewWorkPlace) //增加打卡地点
    case delWorkplace(String) //删除打卡地点
    case attendanceAdmin //是否可以设置打卡地点
    case checkinCycle(String,String) //考勤周期
    case checkinTotalForMonth(OOAttandanceTotalBean) //考勤统计
    case checkinAnalyze(OOAttandanceTotalBean) //考勤分析
}

// MARK:- 上下文实现
extension OOAttendanceAPI:OOAPIContextCapable {
    var apiContextKey: String {
        return "x_attendance_assemble_control"
    }
}


// MARK: - 是否需要加入x-token访问头
extension OOAttendanceAPI:OOAccessTokenAuthorizable {
    public var shouldAuthorize: Bool {
        return true
    }
}

extension OOAttendanceAPI:TargetType {
    var baseURL: URL {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_attendance_assemble_control)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 0)\(model?.context ?? "")"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        switch self {
        case .addWorkplace(_):
            return "/jaxrs/workplace"
        case .attendanceAdmin:
            return "/jaxrs/attendanceadmin/list/all"
        case .attendanceDetailCheckIn(_):
            return "/jaxrs/attendancedetail/mobile/recive"
        case .delWorkplace(let id):
            return "/jaxrs/workplace/\(id)"
        case .myAttendanceDetailMobileByPage(let model, _):
            return "/jaxrs/attendancedetail/mobile/filter/list/page/1/count/\(model.pageSize)"
        case .myWorkplace:
            return "/jaxrs/workplace/list/all"
        case .checkinCycle(let year, let month):
            return "/jaxrs/attendancestatisticalcycle/cycleDetail/\(year)/\(month)"
        case .checkinTotalForMonth(_):
            return "/jaxrs/attendancedetail/filter/list"
        case .checkinAnalyze(let bean):
            return "/jaxrs/statisticshow/person/\(bean.q_empName!)/\(bean.q_year!)/\(bean.q_month!)"
        
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .addWorkplace(_):
            return .post
        case .attendanceAdmin:
            return .get
        case .attendanceDetailCheckIn(_):
            return .post
        case .delWorkplace(_):
            return .delete
        case .myAttendanceDetailMobileByPage(_, _):
            return .put
        case .myWorkplace:
            return .get
        case .checkinCycle(_, _):
            return .get
        case .checkinTotalForMonth(_):
            return .put
        case .checkinAnalyze(_):
            return .get
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .addWorkplace(let bean):
            return .requestParameters(parameters: bean.toJSON() ?? [:], encoding: JSONEncoding.default)
        case .attendanceAdmin:
            return .requestPlain
        case .attendanceDetailCheckIn(let bean):
            return .requestParameters(parameters: bean.toJSON() ?? [:], encoding: JSONEncoding.default)
        case .delWorkplace(_):
            return .requestPlain
        case .myAttendanceDetailMobileByPage(_,let bean):
            return .requestParameters(parameters: bean.toJSON() ?? [:], encoding: JSONEncoding.default)
        case .myWorkplace:
            return .requestPlain
        case .checkinCycle(_,_):
            return .requestPlain
        case .checkinTotalForMonth(let bean):
            return .requestParameters(parameters: bean.toJSON() ?? [:], encoding: JSONEncoding.default)
        case .checkinAnalyze(_):
            return .requestPlain
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
}



