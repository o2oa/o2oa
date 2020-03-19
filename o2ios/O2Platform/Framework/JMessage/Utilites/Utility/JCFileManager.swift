//
//  JCFileManager.swift
//  JChat
//
//  Created by deng on 2017/7/24.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCFileManager: NSObject {
    
    static func fileExists(atPath: String) -> Bool {
        let fileManager = FileManager.default
        return fileManager.fileExists(atPath: atPath)
    }
    
    static func saveFileToLocal(data: Data, savaPath: String) -> Bool {
        let fileManager = FileManager.default
        let exist = fileManager.fileExists(atPath: savaPath)
        if exist {
            try! fileManager.removeItem(atPath: savaPath)
        }
        if !fileManager.createFile(atPath: savaPath, contents: data, attributes: nil) {
            return false
        }
        return true
    }

}
