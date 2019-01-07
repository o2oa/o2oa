//
//  UINavigationBar+Flat.swift
//  Segmentio
//
//  Created by Dmitriy Demchenko
//  Copyright © 2016 Yalantis Mobile. All rights reserved.
//

import UIKit

private var flatAssociatedObjectKey: UInt8 = 0

/*
 An extension that adds a "flat" field to UINavigationBar. This flag, when
 enabled, removes the shadow under the navigation bar.
 */

@IBDesignable extension UINavigationBar {
    
    @IBInspectable var flat: Bool {
        get {
            guard let obj = objc_getAssociatedObject(self, &flatAssociatedObjectKey) as? NSNumber else {
                return false
            }
            return obj.boolValue;
        }
        
        set {
            if (newValue) {
                let void = UIImage()
                setBackgroundImage(void, for: .any, barMetrics: .default)
                shadowImage = void
            } else {
                setBackgroundImage(nil, for: .any, barMetrics: .default)
                shadowImage = nil
            }
            objc_setAssociatedObject(self, &flatAssociatedObjectKey, NSNumber(value: newValue as Bool),
                                     objc_AssociationPolicy.OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }
    
}
