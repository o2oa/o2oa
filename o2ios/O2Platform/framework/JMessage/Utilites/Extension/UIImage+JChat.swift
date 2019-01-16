//
//  UIImage+JChat.swift
//  JChat
//
//  Created by deng on 2017/6/20.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

extension UIImage {
    
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

    
    static func getMyAvator() -> UIImage? {
        if let data = UserDefaults.standard.object(forKey: kLastUserAvator) as? Data {
            let avatorData = NSKeyedUnarchiver.unarchiveObject(with: data) as! Data
            return UIImage(data: avatorData)
        }
        return nil
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

}
