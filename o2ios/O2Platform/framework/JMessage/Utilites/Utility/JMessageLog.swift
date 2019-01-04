//
//  Log.swift
//  JChat
//
//  Created by 邓永豪 on 2017/10/5.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

/**
print log
#file       String    包含这个符号的文件的路径
#line       Int       符号出现处的行号
#column     Int       符号出现处的列
#function   String    包含这个符号的方法名字
*/
func printLog<T>(_ message: T,
                    file: String = #file,
                    method: String = #function,
                    line: Int = #line)
{
    #if DEBUG
    print("\((file as NSString).lastPathComponent)[\(line)], \(method): \(message)")
    #endif
}
