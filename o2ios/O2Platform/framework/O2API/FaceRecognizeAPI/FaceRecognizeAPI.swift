//
//  FaceRecognizeAPI.swift
//  O2Platform
//
//  Created by FancyLou on 2018/10/17.
//  Copyright © 2018 zoneland. All rights reserved.
//

import Moya
import O2OA_Auth_SDK

enum FaceRecognizeAPI {
    case search(UIImage, String)
}

extension FaceRecognizeAPI: OOAPIContextCapable {
    var apiContextKey: String {
        return "x_faceset_control"
    }
}

// MARK: - 是否需要加入x-token访问头
extension FaceRecognizeAPI:OOAccessTokenAuthorizable {
    public var shouldAuthorize: Bool {
        return false
    }
}

extension FaceRecognizeAPI: TargetType {
    var baseURL: URL {
        let webhost = O2AuthSDK.shared.centerServerInfo()?.webServer?.host
        let baseURLString = "http://\(webhost ?? "dev.o2oa.net"):8888/\(apiContextKey)"
        return URL(string: baseURLString)!
    }
    
    var path: String {
        switch self {
        case .search(_, let faceSet):
            return "/search/\(faceSet)"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .search(_, _):
            return .post
        }
    }
    
    var sampleData: Data {
        return "".data(using: String.Encoding.utf8)!
    }
    
    var task: Task {
        switch self {
        case .search(let image, _):
            let data = image.pngData()
            let formData = MultipartFormData(provider: .data(data!), name: "file",
                                              fileName: "face.png", mimeType: "image/png")
            return .uploadMultipart([formData])
        }
    }
    
    var headers: [String : String]? {
        return nil
    }

}
