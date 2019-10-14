//
//  OOCalendarAPI.swift
//  O2Platform
//
//  Created by FancyLou on 2018/7/24.
//  Copyright © 2018 zoneland. All rights reserved.
//

import Moya
import O2OA_Auth_SDK

enum OOCalendarAPI {
    case myCalendarList
    case filterCalendarEventList(OOCalendarEventFilter)
    case getCalendar(String)
    case saveCalendar(OOCalendarInfo)
    case deleteCalendar(String)
    case getPublicCalendarList // 日历广场
    case followCalendar(String)
    case followCalendarCancel(String)
    case saveCalendarEvent(OOCalendarEventInfo)
    case updateCalendarEventSingle(String, OOCalendarEventInfo)
    case updateCalendarEventAfter(String, OOCalendarEventInfo)
    case updateCalendarEventAll(String, OOCalendarEventInfo)
    case deleteCalendarEventSingle(String)
    case deleteCalendarEventAfter(String)
    case deleteCalendarEventAll(String)
}
// 上下文根
extension OOCalendarAPI: OOAPIContextCapable {
    var apiContextKey: String {
        return "x_calendar_assemble_control"
    }
}
// 是否需要xtoken
extension OOCalendarAPI: OOAccessTokenAuthorizable {
    var shouldAuthorize: Bool {
        return true
    }
}

extension OOCalendarAPI: TargetType {
    var baseURL: URL {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_calendar_assemble_control)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 80)\(model?.context ?? "")"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        switch self {
        case .myCalendarList:
            return "/jaxrs/calendar/list/my"
        case .filterCalendarEventList(_):
            return "/jaxrs/event/list/filter"
        case .getCalendar(let id):
            return "/jaxrs/calendar/\(id)"
        case .saveCalendar(_):
            return "/jaxrs/calendar"
        case .deleteCalendar(let id):
            return "/jaxrs/calendar/\(id)"
        case .getPublicCalendarList:
            return "/jaxrs/calendar/list/public"
        case .followCalendar(let id):
            return "/jaxrs/calendar/follow/\(id)"
        case .followCalendarCancel(let id):
            return "/jaxrs/calendar/follow/\(id)/cancel"
        case .saveCalendarEvent(_):
            return "/jaxrs/event"
        case .updateCalendarEventSingle(let id, _):
            return "/jaxrs/event/update/single/\(id)"
        case .updateCalendarEventAfter(let id, _):
            return "/jaxrs/event/update/after/\(id)"
        case .updateCalendarEventAll(let id, _):
            return "/jaxrs/event/update/all/\(id)"
        case .deleteCalendarEventSingle(let id):
            return "/jaxrs/event/single/\(id)"
        case .deleteCalendarEventAfter(let id):
            return "/jaxrs/event/after/\(id)"
        case .deleteCalendarEventAll(let id):
            return "/jaxrs/event/all/\(id)"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .myCalendarList:
            return .get
        case .filterCalendarEventList(_):
            return .put
        case .getCalendar(_):
            return .get
        case .saveCalendar(_):
            return .post
        case .deleteCalendar(_):
            return .delete
        case .getPublicCalendarList:
            return .get
        case .followCalendar(_):
            return .get
        case .followCalendarCancel(_):
            return .get
        case .saveCalendarEvent(_):
            return .post
        case .updateCalendarEventSingle(_, _):
            return .put
        case .updateCalendarEventAfter(_, _):
            return .put
        case .updateCalendarEventAll(_, _):
            return .put
        case .deleteCalendarEventSingle(_):
            return .delete
        case .deleteCalendarEventAfter(_):
            return .delete
        case .deleteCalendarEventAll(_):
            return .delete
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .myCalendarList:
            return .requestPlain
        case .filterCalendarEventList(let filter):
            return .requestParameters(parameters: filter.toJSON() ?? [:], encoding: JSONEncoding.default)
        case .getCalendar(_):
            return .requestPlain
        case .saveCalendar(let bean):
            return .requestParameters(parameters: bean.toJSON() ?? [:], encoding: JSONEncoding.default)
        case .deleteCalendar(_):
            return .requestPlain
        case .getPublicCalendarList:
            return .requestPlain
        case .followCalendar(_):
            return .requestPlain
        case .followCalendarCancel(_):
            return .requestPlain
        case .saveCalendarEvent(let bean):
            return .requestParameters(parameters: bean.toJSON() ?? [:], encoding: JSONEncoding.default)
        case .updateCalendarEventSingle(_, let bean):
            return .requestParameters(parameters: bean.toJSON() ?? [:], encoding: JSONEncoding.default)
        case .updateCalendarEventAfter(_, let bean):
            return .requestParameters(parameters: bean.toJSON() ?? [:], encoding: JSONEncoding.default)
        case .updateCalendarEventAll(_, let bean):
            return .requestParameters(parameters: bean.toJSON() ?? [:], encoding: JSONEncoding.default)
        case .deleteCalendarEventSingle(_):
            return .requestPlain
        case .deleteCalendarEventAfter(_):
            return .requestPlain
        case .deleteCalendarEventAll(_):
            return .requestPlain
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    
}

