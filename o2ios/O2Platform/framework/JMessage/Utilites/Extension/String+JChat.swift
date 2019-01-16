//
//  JCString+JChat.swift
//  JChat
//
//  Created by deng on 2017/2/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

public enum JCFileFormat: Int {
    case document
    case video
    case voice
    case photo
    case other
}

extension String {
    static func getTodayYesterdayString(_ theDate:Date) -> String {
        let formatter = DateFormatter()
        let locale:Locale = Locale(identifier: "zh")
        formatter.locale = locale
        formatter.dateStyle = .short
        formatter.timeStyle = .none
        formatter.doesRelativeDateFormatting = true
        return formatter.string(from: theDate)
    }
    
    static func getPastDateString(_ theDate:Date) -> String {
        let formatter = DateFormatter()
        let locale:Locale = Locale(identifier: "zh")
        formatter.locale = locale
        formatter.dateStyle = .long
        formatter.timeStyle = .none
        formatter.doesRelativeDateFormatting = true
        return formatter.string(from: theDate)
    }
    
    static func getFriendlyDateString(_ timeInterval:TimeInterval, forConversation isShort:Bool) -> String {
        let theDate:Date = Date(timeIntervalSince1970: timeInterval)
        var output = ""
        let theDiff = -theDate.timeIntervalSinceNow
        switch theDiff {
        case theDiff where theDiff < 60.0:
            output = "刚刚"
            break
        case theDiff where theDiff < 60 * 60:
            let minute:Int = Int(theDiff/60)
            output = "\(minute)分钟前"
            break
        default:
            let formatter:DateFormatter = DateFormatter()
            let locale:Locale = Locale(identifier: "zh")
            formatter.locale = locale
            var isTodayYesterday = false
            var isPastLong = false
            
            if theDate.isToday() {
                formatter.dateFormat = FORMAT_TODAY
            } else if theDate.isYesterday() {
                formatter.dateFormat = FORMAT_YESTERDAY
                isTodayYesterday = true
            } else if theDate.isThisWeek() {
                if isShort {
                    formatter.dateFormat = FORMAT_THIS_WEEK_SHORT
                } else {
                    formatter.dateFormat = FORMAT_THIS_WEEK
                }
            } else {
                if isShort {
                    formatter.dateFormat = FORMAT_PAST_SHORT
                } else {
                    formatter.dateFormat = FORMAT_PAST_TIME
                    isPastLong = true
                }
            }
            
            if isTodayYesterday {
                let todayYesterday = String.getTodayYesterdayString(theDate)
                if isShort {
                    output = todayYesterday
                } else {
                    output = formatter.string(from: theDate)
                    output = "\(todayYesterday) \(output)"
                }
            } else {
                output = formatter.string(from: theDate)
                if isPastLong {
                    let thePastDate = String.getPastDateString(theDate)
                    output = "\(thePastDate) \(output)"
                }
            }
            
            break
        }
        return output
    }
    
    static func conversationIdWithConversation(_ conversation:JMSGConversation) -> String {
        var conversationId = ""
        if !conversation.ex.isGroup {
            let user = conversation.target as! JMSGUser
            conversationId = "\(user.username)_0"
        } else {
            let group = conversation.target as! JMSGGroup
            conversationId = "\(group.gid)_1"
        }
        return conversationId
    }
}

extension String {
    static func errorAlert(_ error: NSError) -> String {
        var errorAlert: String = ""

        if error.code > 860000 {
            let  errorcode = JMSGSDKErrorCode(rawValue: Int(error.code))
            switch errorcode! as JMSGSDKErrorCode{
                
            case .jmsgErrorSDKNetworkDownloadFailed:
                errorAlert = "下载失败"
                break
                
            case .jmsgErrorSDKNetworkUploadFailed:
                errorAlert = "上传资源文件失败"
                break
            case .jmsgErrorSDKNetworkUploadTokenVerifyFailed:
                errorAlert = "上传资源文件Token验证失败"
                break
            case .jmsgErrorSDKNetworkUploadTokenGetFailed:
                errorAlert = "获取服务器Token失败"
                break
            case .jmsgErrorSDKDBDeleteFailed:
                errorAlert = "数据库删除失败"
                break
            case .jmsgErrorSDKDBUpdateFailed:
                errorAlert = "数据库更新失败"
                break
            case .jmsgErrorSDKDBSelectFailed:
                errorAlert = "数据库查询失败"
                break
            case .jmsgErrorSDKDBInsertFailed:
                errorAlert = "数据库插入失败"
                break
            case .jmsgErrorSDKParamAppkeyInvalid:
                errorAlert = "appkey不合法"
                break
            case .jmsgErrorSDKParamUsernameInvalid:
                errorAlert = "用户名不合法"
                break
            case .jmsgErrorSDKParamPasswordInvalid:
                errorAlert = "用户密码不合法"
                break
            case .jmsgErrorSDKUserNotLogin:
                errorAlert = "用户没有登录"
                break
            case .jmsgErrorSDKNotMediaMessage:
                errorAlert = "这不是一条媒体消息"
                break
            case .jmsgErrorSDKMediaResourceMissing:
                errorAlert = "下载媒体资源路径或者数据意外丢失"
                break
            case .jmsgErrorSDKMediaCrcCodeIllegal:
                errorAlert = "媒体CRC码无效"
                break
            case .jmsgErrorSDKMediaCrcVerifyFailed:
                errorAlert = "媒体CRC校验失败"
                break
            case .jmsgErrorSDKMediaUploadEmptyFile:
                errorAlert = "上传媒体文件时, 发现文件不存在"
                break
            case .jmsgErrorSDKParamContentInvalid:
                errorAlert = "无效的消息内容"
                break
            case .jmsgErrorSDKParamMessageNil:
                errorAlert = "空消息"
                break
            case .jmsgErrorSDKMessageNotPrepared:
                errorAlert = "消息不符合发送的基本条件检查"
                break
            case .jmsgErrorSDKParamConversationTypeUnknown:
                errorAlert = "未知的会话类型"
                break
            case .jmsgErrorSDKParamConversationUsernameInvalid:
                errorAlert = "会话 username 无效"
                break
            case .jmsgErrorSDKParamConversationGroupIdInvalid:
                errorAlert = "会话 groupId 无效"
                break
            case .jmsgErrorSDKParamGroupGroupIdInvalid:
                errorAlert = "groupId 无效"
                break
            case .jmsgErrorSDKParamGroupGroupInfoInvalid:
                errorAlert = "group 相关字段无效"
                break
            case .jmsgErrorSDKMessageNotInGroup:
                errorAlert = "你已不在该群，无法发送消息"
                break
//            case 810009:
//                errorAlert = "超出群上限"
//                break
            default:
                break
            }
        }
        
        if error.code > 800000 && error.code < 820000  {
            let errorcode = JMSGTcpErrorCode(rawValue: UInt(error.code))
            switch errorcode! {
            case .errorTcpUserNotRegistered:
                errorAlert = "用户名不存在"
                break
            case .errorTcpUserPasswordError:
                errorAlert = "用户名或密码错误"
                break
            default:
                break
            }
            if error.code == 809002 || error.code == 812002 {
                errorAlert = "你已不在该群"
            }
        }
        
        if error.code < 600 {
            let errorcode = JMSGHttpErrorCode(rawValue: UInt(error.code))
            switch errorcode! {
            case .errorHttpServerInternal:
                errorAlert = "服务器端内部错误"
                break
            case .errorHttpUserExist:
                errorAlert = "用户已经存在"
                break
            case .errorHttpUserNotExist:
                errorAlert = "用户不存在"
                break
            case .errorHttpPrameterInvalid:
                errorAlert = "参数无效"
                break
            case .errorHttpPasswordError:
                errorAlert = "密码错误"
                break
            case .errorHttpUidInvalid:
                errorAlert = "内部UID 无效"
                break
            case .errorHttpMissingAuthenInfo:
                errorAlert = "Http 请求没有验证信息"
                break
            case .errorHttpAuthenticationFailed:
                errorAlert = "Http 请求验证失败"
                break
            case .errorHttpAppkeyNotExist:
                errorAlert = "Appkey 不存在"
                break
            case .errorHttpTokenExpired:
                errorAlert = "Http 请求 token 过期"
                break
            case .errorHttpServerResponseTimeout:
                errorAlert = "服务器端响应超时"
                break
            default:
                break
            }
        }
        if error.code == 869999 {
            errorAlert = "网络连接错误"
        }
        if error.code == 898001 {
            errorAlert = "用户名已存在"
        }
        if error.code == 801006 {
            errorAlert = "账号已被禁用"
        }
        if errorAlert == "" {
            errorAlert = "未知错误"
        }
        return errorAlert
    }
}

extension String {

    var length: Int {
        return self.characters.count
    }
    
    var isContainsChinese: Bool {
        let chineseRegex = "^(.*)[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+(.*)$"
        let chinesePredicate = NSPredicate(format: "SELF MATCHES %@", chineseRegex)
        if chinesePredicate.evaluate(with: self) {
            return true
        }
        return false
    }
    
    var isExpectations: Bool {
        let regularExpression = "^([a-zA-Z0-9])[a-zA-Z0-9@_\\-\\.]+$"
        let predicate = NSPredicate(format: "SELF MATCHES %@", regularExpression)
        if predicate.evaluate(with: self) {
            return true
        }
        return false
    }

    
    public func trim(trimNewline: Bool = false) ->String {
        if trimNewline {
            return self.trimmingCharacters(in: .whitespacesAndNewlines)
        }
        return self.trimmingCharacters(in: .whitespaces)
    }
    
    public func py() -> String {
        let mutableString = NSMutableString(string: self)
        //把汉字转为拼音
        CFStringTransform(mutableString, nil, kCFStringTransformToLatin, false)
        //去掉拼音的音标
        CFStringTransform(mutableString, nil, kCFStringTransformStripDiacritics, false)
        let py = String(mutableString)
        return py
    }
    
    public func firstCharacter() -> String {
        let firstCharacter = String(describing: self.first!)
        if firstCharacter.isLetterOrNum() {
            return firstCharacter.uppercased()
        }
        let py = String(describing: firstCharacter.py().first!)
        return py.uppercased()
    }
    
    public func isLetterOrNum() -> Bool {
        if let value = UnicodeScalar(self)?.value {
            if value >= 65 && value <= 90 {
                return true
            }
        }
        return false
    }
    
    static func getRecorderPath() -> String {
        var recorderPath:String? = nil
        let now:Date = Date()
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yy-MMMM-dd"
        recorderPath = "\(NSHomeDirectory())/Documents/"
        
        dateFormatter.dateFormat = "yyyy-MM-dd-hh-mm-ss"
        recorderPath?.append("\(dateFormatter.string(from: now))-MySound.ilbc")
        return recorderPath!
    }
    
    func fileFormat() -> JCFileFormat {
        let docFormat = ["ppt", "pptx", "doc", "docx", "pdf", "xls", "xlsx", "txt", "wps"]
        let videoFormat = ["mp4", "mov", "rm", "rmvb", "wmv", "avi", "3gp", "mkv"]
        let voiceFormat = ["wav", "mp3", "wma", "midi"]
        let photoFormat = ["jpg", "jpeg", "png", "bmp", "gif"]
        if docFormat.contains(self) {
            return .document
        }
        if videoFormat.contains(self) {
            return .video
        }
        if voiceFormat.contains(self) {
            return .voice
        }
        if photoFormat.contains(self) {
            return .photo
        }
        return .other
    }

}

