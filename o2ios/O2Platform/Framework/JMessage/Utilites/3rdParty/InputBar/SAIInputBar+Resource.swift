//
//  SAInputBar+Resource.swift
//  SIMChat
//
//  Created by SAGESSE on 8/31/16.
//  Copyright © 2016-2017 SAGESSE. All rights reserved.
//

import UIKit

public enum SAIInputBarType: Int {
    case `default` // wx: center
    case value1  // qq: center + bottom
    case value2  // qqzone: center + top
}

public extension SAIInputBar {
    public convenience init(type: SAIInputBarType) {
        self.init()
        
        switch type {
        case .default: _setupForDefault()
        case .value1: _setupForValue1()
        case .value2: _setupForValue2()
        }
    }
    
    private func _barItem(_ identifier: String, _ nName: String, _ hName: String) -> SAIInputItem {
        let item = SAIInputItem()
        
        item.identifier = identifier
        item.size = CGSize(width: 34, height: 34)
        
        let nImage = UIImage.loadImage(nName)
        let hImage = UIImage.loadImage(hName)
        item.setImage(nImage, for: [.normal])
        item.setImage(hImage, for: [.highlighted])
        item.setImage(hImage, for: [.selected, .normal])

        return item
    }
    
    private func _setupForDefault() {
        
        let lbs = [
            _barItem("kb:audio", "YH_KB_Voice", "YH_KB_Keyboard"),
        ]
        let rbs = [
            _barItem("kb:emoticon", "YH_KB_Emotion", "YH_KB_Emotion"),
            _barItem("kb:toolbox", "YH_KB_More", "YH_KB_More"),
        ]
        setBarItems(lbs, atPosition: .left)
        setBarItems(rbs, atPosition: .right)
    }
    private func _setupForValue1() {
        
        let bbs = [
            _barItem("kb:audio", "YH_KB_More", "YH_KB_More"),
            
            _barItem("kb:video", "YH_KB_More", "YH_KB_More"),
            _barItem("kb:photo", "chat_bottom_photo_nor", "chat_bottom_photo_press"),
            _barItem("kb:camera", "chat_bottom_Camera_nor", "chat_bottom_Camera_press"),
            _barItem("page:red_pack", "YH_KB_More", "YH_KB_More"),
            
            _barItem("kb:emoticon", "chat_bottom_emoticon_nor", "chat_bottom_emoticon_press"),
            _barItem("kb:toolbox", "YH_KB_More", "YH_KB_More"),
        ]
        bbs.first?.alignment = .left
        bbs.last?.alignment = .right
        setBarItems(bbs, atPosition: .bottom)
    }
    private func _setupForValue2() {
        
    }
}
