//
//  Protocols.swift
//  JChat
//
//  Created by deng on 2017/8/11.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

func == (lhs: ValidationResult, rhs: ValidationResult) -> Bool {
    switch (lhs, rhs) {
    case (.ok, .ok):
        return true
    case (.empty, .empty):
        return true
    case (.validating, .validating):
        return true
    case (.failed, .failed):
        return true
    default:
        return false
    }
}

enum ValidationResult: CustomStringConvertible, Equatable {
    case ok
    case empty
    case validating
    case failed(message: String)
    
    var description: String {
        switch self {
        case .ok:
            return ""
        case .empty:
            return ""
        case .validating:
            return "validating ..."
        case let .failed(message):
            return message
        }
    }
}

protocol UserValidationService {
    func validateUsername(_ username: String) -> ValidationResult
    func validatePassword(_ password: String) -> ValidationResult
}
