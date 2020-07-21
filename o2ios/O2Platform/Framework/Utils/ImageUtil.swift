//
//  ImageUtil.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/30.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import CocoaLumberjack
import Promises

extension UIImage {
//    public static func base64ToImage(_ source:String,defaultImage:UIImage=UIImage(named: "personDefaultIcon")!) -> UIImage {
//        if source != ""{
//            let theImage = UIImage(data: Data(base64Encoded:source,options:NSData.Base64DecodingOptions.ignoreUnknownCharacters)!)
//            return theImage!
//        }else{
//            return defaultImage
//        }
//    }
//
//    class func image(color: UIColor, size: CGSize) -> UIImage {
//
//        let rect = CGRect.init(x: 0, y: 0, width: size.width, height: size.height)
//        UIGraphicsBeginImageContext(rect.size)
//
//        let context = UIGraphicsGetCurrentContext()
//
//        context?.setFillColor(color.cgColor)
//        context?.fill(rect)
//
//        let img = UIGraphicsGetImageFromCurrentImageContext()
//        UIGraphicsEndImageContext()
//
//        return img!
//    }
}


class ImageUtil {
    private init() { }

    static let shared: ImageUtil = {
        return ImageUtil()
    }()

    private let o2ProcessAPI = OOMoyaProvider<OOApplicationAPI>()

    //获取流程应用的图标
    func getProcessApplicationIcon(id: String) -> Promise<UIImage> {
        return Promise { fulfill, reject in
            self.loadApplicationBase64FromCache(id: id).then { (image) in
                fulfill(image)
            }.catch { (error) in
                DDLogError("从缓存获取流程应用图标失败 \(error.localizedDescription)")
                self.loadApplicationBase64FromNet(id: id).then { (img) in
                    fulfill(img)
                }.catch { (err) in
                    DDLogError("从网络获取流程应用图标失败 \(err.localizedDescription)")
                    fulfill(UIImage(named: "todo_8")!)
                }
            }
        }
    }


    //从缓存文件读取base64 转换成image
    private func loadApplicationBase64FromCache(id: String) -> Promise<UIImage> {
        return Promise { fulfill, reject in
            let icon = self.readBase64String(id: id)
            if icon != nil && icon?.isEmpty != true {
                fulfill(UIImage.base64ToImage(icon!, defaultImage: UIImage(named: "todo_8")!))
            } else {
                reject(OOAppError.apiEmptyResultError)
            }
        }
    }

    //下载base64 转换成image
    private func loadApplicationBase64FromNet(id: String) -> Promise<UIImage> {
        return Promise { fulfill, reject in
            self.o2ProcessAPI.request(.icon(id), completion: { result in
                    let myResult = OOResult<BaseModelClass<O2ApplicationIcon>>(result)
                    if myResult.isResultSuccess() {
                        if let item = myResult.model?.data, let icon = item.icon, !icon.isEmpty {
                            DDLogDebug("网络获取icon成功...............")
                            self.writeBase64StringFile(id: id, base64: icon)//写入缓存
                            fulfill(UIImage.base64ToImage(icon, defaultImage: UIImage(named: "todo_8")!))
                        } else {
                            reject(OOAppError.apiEmptyResultError)
                        }
                    } else {
                        reject(myResult.error!)
                    }
                })
        }
    }

    


    //从缓存文件读取base64字符串
    private func readBase64String(id: String) -> String? {
        let path = O2.base64CacheLocalFolder().appendingPathComponent("\(id)")
        do {
            return try String(contentsOf: path, encoding: .utf8)
        }
        catch {
            DDLogError(error.localizedDescription)
        }
        return nil
    }

    //base64字符串写入本地缓存文件 用id作为文件名称
    private func writeBase64StringFile(id: String, base64: String) {
        let path = O2.base64CacheLocalFolder().appendingPathComponent("\(id)")
        do {
            try base64.write(to: path, atomically: false, encoding: .utf8)
        } catch {
            DDLogError(error.localizedDescription)
        }
//        if FileManager.default.fileExists(atPath: path.path) {
//
//        }else {
//            let result = FileManager.default.createFile(atPath: path.path, contents: base64.data(using: .utf8), attributes: nil)
//            DDLogDebug("创建文件完成， result:\(result)")
//        }
    }

}
