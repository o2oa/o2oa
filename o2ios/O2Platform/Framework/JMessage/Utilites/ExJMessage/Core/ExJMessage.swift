//
//  ExJMessage.swift
//  JChat
//
//  Created by 邓永豪 on 2017/10/1.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

public final class ExJMessage<Base> {
    public let base: Base
    public init(_ base: Base) {
        self.base = base
    }
}

/**
 A type that has ExJMessage extensions.
 */
public protocol ExJMessageCompatible { }

public extension ExJMessageCompatible {
    public static var ex: ExJMessage<Self>.Type {
        get { return ExJMessage.self }
    }
    public var ex: ExJMessage<Self> {
        get { return ExJMessage(self) }
    }
}

extension JMSGConversation: ExJMessageCompatible { }
extension JMSGMessage: ExJMessageCompatible { }
extension JMSGOptionalContent: ExJMessageCompatible { }









