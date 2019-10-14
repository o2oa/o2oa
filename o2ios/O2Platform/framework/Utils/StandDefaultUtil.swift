//
//  StandDefaultUtil.swift
//  CommonUtil
//
//  Created by lijunjie on 15/12/4.
//  Copyright © 2015年 lijunjie. All rights reserved.
//

import Foundation

public class StandDefaultUtil {
    
    static let share = StandDefaultUtil()
    
    private init () {}
    
    public func standDefault() -> UserDefaults {
        return UserDefaults.standard
    }
    
    public func userDefaultCache(value: AnyObject?, key: String) {
        self.standDefault().set(value, forKey: key)
    }
    
    public func userDefaultRemove(key: String) {
        self.standDefault().removeObject(forKey: key)
    }
    
    public func userDefaultGetValue(key: String) -> AnyObject? {
        return self.standDefault().object(forKey: key) as AnyObject?
    }
    
    public func userDefaultEmptyValue(key: String) -> Bool {
        return self.userDefaultGetValue(key: key) == nil
    }
    
}

public let SharedStandDefaultUtil: StandDefaultUtil = StandDefaultUtil.share
