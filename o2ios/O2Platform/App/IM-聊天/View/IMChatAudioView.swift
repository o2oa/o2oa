//
//  IMChatAudioView.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/17.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack


protocol IMChatAudioViewDelegate {
    func sendVoice(path: String, voice: Data, duration: String)
    func showAudioRecordingView() //录音状态
    func hideAudioRecordingView() //关闭录音状态
    func changeRecordingView2uplide() //取消发送的状态
    func changeRecordingView2down()// 可以发送的状态
}
class IMChatAudioView: UIView {
    
    @IBOutlet weak var audioViewTitle: UILabel!
    @IBOutlet weak var audioRecordBtn: UIButton!
    @IBOutlet weak var audioBtnContainer: UIView!
    
    private var touchBtn: AudioTouchButton?
    
    private var isCancel = false
    
    private lazy var recordManager: O2RecordVoiceManager = {
        let rm = O2RecordVoiceManager()
        rm.delegate = self
       return rm
    }()
    
    var delegate: IMChatAudioViewDelegate?
    
    override func awakeFromNib() {
//        audioRecordBtn.addTarget(self, action: #selector(startRecord), for: .touchDown)
//        audioRecordBtn.addTarget(self, action: #selector(cancelRecord), for: .touchDragExit)
//        audioRecordBtn.addTarget(self, action: #selector(finishRecord), for: .touchUpInside)
        
        let audioTouchBtn = AudioTouchButton(frame: CGRect(x: 0, y: 0, width: 98, height: 98))
        self.audioBtnContainer.addSubview(audioTouchBtn)
        self.touchBtn = audioTouchBtn
        self.touchBtn?.touchBegan = {
            DDLogDebug("touchBegan 点击............这里开始录音.......")
            self.delegate?.showAudioRecordingView()
            self.startRecord()
        }
        self.touchBtn?.upglide = {
            self.isCancel = true
            self.delegate?.changeRecordingView2uplide()
        }
        self.touchBtn?.down = {
            self.isCancel = false
            self.delegate?.changeRecordingView2down()
        }
        self.touchBtn?.touchEnd = {
            DDLogDebug("touchEnd 点击.........这里结束录音..........")
            self.delegate?.hideAudioRecordingView()
            self.finishRecord()
        }
//        self.touchBtn?.voiceButton?.setTitle("按住说话", for: .normal)
        self.touchBtn?.voiceButton?.setImage(UIImage(named: "chat_mic"), for: .normal)
        self.touchBtn?.backgroundColor = UIColor(hex: "#F3F3F3")
        self.touchBtn?.layer.cornerRadius = 49
        self.touchBtn?.layer.masksToBounds = true
    }
     
    
    @objc private func startRecord() {
        DDLogError("startRecord record...................")
        self.isCancel = false
        self.audioViewTitle.text = "上滑取消发送"
        //开始录音
        recordManager.stopRecordCompletion = {
            DDLogDebug("结束录音！！")
        }
        recordManager.cancelledDeleteCompletion = {
            DDLogDebug("取消录音！")
        }
        recordManager.startRecordingWithPath(O2IMFileManager.shared.getRecorderPath(type: .Caf)) {
            DDLogDebug("开始录音！！！")
        }
    }
    
//    @objc private func cancelRecord() {
//        DDLogError("cancelRecord record...................")
//        self.audioViewTitle.text = "按住说话"
//        self.isCancel = true
//        //取消录音
//        recordManager.cancelledDeleteWithCompletion()
//    }
    @objc private func finishRecord() {
        DDLogError("finish record...................")
        self.audioViewTitle.text = "按住说话"
        if !self.isCancel {
            //录音结束
            recordManager.finishRecordingCompletion()
            if (recordManager.recordDuration! as NSString).floatValue < 1 {
                DispatchQueue.main.async {
                    self.chrysan.show(.error, message: "说话时间太短", hideDelay: 1)
                }
                return
            }
            let filePath = O2IMFileManager.shared.getRecorderPath(type: .MP3)
            recordManager.convertCafToMp3(cafPath: recordManager.recordPath!, mp3Path: filePath)
            let data = try! Data(contentsOf: URL(fileURLWithPath: filePath))
            delegate?.sendVoice(path: filePath, voice: data, duration: recordManager.recordDuration!)
        }else {
            //取消录音
            recordManager.cancelledDeleteWithCompletion()
        }
    }
}

extension IMChatAudioView: O2RecordVoiceDelegate {
    func beyondLimit(_ time: TimeInterval) {
        //录音结束
        recordManager.finishRecordingCompletion()
        if (recordManager.recordDuration! as NSString).floatValue < 1 {
            DispatchQueue.main.async {
                self.chrysan.show(.error, message: "说话时间太短", hideDelay: 1)
            }
            return
        }
        let filePath = O2IMFileManager.shared.getRecorderPath(type: .MP3)
        recordManager.convertCafToMp3(cafPath: recordManager.recordPath!, mp3Path: filePath)
        let data = try! Data(contentsOf: URL(fileURLWithPath: filePath))
        delegate?.sendVoice(path: filePath, voice: data, duration: recordManager.recordDuration!)
    }
}
