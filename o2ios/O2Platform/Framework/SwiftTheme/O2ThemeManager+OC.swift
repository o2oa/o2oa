//
//  ThemeManager+OC.swift
//  SwiftTheme
//
//  Created by Gesen on 16/9/18.
//  Copyright © 2016年 Gesen. All rights reserved.
//

import Foundation

@objc extension O2ThemeManager {
    
    /**
        extension for Objective-C, Use setTheme(plistName: String, path: ThemePath) in Swift
     */
    public class func setThemeWithPlistInMainBundle(_ plistName: String) {
        setTheme(plistName: plistName, path: .mainBundle)
    }
    
    /**
        extension for Objective-C, Use setTheme(plistName: String, path: ThemePath) in Swift
     */
    public class func setThemeWithPlistInSandbox(_ plistName: String, path: URL) {
        setTheme(plistName: plistName, path: .sandbox(path))
    }
    
    /**
        extension for Objective-C, Use setTheme(dict: NSDictionary, path: ThemePath) in Swift
     */
    public class func setThemeWithDictInMainBundle(_ dict: NSDictionary) {
        setTheme(dict: dict, path: .mainBundle)
    }
    
    /**
        extension for Objective-C, Use setTheme(dict: NSDictionary, path: ThemePath) in Swift
     */
    public class func setThemeWithDictInSandbox(_ dict: NSDictionary, path: URL) {
        setTheme(dict: dict, path: .sandbox(path))
    }
    
}
