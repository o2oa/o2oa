//
//  ImageUtil.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/30.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
extension UIImage{
    public static func base64ToImage(_ source:String,defaultImage:UIImage=UIImage(named: "personDefaultIcon")!) -> UIImage {
        if source != ""{
            let theImage = UIImage(data: Data(base64Encoded:source,options:NSData.Base64DecodingOptions.ignoreUnknownCharacters)!)
            return theImage!
        }else{
            return defaultImage
        }
    }
    
    class func image(color: UIColor, size: CGSize) -> UIImage {
        
        let rect = CGRect.init(x: 0, y: 0, width: size.width, height: size.height)
        UIGraphicsBeginImageContext(rect.size)
        
        let context = UIGraphicsGetCurrentContext()
        
        context?.setFillColor(color.cgColor)
        context?.fill(rect)
        
        let img = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return img!
    }
}
