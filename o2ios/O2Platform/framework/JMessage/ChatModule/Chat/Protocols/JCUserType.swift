//
//  JCUserType.swift
//  JChat
//
//  Created by deng on 10/04/2017.
//  Copyright © 2017 HXHG. All rights reserved.
//

import UIKit

@objc public protocol JCUserType: class {
    var identifier: String { get }
    var name: String? { get }
    var portrait: UIImage? { get }
}
