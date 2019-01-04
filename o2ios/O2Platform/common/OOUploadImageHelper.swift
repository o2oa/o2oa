//
//  OOUploadImageHelper.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/3/24.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

class OOUploadImageHelper: NSObject {
    
    class var instance:OOUploadImageHelper {
        struct SingletonWrapper {
            static let singleton = OOUploadImageHelper()
        }
        return SingletonWrapper.singleton
    }
    
    override init() {
        
    }
    
    func uploadImageToServer(url toURL:String,imageData:Data){
        
    }
}
