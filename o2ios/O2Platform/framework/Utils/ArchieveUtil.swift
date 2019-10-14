//
//  ArchieveUtil.swift
//  CommonUtil
//
//  Created by lijunjie on 15/12/4.
//  Copyright © 2015年 lijunjie. All rights reserved.
//

import Foundation

public class ArchieveUtil {
    
    static let share = ArchieveUtil()
    
    private init () {}

    public func archieveObject(anObject: NSCoding, toPath: String) -> Bool {
        let archieveData = NSKeyedArchiver.archivedData(withRootObject: anObject)
        return SharedFileUtil.writeFileData(data: archieveData as NSData, toPath: toPath)
    }
    
    public func unarchieveFromPath(filePath: String) -> AnyObject? {
        return NSKeyedUnarchiver.unarchiveObject(withFile: filePath) as AnyObject?
    }
}

public let SharedArchieveUtil: ArchieveUtil = ArchieveUtil.share
