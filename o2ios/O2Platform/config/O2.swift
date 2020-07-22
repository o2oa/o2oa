//
//  O2.swift
//  O2Platform
//
//  Created by FancyLou on 2019/9/26.
//  Copyright © 2019 zoneland. All rights reserved.
//

import Foundation
import CocoaLumberjack


struct O2 {
    //考勤打卡版本判断用的 userDefaults的key
    public static let O2_Attendance_version_key = "attendance_version_key"
    
    public static let O2_Word_draft_mode = "draft"
    public static let O2_First_ID = "(0)"
    /// EZSE: Returns app's name
    public static var appDisplayName: String? {
        if let bundleDisplayName = Bundle.main.object(forInfoDictionaryKey: "CFBundleDisplayName") as? String {
            return bundleDisplayName
        } else if let bundleName = Bundle.main.object(forInfoDictionaryKey: "CFBundleName") as? String {
            return bundleName
        }

        return nil
    }

    /// EZSE: Returns app's version number
    public static var appVersion: String? {
        return Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String
    }

    /// EZSE: Return app's build number
    public static var appBuild: String? {
        return Bundle.main.object(forInfoDictionaryKey: kCFBundleVersionKey as String) as? String
    }

    /// EZSE: Return app's bundle ID
    public static var appBundleID: String? {
        return Bundle.main.bundleIdentifier
    }

    /// EZSE: Returns both app's version and build numbers "v0.3(7)"
    public static var appVersionAndBuild: String? {
        if appVersion != nil && appBuild != nil {
            if appVersion == appBuild {
                return "v\(appVersion!)"
            } else {
                return "v\(appVersion!)(\(appBuild!))"
            }
        }
        return nil
    }

    /// EZSE: Return device version ""
    public static var deviceVersion: String {
        var size: Int = 0
        sysctlbyname("hw.machine", nil, &size, nil, 0)
        var machine = [CChar](repeating: 0, count: Int(size))
        sysctlbyname("hw.machine", &machine, &size, nil, 0)
        return String(cString: machine)
    }

    // MARK: - 本地缓存目录

    ///云盘缓存目录
    public static func cloudFileLocalFolder() -> URL {
        let manager = FileManager.default
        let documentsURL = manager.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let cloudFolder = documentsURL
            .appendingPathComponent("O2")
            .appendingPathComponent("cloud")
        if !manager.fileExists(atPath: cloudFolder.path) {
            do {
                try manager.createDirectory(at: cloudFolder, withIntermediateDirectories: true, attributes: nil)
            } catch {
                DDLogError("创建文件夹错误，\(error.localizedDescription)")
            }
        }
        return cloudFolder
    }


    ///base64缓存目录
    public static func base64CacheLocalFolder() -> URL {
        let manager = FileManager.default
        let documentsURL = manager.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let base64Folder = documentsURL
            .appendingPathComponent("O2")
            .appendingPathComponent("base64")
        if !manager.fileExists(atPath: base64Folder.path) {
            do {
                try manager.createDirectory(at: base64Folder, withIntermediateDirectories: true, attributes: nil)
            } catch {
                DDLogError("创建文件夹错误，\(error.localizedDescription)")
            }
        }
        return base64Folder
    }


    ///info缓存目录
    public static func inforCacheLocalFolder() -> URL {
        let manager = FileManager.default
        let documentsURL = manager.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let inforFolder = documentsURL
            .appendingPathComponent("O2")
            .appendingPathComponent("infor")
        if !manager.fileExists(atPath: inforFolder.path) {
            do {
                try manager.createDirectory(at: inforFolder, withIntermediateDirectories: true, attributes: nil)
            } catch {
                DDLogError("创建文件夹错误，\(error.localizedDescription)")
            }
        }
        return inforFolder
    }
    
    ///删除文件夹
    public static func deleteFolder(folder: URL) {
        do{
            try FileManager.default.removeItem(atPath: folder.path)
        }catch{
            DDLogError("删除目录失败，\(error.localizedDescription)")
        }
    }
}
