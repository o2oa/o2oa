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

    func o2_aspectFitRectForSize(_ size: CGSize) -> CGRect {
        let targetAspect: CGFloat = size.width / size.height
        let sourceAspect: CGFloat = self.size.width / self.size.height
        var rect: CGRect = CGRect.zero
        
        if targetAspect > sourceAspect {
            rect.size.height = size.height
            rect.size.width = ceil(rect.size.height * sourceAspect)
            rect.origin.x = ceil((size.width - rect.size.width) * 0.5)
        } else {
            rect.size.width = size.width
            rect.size.height = ceil(rect.size.width / sourceAspect)
            rect.origin.y = ceil((size.height - rect.size.height) * 0.5)
        }
        
        return rect
    }
    //base64 转UIImage
    public static func base64ToImage(_ source:String,defaultImage:UIImage=UIImage(named: "personDefaultIcon")!) -> UIImage {
        if source != ""{
            let theImage = UIImage(data: Data(base64Encoded:source,options:NSData.Base64DecodingOptions.ignoreUnknownCharacters)!)
            return theImage!
        }else{
            return defaultImage
        }
    }
    
    
    func fixOrientation() -> UIImage {
        var image = self
        if self.imageOrientation == .up {
            return image
        }
        var transform = CGAffineTransform.identity
        switch self.imageOrientation {
        case .down, .downMirrored:
            transform = transform.translatedBy(x: image.size.width, y: image.size.height)
            transform = transform.rotated(by: .pi / 2)
        case .left, .leftMirrored:
            transform = transform.translatedBy(x: image.size.width, y: 0)
            transform = transform.rotated(by: .pi / 2)
        case .right, .rightMirrored :
            transform = transform.translatedBy(x: 0, y: image.size.height)
            transform = transform.rotated(by: -.pi / 2)
        default:
            break
        }
        
        switch self.imageOrientation {
        case .upMirrored, .downMirrored:
            transform = transform.translatedBy(x: image.size.width,y: 0)
            transform = transform.scaledBy(x: -1, y: 1)
        case .rightMirrored, .leftMirrored:
            transform = transform.translatedBy(x: image.size.height,y: 0)
            transform = transform.scaledBy(x: -1, y: 1)
        default:
            break
        }
        
        let ctx = CGContext(data: nil , width: Int(image.size.width), height: Int(image.size.height), bitsPerComponent: image.cgImage!.bitsPerComponent, bytesPerRow: 0, space: image.cgImage!.colorSpace!, bitmapInfo: image.cgImage!.bitmapInfo.rawValue)
        
        ctx!.concatenate(transform)
        
        switch image.imageOrientation {
        case .left, .leftMirrored, .right, .rightMirrored:
            ctx?.draw(image.cgImage!, in: CGRect(x: 0, y: 0, width: image.size.height, height: image.size.width))
        default:
            ctx?.draw(image.cgImage!, in: CGRect(x: 0, y: 0, width: image.size.width, height: image.size.height))
        }
        
        let cgImage = ctx!.makeImage()
        image = UIImage(cgImage: cgImage!)
        return image
    }

    static func createImage(color: UIColor, size: CGSize) -> UIImage? {

        var rect = CGRect(origin: CGPoint.zero, size: size)
        UIGraphicsBeginImageContext(size)
        defer {
            UIGraphicsEndImageContext()
        }
        let context = UIGraphicsGetCurrentContext()
        context?.setFillColor(color.cgColor)
        context?.fill(rect)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        return image
    }

    
    
    func resizeImage(_ newSize: CGSize) -> UIImage {
        UIGraphicsBeginImageContextWithOptions(newSize, false, UIScreen.main.scale)
        self.draw(in: CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height))
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return newImage!
    }
    
    // iOS 9 以后，ImageView.image 设置圆角并不会触发离屏渲染了
    // 可以异步绘制，再主线刷新
    func imageCornerRadius(_ radius: CGFloat) -> UIImage? {
        let rect = CGRect(x: 0, y: 0, width: size.width, height: size.height)
        UIGraphicsBeginImageContextWithOptions(self.size, false, UIScreen.main.scale);
        guard let ctx = UIGraphicsGetCurrentContext() else {
            return nil
        }
        ctx.addPath(UIBezierPath(roundedRect: rect, cornerRadius: radius).cgPath)
        ctx.clip()
        draw(in: rect)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image
    }
    
    func scaledImageFrom3x() -> UIImage {
        let theRate: CGFloat = 1.0 / 3.0
        let oldSize = self.size
        let scaleWidth = CGFloat(oldSize.width) * theRate
        let scaleHeight = CGFloat(oldSize.height) * theRate
        var scaleRect = CGRect.zero
        scaleRect.size = CGSize(width: scaleWidth, height: scaleHeight)
        UIGraphicsBeginImageContextWithOptions(scaleRect.size, false, UIScreen.main.scale)
        draw(in: scaleRect)
        let newImage = UIGraphicsGetImageFromCurrentImageContext()!
        UIGraphicsEndImageContext()
        return newImage
    }
}
 


