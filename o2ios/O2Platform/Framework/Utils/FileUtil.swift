//
//  FileUtil.swift
//  CommonUtil
//
//  Created by lijunjie on 15/11/14.
//  Copyright © 2015年 lijunjie. All rights reserved.
//

import Foundation

public class FileUtil {
    
    static let share = FileUtil()
    
    private init () {}
    
    public func createDirectory(path: String) {
        if !directoryExist(directoryPath: path) {
            do {
                try FileManager.default.createDirectory(atPath: path, withIntermediateDirectories: true, attributes: nil)
            } catch {
                print("创建目录错误！")
            }
        }
    }
    
    public func fileExist(filePath: String) -> Bool {
        return FileManager.default.fileExists(atPath: filePath, isDirectory: nil)
    }
    
    public func deleteFile(filePath: String) -> Bool {
        do {
            try FileManager.default.removeItem(atPath: filePath)
            return true
        } catch {
            return false
        }
    }
    
    public func directoryExist(directoryPath: String) -> Bool {
        var isDir:ObjCBool = true
        return FileManager.default.fileExists(atPath: directoryPath, isDirectory: &isDir)
    }
    
    public func writeFileData(data:NSData, toPath: String) -> Bool {
        return data.write(toFile: toPath, atomically: true)
    }
    
    public func readFromFile(path: String) -> NSData? {
        return NSData(contentsOfFile: path)
    }
    
    public func deleteFileAtPath(filePath: String) {
        do {
            try FileManager.default.removeItem(atPath: filePath)
        } catch {
            print("删除目录里面的文件错误！")
        }
    }
    
    public func deleteDirectoryAtPath(dirPath: String) {
        do {
            try FileManager.default.removeItem(atPath: dirPath)
        } catch {
            print("删除目录里面的目录错误！")
        }
    }
    
    
    
    
    public func copyFileFromPath(fromPath: String, toPath: String, isRemoveOld: Bool) -> Bool {
        var res = false
        if self.fileExist(filePath: fromPath) {
            do {
                try FileManager.default.copyItem(atPath: fromPath, toPath: toPath)
                res = true
            } catch {
                print("复制文件错误！")
            }
        } else {
            print("源文件不存在！")
        }
        
        if res && isRemoveOld {
            do {
                try FileManager.default.removeItem(atPath: fromPath)
            } catch {
                print("删除当前路径")
            }
        }
        return res
    }
    
    public func documentDirectory() -> String {
        return NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first!
    }
    
    public func documentDirectoryPath(file: String) -> String {
        return (self.documentDirectory() as NSString).appendingPathComponent(file)
    }
    
    public func cacheDirectory() -> String {
        return NSSearchPathForDirectoriesInDomains(.cachesDirectory, .userDomainMask, true).first!
    }
    
    public func cacheDir() -> URL {
        return FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask)[0]
    }
    
    public func cacheDirectoryPath(file: String) -> String {
        return (self.cacheDirectory() as NSString).appendingPathComponent(file)
    }
    
    public func cacheDirectoryDelete(file: String) -> Bool {
         return self.deleteFile(filePath: self.cacheDirectoryPath(file: file))
    }
    
    public func documentDirectoryDelete(file: String) -> Bool {
        return self.deleteFile(filePath: self.documentDirectoryPath(file: file))
    }
    
    public func getDirFileNames(dirPath: String) -> [String] {
        var arrTemp = [NSURL]()
        do {
            arrTemp = try FileManager.default.contentsOfDirectory(at: NSURL.fileURL(withPath: dirPath), includingPropertiesForKeys: [URLResourceKey.nameKey], options: FileManager.DirectoryEnumerationOptions.skipsHiddenFiles) as [NSURL]
        } catch {
            return Array()
        }
        if arrTemp.count == 0 {
            return Array()
        }
        var arr = [String]()
        for fileNameUrl in arrTemp {
            arr.append(fileNameUrl.relativePath!)
        }
        return arr
    }
    
    public func saveImage(path: NSString) {
        let lastDir = path.deletingLastPathComponent
        if FileManager.default.fileExists(atPath: lastDir, isDirectory: nil) {
            try! FileManager.default.createDirectory(atPath: lastDir, withIntermediateDirectories: true, attributes: nil)
        }
    }
}

public let SharedFileUtil: FileUtil = FileUtil.share


