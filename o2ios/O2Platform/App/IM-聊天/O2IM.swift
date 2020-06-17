//
//  O2IM.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/4.
//  Copyright © 2020 zoneland. All rights reserved.
//

import Foundation
import Promises
import CocoaLumberjack

//心跳消息
let o2_im_ws_heartbeat = "heartbeat"


let o2_im_conversation_type_single = "single"
let o2_im_conversation_type_group = "group"

//消息分类
let o2_im_msg_type_text = "text"
let o2_im_msg_type_emoji = "emoji"
let o2_im_msg_type_image = "image"
let o2_im_msg_type_audio = "audio"

//消息body
let o2_im_msg_body_image = "[图片]"
let o2_im_msg_body_audio = "[语音]"
let o2_im_msg_body_video = "[视频]"


//表情的字符串转化为O2Emoji.bundle里面的图片路径 [01] -> im_emotion_01
func o2ImEmojiPath(emojiBody: String) -> String {
    if emojiBody.length == 4 {
        let s = emojiBody.subString(from: 1, to: 3)
        return "im_emotion_\(s)"
    }
    return ""
}

class O2IMFileManager {
    static let shared: O2IMFileManager = {
        return O2IMFileManager()
    }()

    private let communicateAPI = {
        return OOMoyaProvider<CommunicateAPI>()
    }()

    private init() { }
    //根据id下载文件，并返回文件的本地url
    func getFileLocalUrl(fileId: String) -> Promise<URL> {
        return Promise { fulfill, reject in
            let url = self.localFilePath(fileId: fileId)
            if FileUtil.share.fileExist(filePath: url.path) {
                fulfill(url)
            } else {
                self.communicateAPI.request(.imDownloadFullFile(fileId), completion: { result in
                        switch result {
                        case .success(_):
                            DDLogError("下载成功。。。。。\(fileId)")
                            fulfill(url)
                            break
                        case .failure(let err):
                            DDLogError(err.localizedDescription)
                            reject(err)
                            break
                        }
                    })
            }
        }

    }

    func localFilePath(fileId: String) -> URL {
        return FileUtil.share.cacheDir().appendingPathComponent("\(fileId).png")
    }
    
    //音频文件存储地址
    func getRecorderPath(type: RecordType) -> String {
        var recorderPath = FileUtil.share.cacheDir()
        recorderPath.appendPathComponent("o2im")
        //目录不存在就创建
        DDLogDebug("开始创建目录\(recorderPath.path)")
        FileUtil.share.createDirectory(path: recorderPath.path)
        let now:Date = Date()
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd-hh-mm-ss"
        let fileName = (type == RecordType.Caf) ? "\(dateFormatter.string(from: now))-MySound.caf" : "\(dateFormatter.string(from: now))-MySound.mp3"
        recorderPath.appendPathComponent(fileName)
        return recorderPath.path
    }
    
}


enum RecordType :String {
    case Caf = "caf"
    case MP3 = "mp3"
}
