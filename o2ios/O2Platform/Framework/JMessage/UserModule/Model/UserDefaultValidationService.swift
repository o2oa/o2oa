//
//  UserDefaultValidationService.swift
//  JChat
//
//  Created by deng on 2017/8/11.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

final class UserDefaultValidationService: UserValidationService {
    static let sharedValidationService = UserDefaultValidationService()

    private init () {}

    let maxCount = 128
    let minCount = 4
    
    func validateUsername(_ username: String) -> ValidationResult {
        
        if username.isEmpty {
            return .failed(message: "用户名不能为空")
        }
        
        if username.length < minCount || username.length > maxCount {
            return .failed(message: "用户名为4-128位字符")
        }
        
        let fristCharRegex = "^([a-zA-Z0-9])(.*)$"
        let fristCharPredicate = NSPredicate(format: "SELF MATCHES %@", fristCharRegex)
        if !fristCharPredicate.evaluate(with: username) {
            return .failed(message: "用户名以字母或数字开头")
        }
        
        if username.isContainsChinese {
            return .failed(message: "用户名不能包含中文字符")
        }
        
        if username.isExpectations {
            return .ok
        }
        
        return .failed(message: "用户名包含非法字符")
    }
    
    func validatePassword(_ password: String) -> ValidationResult {
        if password.isEmpty {
            return .failed(message: "密码不能为空")
        }
        if password.length < minCount || password.length > maxCount {
            return .failed(message: "密码为4-128位字符")
        }
        return .ok
    }
}
