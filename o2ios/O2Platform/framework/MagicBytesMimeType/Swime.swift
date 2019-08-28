//
//  Swime.swift
//  源码来自 https://github.com/sendyhalim/Swime
//  O2Platform
//
//  Created by FancyLou on 2019/8/23.
//  Copyright © 2019 zoneland. All rights reserved.
//

import Foundation



public struct Swime {
    /// File data
    let data: Data
    
    ///  A static method to get the `MimeType` that matches the given file data
    ///
    ///  - returns: Optional<MimeType>
    static public func mimeType(data: Data) -> MimeType? {
        return mimeType(swime: Swime(data: data))
    }
    
    ///  A static method to get the `MimeType` that matches the given bytes
    ///
    ///  - returns: Optional<MimeType>
    static public func mimeType(bytes: [UInt8]) -> MimeType? {
        return mimeType(swime: Swime(bytes: bytes))
    }
    
    ///  Get the `MimeType` that matches the given `Swime` instance
    ///
    ///  - returns: Optional<MimeType>
    static public func mimeType(swime: Swime) -> MimeType? {
        let bytes = swime.readBytes(count: min(swime.data.count, 262))
        
        for mime in MimeType.all {
            if mime.matches(bytes: bytes, swime: swime) {
                return mime
            }
        }
        
        return nil
    }
    
    public init(data: Data) {
        self.data = data
    }
    
    public init(bytes: [UInt8]) {
        self.init(data: Data(bytes))
    }
    
    ///  Read bytes from file data
    ///
    ///  - parameter count: Number of bytes to be read
    ///
    ///  - returns: Bytes represented with `[UInt8]`
    internal func readBytes(count: Int) -> [UInt8] {
        var bytes = [UInt8](repeating: 0, count: count)
        
        data.copyBytes(to: &bytes, count: count)
        
        return bytes
    }
}

