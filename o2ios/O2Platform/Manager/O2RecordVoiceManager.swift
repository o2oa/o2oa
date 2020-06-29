//
//  O2RecordVoiceManager.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/17.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import AVFoundation


fileprivate func < <T : Comparable>(lhs: T?, rhs: T?) -> Bool {
    switch (lhs, rhs) {
    case let (l?, r?):
        return l < r
    case (nil, _?):
        return true
    default:
        return false
    }
}

fileprivate func > <T : Comparable>(lhs: T?, rhs: T?) -> Bool {
    switch (lhs, rhs) {
    case let (l?, r?):
        return l > r
    default:
        return rhs < lhs
    }
}

typealias O2RecordCompletionCallBack = () -> Void

protocol O2RecordVoiceDelegate {
    func beyondLimit(_ time: TimeInterval)
}

class O2RecordVoiceManager: NSObject {
    private let maxRecordTime = 60.0

    private var startRecordCompleted: O2RecordCompletionCallBack?
    private var recorder: AVAudioRecorder?
    private var recordProgress: Float?
    private var theTimer: Timer?
    private var currentTimeInterval: TimeInterval?
    
    var recordPath: String?
    var recordDuration: String?
    var stopRecordCompletion: O2RecordCompletionCallBack?
    var cancelledDeleteCompletion: O2RecordCompletionCallBack?
    var delegate: O2RecordVoiceDelegate?
    
    override init() {
        super.init()
    }
    
    deinit {
        stopRecord()
        recordPath = nil
    }
    
    @objc private func updateMeters() {
        if recorder == nil {
            return
        }
        currentTimeInterval = recorder?.currentTime
        
        recordProgress = recorder?.peakPower(forChannel: 0)
        if currentTimeInterval > maxRecordTime {
            stopRecord()
            delegate?.beyondLimit(currentTimeInterval!)
            if stopRecordCompletion != nil {
                DispatchQueue.main.async(execute: stopRecordCompletion!)
                recorder?.updateMeters()
            }
        }
    }
    
    private func getVoiceDuration(_ recordPath:String) {
        do {
            let player:AVAudioPlayer = try AVAudioPlayer(contentsOf: URL(fileURLWithPath: recordPath))
            player.play()
            let duration = player.duration
            self.recordDuration = "\(Int(duration))"
        } catch let error as NSError {
            print("get AVAudioPlayer is fail \(error)")
            self.recordDuration = "0"
        }
    }
    
    private func resetTimer() {
        if theTimer == nil {
            return
        } else {
            theTimer!.invalidate()
            theTimer = nil
        }
    }
    
    private func cancelRecording() {
        if recorder == nil {
            return
        }
        if recorder?.isRecording != false {
            recorder?.stop()
        }
        recorder = nil
    }
    
    private func stopRecord() {
        cancelRecording()
        resetTimer()
    }
    
    func startRecordingWithPath(_ path:String, startRecordCompleted:@escaping O2RecordCompletionCallBack) {
        print("Action - startRecordingWithPath:")
        self.startRecordCompleted = startRecordCompleted
        self.recordPath = path
        
        let audioSession:AVAudioSession = AVAudioSession.sharedInstance()
        do {
            try audioSession.setCategory(AVAudioSession.Category.playAndRecord, mode: .default, options: .defaultToSpeaker)
        } catch let error as NSError {
            print("could not set session category")
            print(error.localizedDescription)
        }
        
        do {
            try audioSession.setActive(true)
        } catch let error as NSError {
            print("could not set session active")
            print(error.localizedDescription)
        }
        
//        let recordSettings:[String : AnyObject] = [
//            AVFormatIDKey: NSNumber(value: kAudioFormatAppleIMA4 as UInt32),
//            AVNumberOfChannelsKey: 1 as AnyObject,
//            AVSampleRateKey : 16000.0 as AnyObject
//        ]
        let recordSetting: [String: Any] = [
            AVSampleRateKey: NSNumber(value: 16000),//采样率
            AVEncoderBitRateKey:NSNumber(value: 16000),
            AVFormatIDKey: NSNumber(value: kAudioFormatLinearPCM),//音频格式
            AVNumberOfChannelsKey: NSNumber(value: 1),//通道数
            AVLinearPCMBitDepthKey:NSNumber(value: 16),
            AVEncoderAudioQualityKey: NSNumber(value: AVAudioQuality.high.rawValue)//录音质量
        ]
        
        do {
            self.recorder = try AVAudioRecorder(url: URL(fileURLWithPath: self.recordPath!), settings: recordSetting)
            self.recorder!.delegate = self
            self.recorder!.prepareToRecord()
            self.recorder?.record(forDuration: 160.0)
        } catch let error as NSError {
            recorder = nil
            print(error.localizedDescription)
        }
        
        if ((self.recorder?.record()) != false) {
            self.resetTimer()
            self.theTimer = Timer.scheduledTimer(timeInterval: 0.05, target: self, selector: #selector(updateMeters), userInfo: nil, repeats: true)
        } else {
            print("fail record")
        }
        
        if self.startRecordCompleted != nil {
            DispatchQueue.main.async(execute: self.startRecordCompleted!)
        }
    }
    
    func finishRecordingCompletion() {
        stopRecord()
        getVoiceDuration(recordPath!)
        
        if stopRecordCompletion != nil {
            DispatchQueue.main.async(execute: stopRecordCompletion!)
        }
    }
    
    func cancelledDeleteWithCompletion() {
        stopRecord()
        if recordPath != nil {
            let fileManager:FileManager = FileManager.default
            if fileManager.fileExists(atPath: recordPath!) == true {
                do {
                    try fileManager.removeItem(atPath: recordPath!)
                } catch let error as NSError {
                    print("can no to remove the voice file \(error.localizedDescription)")
                }
            } else {
                if cancelledDeleteCompletion != nil {
                    DispatchQueue.main.async(execute: cancelledDeleteCompletion!)
                }
            }
            
        }
    }
    // test player
    func playVoice(_ recordPath:String) {
        do {
            print("\(recordPath)")
            let player:AVAudioPlayer = try AVAudioPlayer(contentsOf: URL(fileURLWithPath: recordPath))
            player.volume = 1
            player.delegate = self
            player.numberOfLoops = -1
            player.prepareToPlay()
            player.play()
            
        } catch let error as NSError {
            print("get AVAudioPlayer is fail \(error)")
        }
    }
    //caf文件转mp3
    func convertCafToMp3(cafPath: String, mp3Path: String){
        ConvertMp3().audioPCMtoMP3(cafPath, mp3File: mp3Path)
        print("caf源文件:\(cafPath)")
        print("mp3文件:\(mp3Path)")
    }
    
    
}

extension O2RecordVoiceManager: AVAudioPlayerDelegate, AVAudioRecorderDelegate {
    func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool) {
        print("finished playing \(flag)")
        
    }

    func audioPlayerDecodeErrorDidOccur(_ player: AVAudioPlayer, error: Error?) {
        if let e = error {
            print("\(e.localizedDescription)")
        }
    }
}
