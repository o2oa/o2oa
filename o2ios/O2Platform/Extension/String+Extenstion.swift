//
//  String+Extenstion.swift
//  o2app
//
//  Created by 刘振兴 on 2017/8/18.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import UIKit

extension String {
    
    
    /// EZSE: Checks if string is empty or consists only of whitespace and newline characters
    public var isBlank: Bool {
        let trimmed = trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmed.isEmpty
    }
    
    /// EZSE: split string using a spearator string, returns an array of string
    public func split(_ separator: String) -> [String] {
        return self.components(separatedBy: separator).filter {
            !$0.trim().isEmpty
        }
    }
    
    /// EZSE: split string with delimiters, returns an array of string
    public func split(_ characters: CharacterSet) -> [String] {
        return self.components(separatedBy: characters).filter {
            !$0.trim().isEmpty
        }
    }
    
    public func trim(trimNewline: Bool = false) ->String {
        if trimNewline {
            return self.trimmingCharacters(in: .whitespacesAndNewlines)
        }
        return self.trimmingCharacters(in: .whitespaces)
    }
    
    /// 字符串时间转 Date
    ///
    /// - Parameter formatter: 字符串时间的格式 yyyy-MM-dd/YYYY-MM-dd/HH:mm:ss/yyyy-MM-dd HH:mm:ss
    /// - Returns: Date
    func toDate(formatter: String) -> Date {
        let dateFormatter = DateFormatter()
        dateFormatter.locale = Locale.current
        dateFormatter.dateFormat = formatter
        let date = dateFormatter.date(from: self)
        return date!
    }
    
    
    var length: Int {
        return self.count
    }
    
    func subString(from: Int, to: Int? = nil) -> String {
        if from >= self.length {
            return self
        }
        let startIndex = self.index(self.startIndex, offsetBy: from)
        if to == nil {
            return String(self[startIndex..<self.endIndex])
        }else {
            if from >= to! {
                return String(self[startIndex..<self.endIndex])
            }else {
                let endIndex = index(self.startIndex, offsetBy: to!)
                return String(self[startIndex..<endIndex])
            }
        }
    }
    
    /// 计算文本的高度
    func textHeight(fontSize: CGFloat, width: CGFloat) -> CGFloat {
        return self.boundingRect(with: CGSize(width: width, height: CGFloat(MAXFLOAT)), options: .usesLineFragmentOrigin, attributes: [.font: UIFont.systemFont(ofSize: fontSize)], context: nil).size.height
    }
    
    // MARK: - URL允许的字符
    var urlEscaped: String {
        return self.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed)!
    }
    
    var urlEncoded: String {
        return self.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
    }
    
    
    // MARK:- 获取字符串的CGSize
    func getSize(with fontSize: CGFloat) -> CGSize {
        let str = self as NSString
        
        let size = CGSize(width: UIScreen.main.bounds.width, height: CGFloat(MAXFLOAT))
        return str.boundingRect(with: size, options: .usesLineFragmentOrigin, attributes: [NSAttributedString.Key.font: UIFont.systemFont(ofSize: fontSize)], context: nil).size
    }
    
    // MARK: - 根据固定宽度获取字符串在label中的size
    func getSizeWithMaxWidth(fontSize:CGFloat, maxWidth: CGFloat) -> CGSize {
        let size = CGSize(width: maxWidth, height: CGFloat(MAXFLOAT))
        return self.boundingRect(with: size, options: .usesLineFragmentOrigin, attributes: [NSAttributedString.Key.font: UIFont.systemFont(ofSize: fontSize)], context: nil).size
    }
    
    // MARK:- 获取文本图片
    func getTextImage(_ size:CGSize,textColor tColor:UIColor,backColor bColor:UIColor,textFont tFont:UIFont) -> UIImage? {
        let label = UILabel(frame: CGRect(origin:CGPoint(x:0,y:0), size: size))
        label.textAlignment = .center
        label.textColor = tColor
        label.font = tFont
        label.text = self
        label.backgroundColor = bColor
        UIGraphicsBeginImageContextWithOptions(label.frame.size, true, 0)
        guard let context = UIGraphicsGetCurrentContext() else { return nil }
        label.layer.render(in: context)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image
    }
    
    subscript(r: Range<Int>) -> String {
        get {
            let startIndex = self.index(self.startIndex, offsetBy: r.lowerBound)
            let endIndex = self.index(self.startIndex, offsetBy: r.upperBound)
            
            return String(self[startIndex..<endIndex])
        }
    }
    
    subscript(r: ClosedRange<Int>) -> String {
        get {
            let startIndex = self.index(self.startIndex, offsetBy: r.lowerBound)
            let endIndex = self.index(self.startIndex, offsetBy: r.upperBound)
            
            return String(self[startIndex...endIndex])
        }
    }
    
    static func randomString(length:Int) -> String {
        let charSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        var c = charSet.map { String($0) }
        var s:String = ""
        for _ in (1...length) {
            s.append(c[Int(arc4random()) % c.count])
        }
        return s
    }

    // MARK:- 获取帐号中的中文名称
    func getChinaName() -> String{
        let userName = self
        var strTemp = ""
        if !userName.isBlank{
              let userNameSplit =  userName.split("@");
              if strTemp == "" {
                 strTemp = userNameSplit[0]
              }else{
                  strTemp = strTemp + "," + userNameSplit[0]
              }
              print(strTemp)
         }
        return strTemp
        
    }

    
}
