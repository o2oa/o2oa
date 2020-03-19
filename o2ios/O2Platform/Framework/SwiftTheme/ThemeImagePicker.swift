//
//  ThemeImagePicker.swift
//  SwiftTheme
//
//  Created by Gesen on 2017/1/28.
//  Copyright © 2017年 Gesen. All rights reserved.
//

import UIKit

@objc public final class ThemeImagePicker: ThemePicker {
    
    public convenience init(keyPath: String) {
        self.init(v: { O2ThemeManager.image(for: keyPath) })
    }
    
    public convenience init(keyPath: String, map: @escaping (Any?) -> UIImage?) {
        self.init(v: { map(O2ThemeManager.value(for: keyPath)) })
    }
    
    public convenience init(names: String...) {
        self.init(v: { O2ThemeManager.imageElement(for: names) })
    }
    
    public convenience init(images: UIImage...) {
        self.init(v: { O2ThemeManager.element(for: images) })
    }
    
    public required convenience init(arrayLiteral elements: String...) {
        self.init(v: { O2ThemeManager.imageElement(for: elements) })
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

@objc public extension ThemeImagePicker {
    
    class func pickerWithKeyPath(_ keyPath: String) -> ThemeImagePicker {
        return ThemeImagePicker(keyPath: keyPath)
    }
    
    class func pickerWithKeyPath(_ keyPath: String, map: @escaping (Any?) -> UIImage?) -> ThemeImagePicker {
        return ThemeImagePicker(v: { map(O2ThemeManager.value(for: keyPath)) })
    }
    
    class func pickerWithNames(_ names: [String]) -> ThemeImagePicker {
        return ThemeImagePicker(v: { O2ThemeManager.imageElement(for: names) })
    }
    
    class func pickerWithImages(_ images: [UIImage]) -> ThemeImagePicker {
        return ThemeImagePicker(v: { O2ThemeManager.element(for: images) })
    }
    
}

extension ThemeImagePicker: ExpressibleByArrayLiteral {}
extension ThemeImagePicker: ExpressibleByStringLiteral {}
