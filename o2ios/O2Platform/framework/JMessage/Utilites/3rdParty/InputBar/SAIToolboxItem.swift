//
//  SAIToolboxItem.swift
//  SAC
//
//  Created by SAGESSE on 9/15/16.
//  Copyright © 2016-2017 SAGESSE. All rights reserved.
//

import UIKit

@objc open class SAIToolboxItem: NSObject {
    
    open var name: String
    open var identifier: String
    
    open var image: UIImage?
    open var highlightedImage: UIImage?
    
    public init(_ identifier: String, _ name: String, _ image: UIImage?, _ highlightedImage: UIImage? = nil) {
        self.identifier = identifier
        self.name = name
        self.image = image
        self.highlightedImage = highlightedImage
    }
}
