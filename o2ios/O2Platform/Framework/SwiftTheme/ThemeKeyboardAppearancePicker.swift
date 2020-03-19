//
//  ThemeKeyboardAppearancePicker.swift
//  SwiftTheme
//
//  Created by Gesen on 2017/3/1.
//  Copyright © 2017年 Gesen. All rights reserved.
//

import UIKit

@objc public final class ThemeKeyboardAppearancePicker: ThemePicker {
    
    public convenience init(keyPath: String) {
        self.init(v: { ThemeKeyboardAppearancePicker.getStyle(stringStyle: O2ThemeManager.string(for: keyPath) ?? "") })
    }
    
    public convenience init(keyPath: String, map: @escaping (Any?) -> UIKeyboardAppearance?) {
        self.init(v: { map(O2ThemeManager.value(for: keyPath)) })
    }
    
    public convenience init(styles: UIKeyboardAppearance...) {
        self.init(v: { O2ThemeManager.element(for: styles) })
    }
    
    public required convenience init(arrayLiteral elements: UIKeyboardAppearance...) {
        self.init(v: { O2ThemeManager.element(for: elements) })
    }
    
    public required convenience init(stringLiteral value: String) {
        self.init(keyPath: value)
    }
    
    public required convenience init(unicodeScalarLiteral value: String) {
        self.init(keyPath: value)
    }
    
    public required convenience init(extendedGraphemeClusterLiteral value: String) {
        self.init(keyPath: value)
    }
    
    class func getStyle(stringStyle: String) -> UIKeyboardAppearance {
        switch stringStyle.lowercased() {
        case "default"  : return .default
        case "dark"     : return .dark
        case "light"    : return .light
        default: return .default
        }
    }
    
}

public extension ThemeKeyboardAppearancePicker {
    
    class func pickerWithKeyPath(_ keyPath: String, map: @escaping (Any?) -> UIKeyboardAppearance?) -> ThemeKeyboardAppearancePicker {
        return ThemeKeyboardAppearancePicker(v: { map(O2ThemeManager.value(for: keyPath)) })
    }
    
    class func pickerWithStyles(_ styles: [UIKeyboardAppearance]) -> ThemeKeyboardAppearancePicker {
        return ThemeKeyboardAppearancePicker(v: { O2ThemeManager.element(for: styles) })
    }
    
}

@objc public extension ThemeKeyboardAppearancePicker {
    
    class func pickerWithKeyPath(_ keyPath: String) -> ThemeKeyboardAppearancePicker {
        return ThemeKeyboardAppearancePicker(keyPath: keyPath)
    }
    
    class func pickerWithStringStyles(_ styles: [String]) -> ThemeKeyboardAppearancePicker {
        return ThemeKeyboardAppearancePicker(v: { O2ThemeManager.element(for: styles.map(getStyle)) })
    }
    
}

extension ThemeKeyboardAppearancePicker: ExpressibleByArrayLiteral {}
extension ThemeKeyboardAppearancePicker: ExpressibleByStringLiteral {}
