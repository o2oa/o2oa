//
//  JCLocation.swift
//  JChat
//
//  Created by deng on 2017/5/3.
//  Copyright © 2017年 dengyonghao. All rights reserved.
//

import Foundation

@objc(JCLocation)
public class JCLocation: NSObject {
    
    var country = ""
    var province = ""
    var city = ""
    var area = ""
    var street = ""
    
    var provinceCode = ""
    var cityCode = ""
    var areaCode = ""
    
    func decription() {
        print("\(province): \(provinceCode) \(city): \(cityCode) \(area): \(areaCode)")
    }
    
}
