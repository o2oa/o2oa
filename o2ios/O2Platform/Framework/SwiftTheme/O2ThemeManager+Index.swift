//
//  ThemeManager+Index.swift
//  SwiftTheme
//
//  Created by Gesen on 16/9/18.
//  Copyright © 2016年 Gesen. All rights reserved.
//

import UIKit

extension O2ThemeManager {
    
    public class func colorElement(for array: [String]) -> UIColor? {
        guard let rgba = element(for: array) else { return nil }
        guard let color = try? UIColor(rgba_throws: rgba as String) else {
            print("SwiftTheme WARNING: Not convert rgba \(rgba) in array: \(array)[\(currentThemeIndex)]")
            return nil
        }
        return color
    }
    
    public class func imageElement(for array: [String]) -> UIImage? {
        guard let imageName = element(for: array) else { return nil }
        guard let image = UIImage(named: imageName as String) else {
            print("SwiftTheme WARNING: Not found image name '\(imageName)' in array: \(array)[\(currentThemeIndex)]")
            return nil
        }
        return image
    }
    
    public class func element<T>(for array: [T]) -> T? {
        let index = O2ThemeManager.currentThemeIndex
        guard  array.indices ~= index else {
            print("SwiftTheme WARNING: Not found element in array: \(array)[\(currentThemeIndex)]")
            return nil
        }
        return array[index]
    }
    
}
