//
//  ZLTextView.swift
//  O2Platform
//
//  Created by 程剑 on 2017/7/13.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import Foundation
extension UITextView {

    //添加链接文本（链接为空时则表示普通文本）
    func appendLinkString(string:String, withURLString:String = "") {
        //原来的文本内容
        let attrString:NSMutableAttributedString = NSMutableAttributedString()
        attrString.append(self.attributedText)
        
        //新增的文本内容（使用默认设置的字体样式）
        let textFont = UIFont(name: "PingFangTC-Regular", size: 16.0)
        let attrs = [NSAttributedString.Key.foregroundColor : base_color, NSAttributedString.Key.font : textFont!]
        let appendString = NSMutableAttributedString(string: string, attributes:attrs)
        //判断是否是链接文字
        if withURLString != "" {
            let range:NSRange = NSMakeRange(0, appendString.length)
            appendString.beginEditing()
            appendString.addAttribute(NSAttributedString.Key.link, value:withURLString, range:range)
            appendString.endEditing()
        }
        //合并新的文本
        attrString.append(appendString)
        
        //设置合并后的文本
        self.attributedText = attrString
    }
}
