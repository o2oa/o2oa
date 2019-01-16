//
//  Data+Extension.swift
//  O2Platform
//
//  Created by FancyLou on 2018/11/20.
//  Copyright © 2018 zoneland. All rights reserved.
//

import Foundation


//ext Data
extension Data {
    var hexString: String {
        return withUnsafeBytes {(bytes: UnsafePointer<UInt8>) -> String in
            let buffer = UnsafeBufferPointer(start: bytes, count: count)
            return buffer.map {String(format: "%02hhx", $0)}.reduce("", { $0 + $1 })
        }
    }
}
