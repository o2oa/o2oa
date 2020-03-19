//
//  ThemeBarStylePicker.swift
//  SwiftTheme
//
//  Created by Gesen on 2017/1/28.
//  Copyright © 2017年 Gesen. All rights reserved.
//

import UIKit

#if os(tvOS)
    
final class ThemeBarStylePicker: ThemePicker {}
    
#else

@objc public final class ThemeBarStylePicker: ThemePicker {
    
    public convenience init(keyPath: String) {
        self.init(v: { ThemeBarStylePicker.getStyle(stringStyle: O2ThemeManager.string(for: keyPath) ?? "") })
    }
    
    public convenience init(keyPath: String, map: @escaping (Any?) -> UIBarStyle?) {
        self.init(v: { map(O2ThemeManager.value(for: keyPath)) })
    }
    
    public convenience init(styles: UIBarStyle...) {
        self.init(v: { O2ThemeManager.element(for: styles) })
    }
    
    public required convenience init(arrayLiteral elements: UIBarStyle...) {
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
    
    class func getStyle(stringStyle: String) -> UIBarStyle {
        switch stringStyle.lowercased() {
        case "default"  : return .default
        case "black"    : return .black
        default: return .default
        }
    }
    
}

public extension ThemeBarStylePicker {
    
    class func pickerWithKeyPath(_ keyPath: String, map: @escaping (Any?) -> UIBarStyle?) -> ThemeBarStylePicker {
        return ThemeBarStylePicker(v: { map(O2ThemeManager.value(for: keyPath)) })
    }
    
    class func pickerWithStyles(_ styles: [UIBarStyle]) -> ThemeBarStylePicker {
        return ThemeBarStylePicker(v: { O2ThemeManager.element(for: styles) })
    }
    
}

@objc public extension ThemeBarStylePicker {
    
    class func pickerWithKeyPath(_ keyPath: String) -> ThemeBarStylePicker {
        return ThemeBarStylePicker(keyPath: keyPath)
    }
    
    class func pickerWithStringStyles(_ styles: [String]) -> ThemeBarStylePicker {
        return ThemeBarStylePicker(v: { O2ThemeManager.element(for: styles.map(getStyle)) })
    }
    
}

extension ThemeBarStylePicker: ExpressibleByArrayLiteral {}
extension ThemeBarStylePicker: ExpressibleByStringLiteral {}

#endif
