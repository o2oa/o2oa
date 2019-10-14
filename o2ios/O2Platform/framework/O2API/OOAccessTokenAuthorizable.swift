//
//  OOAccessTokenAuthorizable.swift
//  o2app
//
//  Created by 刘振兴 on 2017/9/25.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation


public protocol OOAccessTokenAuthorizable {
    var shouldAuthorize: Bool { get }
}
