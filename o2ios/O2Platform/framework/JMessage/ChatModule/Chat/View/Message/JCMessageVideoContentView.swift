//
//  JCMessageVideoContentView.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import AVKit
import AVFoundation

open class JCMessageVideoContentView: UIImageView, JCMessageContentViewType {
    
    public override init(image: UIImage?) {
        super.init(image: image)
        _commonInit()
    }
    public override init(image: UIImage?, highlightedImage: UIImage?) {
        super.init(image: image, highlightedImage: highlightedImage)
        _commonInit()
    }
    public override init(frame: CGRect) {
        super.init(frame: frame)
        _commonInit()
    }
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _commonInit()
    }
    
    open func apply(_ message: JCMessageType) {
        guard message.content is JCMessageVideoContent else {
            return
        }
        _message = message
        let content = message.content as! JCMessageVideoContent
        _delegate = content.delegate
        if content.data != nil {
            _data = content.data
            if content.image != nil {
                self.image = content.image
            }else {
                DispatchQueue.main.async {
                    self.image = JCVideoManager.getFristImage(data: self._data!)
                }
            }
        }else{
            //self.image = UIImage.createImage(color: UIColor.init(hex: "0xCDD0D1"), size: self.size)
            content.fileContent?.fileData({ (data, id, error) in
                if data != nil {
                    self._data = data
                    DispatchQueue.main.async {
                        self.image = JCVideoManager.getFristImage(data: self._data!)
                    }
                }
            })
        }
//
        _playImageView.center = CGPoint(x: self.width/2, y: self.height/2)
        
        
//        guard let content = message.content as? JCMessageVideoContent else {
//            return
//        }
//        _message = message
//        _delegate = content.delegate
//        if content.data != nil {
//            _data = content.data
//            if content.image != nil {
//                self.image = content.image
//            } else {
//                DispatchQueue.main.async {
//                    self.image = JCVideoManager.getFristImage(data: self._data!)
//                }
//            }
//        } else {
//            self.image = UIImage.createImage(color: UIColor(netHex: 0xCDD0D1), size: self.size)
//            content.fileContent?.fileData({ (data, id, error) in
//                if data != nil {
//                    self._data = data
//                    DispatchQueue.main.async {
//                        self.image = JCVideoManager.getFristImage(data: self._data!)
//                    }
//                }
//            })
//        }
//        _playImageView.center = CGPoint(x: self.width / 2, y: self.height / 2)
    }

    private weak var _delegate: JCMessageDelegate?
    private var _data: Data?
    private var _playImageView: UIImageView!
    private var _message: JCMessageType!
    
    private func _commonInit() {
        isUserInteractionEnabled = true
        layer.cornerRadius = 2
        layer.masksToBounds = true
        _tapGesture()
        _playImageView = UIImageView(frame: CGRect(x: 0, y: 50, width: 41, height: 41))
        _playImageView.image = UIImage.loadImage("com_icon_play")
        addSubview(_playImageView)
    }
    
    func _tapGesture() {
        let tap = UITapGestureRecognizer(target: self, action: #selector(_clickCell))
        tap.numberOfTapsRequired = 1
        addGestureRecognizer(tap)
    }
    
    func _clickCell() {
        _delegate?.message?(message: _message, videoData: _data)
    }
}
