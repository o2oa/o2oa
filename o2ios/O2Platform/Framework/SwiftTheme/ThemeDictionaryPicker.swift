//
//  ThemeDictionaryPicker.swift
//  SwiftTheme
//
//  Created by Gesen on 2017/1/28.
//  Copyright © 2017年 Gesen. All rights reserved.
//

import Foundation

@objc public final class ThemeDictionaryPicker: ThemePicker {
    
    public convenience init<T>(keyPath: String, map: @escaping (Any?) -> [T: AnyObject]?) {
        self.init(v: { map(O2ThemeManager.value(for: keyPath)) })
    }
    
    public convenience init<T>(dicts: [T: AnyObject]...) {
        self.init(v: { O2ThemeManager.element(for: dicts) })
    }
    
    public required convenience init(arrayLiteral elements: [String: AnyObject]...) {
        self.init(v: { O2ThemeManager.element(for: elements) })
    }
    
}

@objc public extension ThemeDictionaryPicker {
    
    class func pickerWithKeyPath(_ keyPath: String, map: @escaping (Any?) -> [String: AnyObject]?) -> ThemeDictionaryPicker {
        return ThemeDictionaryPicker(v: { map(O2ThemeManager.value(for: keyPath)) })
    }
    
    class func pickerWithKeyPath(_ keyPath: String, mapAttributes: @escaping (Any?) -> [NSAttributedString.Key: AnyObject]?) -> ThemeDictionaryPicker {
        return ThemeDictionaryPicker(v: { mapAttributes(O2ThemeManager.value(for: keyPath)) })
    }
    
    class func pickerWithDicts(_ dicts: [[String: AnyObject]]) -> ThemeDictionaryPicker {
        return ThemeDictionaryPicker(v: { O2ThemeManager.element(for: dicts) })
    }
    
    class func pickerWithAttributes(_ attributes: [[NSAttributedString.Key: AnyObject]]) -> ThemeDictionaryPicker {
        return ThemeDictionaryPicker(v: { O2ThemeManager.element(for: attributes) })
    }
    
}

extension ThemeDictionaryPicker: ExpressibleByArrayLiteral {}
