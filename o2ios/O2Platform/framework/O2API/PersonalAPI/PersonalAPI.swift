//
//  PersonalAPI.swift
//  O2Platform
//
//  Created by FancyLou on 2018/11/21.
//  Copyright © 2018 zoneland. All rights reserved.
//

import O2OA_Auth_SDK
import Moya


enum PersonalAPI {
    case personInfo
    case updatePersonInfo(O2PersonInfo)
    case updatePersonIcon(UIImage)
    case meetingConfig
}

extension PersonalAPI: OOAPIContextCapable {
    var apiContextKey: String {
        return "x_organization_assemble_personal"
    }
}

extension PersonalAPI: OOAccessTokenAuthorizable {
    var shouldAuthorize: Bool {
        return true
    }
}

extension PersonalAPI: TargetType {
    var baseURL: URL {
        let model = O2AuthSDK.shared.o2APIServer(context: .x_organization_assemble_personal)
        let baseURLString = "\(model?.httpProtocol ?? "http")://\(model?.host ?? ""):\(model?.port ?? 80)\(model?.context ?? "")"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        switch self {
        case .personInfo:
            return "jaxrs/person"
        case .updatePersonInfo(_):
            return "jaxrs/person"
        case .updatePersonIcon(_):
            return "jaxrs/person/icon"
        case .meetingConfig:
            return "jaxrs/definition/meetingConfig"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .personInfo, .meetingConfig:
            return .get
        case .updatePersonInfo(_), .updatePersonIcon(_):
            return .put
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .personInfo, .meetingConfig:
            return .requestPlain
        case .updatePersonInfo(let person):
            return .requestParameters(parameters: person.toJSON() ?? [:], encoding: JSONEncoding.default)
        case .updatePersonIcon(let image):
            let data = image.pngData()
            let formData = MultipartFormData(provider: .data(data!), name: "file",
                                             fileName: "avatar.png", mimeType: "image/png")
            return .uploadMultipart([formData])
        }
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    
}
