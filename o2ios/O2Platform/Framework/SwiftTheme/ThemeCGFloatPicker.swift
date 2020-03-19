//
//  ThemeCGFloatPicker.swift
//  SwiftTheme
//
//  Created by Gesen on 2017/1/28.
//  Copyright © 2017年 Gesen. All rights reserved.
//

import UIKit

@objc public final class ThemeCGFloatPicker: ThemePicker{
    
    public convenience init(keyPath: String) {
        self.init(v: { CGFloat(O2ThemeManager.number(for: keyPath)?.doubleValue ?? 0) })
    }
    
    public convenience init(keyPath: String, map: @escaping (Any?) -> CGFloat?) {
        self.init(v: { map(O2ThemeManager.value(for: keyPath)) })
    }
    
    public convenience init(floats: CGFloat...) {
        self.init(v: { O2ThemeManager.element(for: floats) })
    }
    
    public required convenience init(arrayLiteral elements: CGFloat...) {
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
    
}

public extension ThemeCGFloatPicker {
    
    class func pickerWithKeyPath(_ keyPath: String, map: @escaping (Any?) -> CGFloat?) -> ThemeCGFloatPicker {
        return ThemeCGFloatPicker(v: { map(O2ThemeManager.value(for: keyPath)) })
    }
    
}

@objc public extension ThemeCGFloatPicker {
    
    class func pickerWithKeyPath(_ keyPath: String) -> ThemeCGFloatPicker {
        return ThemeCGFloatPicker(keyPath: keyPath)
    }
    
    class func pickerWithFloats(_ floats: [CGFloat]) -> ThemeCGFloatPicker {
        return ThemeCGFloatPicker(v: { O2ThemeManager.element(for: floats) })
    }
    
}

extension ThemeCGFloatPicker: ExpressibleByArrayLiteral {}
extension ThemeCGFloatPicker: ExpressibleByStringLiteral {}
