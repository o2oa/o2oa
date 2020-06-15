//
//  O2IM.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/4.
//  Copyright © 2020 zoneland. All rights reserved.
//

import Foundation

//心跳消息
let o2_im_ws_heartbeat = "heartbeat"


let o2_im_conversation_type_single = "single"
let o2_im_conversation_type_group = "group"

let o2_im_msg_type_text = "text"
let o2_im_msg_type_emoji = "emoji"


//表情的字符串转化为O2Emoji.bundle里面的图片路径 [01] -> im_emotion_01
func o2ImEmojiPath(emojiBody: String) -> String {
    if emojiBody.length == 4 {
        let s = emojiBody.subString(from: 1, to: 3)
        return "im_emotion_\(s)"
    }
    return ""
}
