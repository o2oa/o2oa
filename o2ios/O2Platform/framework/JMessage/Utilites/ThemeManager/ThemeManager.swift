//
//  ThemeManager.swift
//  ThemeDemo
//
//  Created by 邓永豪 on 2017/8/23.
//  Copyright © 2017年 dengyonghao. All rights reserved.
//

import UIKit

let kUpdateTheme = "kUpdateTheme"
let kThemeStyle = "kThemeStyle"

final class ThemeManager: NSObject {
    var style: ThemeStyle {
        return themeStyle
    }
    
    static var instance = ThemeManager()
    
    private var themeBundleName: String {
        switch themeStyle {
        case .black:
            return "blackTheme"
        case .o2:
            return "o2Theme"
        default:
            return "defaultTheme"
        }
    }
    // 缓存 image 到内存中，提高重复访问的速度
    private let memoryCache = NSCache<NSString, UIImage>()
    private var themeStyle: ThemeStyle = .o2
    private var themeColors: NSDictionary?
    
    private override init() {
        super.init()
        if let style = UserDefaults.standard.object(forKey: kThemeStyle) as? Int {
            themeStyle = ThemeStyle(rawValue: style)!
        } else {
            UserDefaults.standard.set(themeStyle.rawValue, forKey: kThemeStyle)
            UserDefaults.standard.synchronize()
        }
        
        themeColors = getThemeColors()
        // 收到内存警告时，移除所有缓存
        NotificationCenter.default.addObserver(
            self, selector: #selector(clearMemoryCache), name: UIApplication.didReceiveMemoryWarningNotification, object: nil)
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    @objc private func clearMemoryCache() {
        memoryCache.removeAllObjects()
    }
    
    private func getThemeColors() -> NSDictionary? {
        
        let bundleName = themeBundleName
        
        guard let themeBundlePath = Bundle.path(forResource: bundleName, ofType: "bundle", inDirectory: Bundle.main.bundlePath) else {
            return nil
        }
        guard let themeBundle = Bundle(path: themeBundlePath) else {
            return nil
        }
        guard let path = themeBundle.path(forResource: "themeColor", ofType: "txt") else {
            return nil
        }
        
        let url = URL(fileURLWithPath: path)
        let data = try! Data(contentsOf: url)
        
        do {
            return try JSONSerialization.jsonObject(with: data, options: [JSONSerialization.ReadingOptions(rawValue: 0)]) as? NSDictionary
        } catch {
            return nil
        }

    }
    
    public func updateThemeStyle(_ style: ThemeStyle) {
        if themeStyle.rawValue == style.rawValue {
            return
        }
        themeStyle = style
        UserDefaults.standard.set(style.rawValue, forKey: kThemeStyle)
        UserDefaults.standard.synchronize()
        themeColors = getThemeColors()
        memoryCache.removeAllObjects()
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: kUpdateTheme), object: nil)
    }
    
    public func themeColor(_ colorName: String) -> Int {
        guard let hexString = themeColors?.value(forKey: colorName) as? String else {
            assert(true, "Invalid color key")
            return 0
        }
        let colorValue = Int(strtoul(hexString, nil, 16))
        return colorValue
    }

    // MARK: load image
    public func loadImage(_ imageName: String) -> UIImage? {
        return loadImage(imageName, themeStyle)
    }

    public func loadImage(_ imageName: String, _ style: ThemeStyle) -> UIImage? {

        if imageName.isEmpty || imageName.count == 0 {
            return nil
        }

        var nameAndType = imageName.components(separatedBy: ".")
        var name = nameAndType.first!
        let type = nameAndType.count > 1 ? nameAndType[1] : "png"

        if let image = memoryCache.object(forKey: name as NSString) {
            return image
        }

        guard let themeBundlePath = Bundle.path(forResource: themeBundleName, ofType: "bundle", inDirectory: Bundle.main.bundlePath) else {
            return nil
        }
        guard let themeBundle = Bundle(path: themeBundlePath) else {
            return nil
        }

        var isImageUnder3x = false
        var imagePath  =  themeBundle.path(forResource: "image/" + name, ofType: type)
        let nameLength = name.count

        if imagePath == nil && name.hasSuffix("@2x") && nameLength > 3 {
            let index = name.index(name.endIndex, offsetBy: -3)
            name = name.substring(with: (name.startIndex ..< index))
        }

        if imagePath == nil && !name.hasSuffix("@2x") {
            let name2x = name + "@2x";
            imagePath = themeBundle.path(forResource: "image/" + name2x, ofType: type)
            if imagePath == nil && !name.hasSuffix("3x") {
                let name3x = name + "@3x"
                imagePath = themeBundle.path(forResource: "image/" + name3x, ofType: type)
                isImageUnder3x = true
            }
        }

        var image: UIImage?
        if let imagePath = imagePath {
            image = UIImage(contentsOfFile: imagePath)
        } else if style != .default {
            // 如果当前 bundle 里面不存在这张图片的路径，那就去默认的 bundle 里面找，
            // 为什么要这样做呢，因为部分资源在不同 theme 中是一样的，就不需要导入重复的资源，使应用包的大小变大
            image = loadImage(imageName, .default)
        }
        if #available(iOS 8, *){} else {
            if isImageUnder3x {
                image = image?.scaledImageFrom3x()
            }
        }
        if let image = image {
            memoryCache.setObject(image, forKey: name as NSString)
        }
        return image
    }

}

extension UIImage {

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


