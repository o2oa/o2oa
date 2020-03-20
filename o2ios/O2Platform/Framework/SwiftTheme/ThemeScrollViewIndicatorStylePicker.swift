//
//  ThemeScrollViewIndicatorStylePicker.swift
//  SwiftTheme
//
//  Created by Jonathan Valldejuli on 12/23/18.
//  Copyright © 2018 Gesen. All rights reserved.
//

import UIKit

@objc public final class ThemeScrollViewIndicatorStylePicker: ThemePicker {
    
    public convenience init(keyPath: String) {
        self.init(v: { ThemeScrollViewIndicatorStylePicker.getStyle(stringStyle: O2ThemeManager.string(for: keyPath) ?? "") })
    }
    
    public convenience init(keyPath: String, map: @escaping (Any?) -> UIScrollView.IndicatorStyle?) {
        self.init(v: { map(O2ThemeManager.value(for: keyPath)) })
    }
    
    public convenience init(styles: UIScrollView.IndicatorStyle...) {
        self.init(v: { O2ThemeManager.element(for: styles) })
    }
    
    public required convenience init(arrayLiteral elements: UIScrollView.IndicatorStyle...) {
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
    
    class func getStyle(stringStyle: String) -> UIScrollView.IndicatorStyle {
        switch stringStyle.lowercased() {
        case "default"  : return .default
        case "black"    : return .black
        case "white"    : return .white
        default: return .default
        }
    }
    
}

public extension ThemeScrollViewIndicatorStylePicker {
    
    class func pickerWithKeyPath(_ keyPath: String, map: @escaping (Any?) -> UIScrollView.IndicatorStyle?) -> ThemeScrollViewIndicatorStylePicker {
        return ThemeScrollViewIndicatorStylePicker(v: { map(O2ThemeManager.value(for: keyPath)) })
    }
    
    class func pickerWithStyles(_ styles: [UIScrollView.IndicatorStyle]) -> ThemeScrollViewIndicatorStylePicker {
        return ThemeScrollViewIndicatorStylePicker(v: { O2ThemeManager.element(for: styles) })
    }
    
}

@objc public extension ThemeScrollViewIndicatorStylePicker {
    
    class func pickerWithKeyPath(_ keyPath: String) -> ThemeScrollViewIndicatorStylePicker {
        return ThemeScrollViewIndicatorStylePicker(keyPath: keyPath)
    }
    
    class func pickerWithStringStyles(_ styles: [String]) -> ThemeScrollViewIndicatorStylePicker {
        return ThemeScrollViewIndicatorStylePicker(v: { O2ThemeManager.element(for: styles.map(getStyle)) })
    }
    
}

extension ThemeScrollViewIndicatorStylePicker: ExpressibleByArrayLiteral {}
extension ThemeScrollViewIndicatorStylePicker: ExpressibleByStringLiteral {}

