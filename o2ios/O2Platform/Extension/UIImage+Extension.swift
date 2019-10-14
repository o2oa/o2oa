//
//  UIImage+Extension.swift
//  O2Platform
//
//  Created by FancyLou on 2018/11/21.
//  Copyright © 2018 zoneland. All rights reserved.
//

import UIKit

extension UIImage {
    
    /// EZSE: scales image
    public class func scaleTo(image: UIImage, w: CGFloat, h: CGFloat) -> UIImage {
        let newSize = CGSize(width: w, height: h)
        UIGraphicsBeginImageContextWithOptions(newSize, false, 0.0)
        image.draw(in: CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height))
        let newImage: UIImage = UIGraphicsGetImageFromCurrentImageContext()!
        UIGraphicsEndImageContext()
        return newImage
    }

}
 


