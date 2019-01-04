//
//  JCMessageVoiceContentView.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import AVFoundation

open class JCMessageVoiceContentView: UIView, JCMessageContentViewType {
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        _commonInit()
    }
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _commonInit()
    }
    
    deinit {
        JCAudioPlayerHelper.sharedInstance.stopAudio()
    }

    
    open func apply(_ message: JCMessageType) {
        guard let content = message.content as? JCMessageVoiceContent else {
            return
        }
        _duration = Double(content.duration)
        _message = message
        _delegate = content.delegate
        _updateViewLayouts(message.options)
        _data = content.data
        _titleLabel.attributedText = content.attributedText
    }
    
    private func _updateViewLayouts(_ options: JCMessageOptions) {
        guard _alignment != options.alignment else {
            return
        }
        _alignment = options.alignment
        
        let aw = CGFloat(20)
        let tw = bounds.maxX - 8 - aw
        
        if _alignment == .left {
            _titleLabel.textColor = UIColor(netHex: 0x999999)
            _titleLabel.textAlignment = .right
            _animationView.image = UIImage.loadImage("chat_voice_receive_icon_3")
            _animationView.animationImages = [
                UIImage.loadImage("chat_voice_receive_icon_1"),
                UIImage.loadImage("chat_voice_receive_icon_2"),
                UIImage.loadImage("chat_voice_receive_icon_3"),
            ].flatMap { $0 }
            
            _animationView.frame = CGRect(x: bounds.minX, y: 3, width: aw, height: 20)
            _titleLabel.frame = CGRect(x: bounds.maxX - tw, y: 3, width: tw, height: 20)
        } else {
            _titleLabel.textColor = UIColor(netHex: 0x4D9999)
            _titleLabel.textAlignment = .left
            _animationView.image = UIImage.loadImage("chat_voice_send_icon_3")
            _animationView.animationImages = [
                UIImage.loadImage("chat_voice_send_icon_1"),
                UIImage.loadImage("chat_voice_send_icon_2"),
                UIImage.loadImage("chat_voice_send_icon_3"),
            ].flatMap { $0 }
            
            _animationView.frame = CGRect(x: bounds.maxX - aw, y: 3, width: aw, height: 20)
            _titleLabel.frame = CGRect(x: bounds.minX, y: 3, width: tw, height: 20)
        }
    }
    
    private func _commonInit() {
        _animationView.animationDuration = 0.8
        _animationView.animationRepeatCount = 0
        addSubview(_animationView)
        addSubview(_titleLabel)
        _tapGesture()
    }
    
    func _tapGesture() {
        let tap = UITapGestureRecognizer(target: self, action: #selector(_clickCell))
        tap.numberOfTapsRequired = 1
        addGestureRecognizer(tap)
    }
    
    func _clickCell() {
//        _delegate?.message?(message: _message, voiceData: _data, duration: _duration)

        // TODO: 这里不就应该把 V 层代码放在这里，需要考虑下这个播放时开启动画的传递方式
        if let player = JCAudioPlayerHelper.sharedInstance.player {
            if player.isPlaying {
                JCAudioPlayerHelper.sharedInstance.stopAudio()
                return
            }
        }
        JCAudioPlayerHelper.sharedInstance.delegate = self
        JCAudioPlayerHelper.sharedInstance.managerAudioWithData(_data!, toplay: true)
        _animationView.startAnimating()
    }

    private var _duration: Double = 0.0
    private weak var _delegate: JCMessageDelegate?
    private var _message: JCMessageType!
    fileprivate lazy var _animationView: UIImageView = UIImageView()
    private lazy var _titleLabel: UILabel = UILabel()
    private var _data: Data?
    private var _alignment: JCMessageAlignment = .center
}

extension JCMessageVoiceContentView: JCAudioPlayerHelperDelegate {
    func didAudioPlayerStopPlay(_ AudioPlayer: AVAudioPlayer) {
        _animationView.stopAnimating()
    }
    func didAudioPlayerBeginPlay(_ AudioPlayer: AVAudioPlayer) {
        
    }
    func didAudioPlayerPausePlay(_ AudioPlayer: AVAudioPlayer) {
        _animationView.stopAnimating()
    }
}
