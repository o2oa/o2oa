//
//  CacheUtil.swift
//  CommonUtil
//
//  Created by lijunjie on 15/12/4.
//  Copyright © 2015年 lijunjie. All rights reserved.
//

import Foundation

public class  CacheUtil {
    
    var cache: NSCache = NSCache<AnyObject, AnyObject>()
    
    static let share = CacheUtil()
    
    private init () {}
    
    public func shareCache() -> NSCache<AnyObject, AnyObject> {
        return cache
    }
    
    public func systemMemoryCacheSet(key: NSCoding, value: AnyObject) {
        self.shareCache().setObject(value, forKey: key)
    }
    
    public func systemMemoryCacheRemove(key: AnyObject) {
        self.shareCache().removeObject(forKey: key)
    }
    
    public func systemMemoryCacheGetValue(key:AnyObject) -> AnyObject? {
        return self.shareCache().object(forKey: key)
    }
    
    public func systemMemoryCacheEmptyValue(key:AnyObject) -> Bool {
        return (self.systemMemoryCacheGetValue(key: key) == nil)
    }
    
}

public let SharedCacheUtil: CacheUtil = CacheUtil.share

