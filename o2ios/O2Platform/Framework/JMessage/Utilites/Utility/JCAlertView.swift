//
//  JCAlertView.swift
//  JChat
//
//  Created by deng on 2017/7/18.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCAlertView: NSObject {
    
    fileprivate var alertView: UIAlertView!
    
    private override init() {}
    
    static func bulid() -> JCAlertView {
        let alertView = UIAlertView()
        let alert = JCAlertView()
        alert.alertView = alertView
        return alert
    }

    @discardableResult
    public func setDelegate(_ delegate: AnyObject?) -> JCAlertView {
        alertView.delegate = delegate
        return self
    }

    @discardableResult
    public func setTitle(_ title: String) -> JCAlertView {
        alertView.title = title
        return self
    }

    @discardableResult
    public func setMessage(_ message: String) -> JCAlertView {
        alertView.message = message
        return self
    }

    @discardableResult
    public func setTag(_ tag: Int) -> JCAlertView {
        alertView.tag = tag
        return self
    }

    @discardableResult
    public func addButton(_ buttonTitle: String) -> JCAlertView {
        alertView.addButton(withTitle: buttonTitle)
        return self
    }

    @discardableResult
    public func addCancelButton(_ buttonTitle: String) -> JCAlertView {
        alertView.addButton(withTitle: buttonTitle)
        let count = alertView.numberOfButtons
        alertView.cancelButtonIndex = count - 1
        return self
    }

    @discardableResult
    public func  addImage(_ image: UIImage) -> JCAlertView {
        let imageView = UIImageView()
        let scale = 270 / image.size.width
        imageView.image = image.resizeImage(CGSize(width: image.size.width * scale, height: image.size.height * scale))
        alertView.setValue(imageView, forKey: "accessoryView")
        return self
    }
    
    public func show() {
        alertView.show()
    }

}

// JMessage
extension JCAlertView {

    @discardableResult
    public func setJMessage(_ message: JMSGMessage) -> JCAlertView {
        switch(message.contentType) {
        case .text:
            if message.ex.isBusinessCard {
                self.setMessage(message.ex.businessCardName! + "的名片")
            } else {
                let content = message.content as! JMSGTextContent
                self.setMessage(content.text)
            }
        case .image:
            let content = message.content as! JMSGImageContent
            guard let image = UIImage(contentsOfFile: content.originMediaLocalPath  ?? content.thumbImageLocalPath!) else {
                break
            }
            self.addImage(image)
        case .file:
            let content = message.content as! JMSGFileContent
            if message.ex.isShortVideo {
                self.setMessage("[小视频]")
            } else {
                self.setMessage("[文件] \(content.fileName)")
            }
        case .location:
            let content = message.content as! JMSGLocationContent
            self.setMessage("[位置] " + content.address)
        case .voice:
            self.setMessage("[语音消息]")

        default :
            break
        }
        return self.addCancelButton("取消")
            .addButton("确定")
    }
}
