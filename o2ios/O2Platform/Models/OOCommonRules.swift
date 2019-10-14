//
//  OOCommonRules.swift
//  o2app
//
//  Created by 刘振兴 on 2017/9/11.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import SwiftValidator
public class OOPhoneNumberRule: RegexRule {
    //    let PHONE_REGEX = "^\\d{3}-\\d{3}-\\d{4}$"
    
    /// Phone number regular express string to be used in validation.
    static let regex = "^\\d{11}$"
    
    /**
     Initializes a `PhoneNumberRule` object. Used to validate that a field has a valid phone number.
     
     - parameter message: Error message that is displayed if validation fails.
     - returns: An initialized `PasswordRule` object, or nil if an object could not be created for some reason that would not result in an exception.
     */
    public convenience init(message : String = "请输入11位手机号码") {
        self.init(regex: OOPhoneNumberRule.regex, message : message)
    }
    
}
