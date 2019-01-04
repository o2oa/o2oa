//
//  File.swift
//  JChat
//
//  Created by deng on 2017/3/22.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

@inline(__always)
internal func _JCLayoutConstraintMake(_ item: AnyObject, _ attr1: NSLayoutConstraint.Attribute, _ related: NSLayoutConstraint.Relation, _ toItem: AnyObject? = nil, _ attr2: NSLayoutConstraint.Attribute = .notAnAttribute, _ constant: CGFloat = 0, priority: UILayoutPriority = UILayoutPriority.init(1000.0), multiplier: CGFloat = 1, output: UnsafeMutablePointer<NSLayoutConstraint?>? = nil) -> NSLayoutConstraint {
    
    if let view = item as? UIView {
        view.translatesAutoresizingMaskIntoConstraints = false
    }
    
    let c = NSLayoutConstraint(item:item, attribute:attr1, relatedBy:related, toItem:toItem, attribute:attr2, multiplier:multiplier, constant:constant)
    c.priority = priority
    if output != nil {
        output?.pointee = c
    }
    
    return c
}
