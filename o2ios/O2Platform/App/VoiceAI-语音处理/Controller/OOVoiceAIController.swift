//
//  OOVoiceAIController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/6/12.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack
import AVFoundation
import Speech
import O2OA_Auth_SDK

class OOVoiceAIController: UIViewController {
    
    @IBOutlet weak var voiceView: OOVoiceView!
    
    @IBOutlet weak var showLabel: UILabel!
    
    
    var closeVC = false
    var animation = false
    var lastRecognizeTime = -1 // 没有识别出内容的时间  计算持续多久没识别内容出来了
    var beginRecognizeFirstTime = -1 //是否有开始识别出内容
    
    // 语音合成
    var synthersizer:AVSpeechSynthesizer!
    var voice: AVSpeechSynthesisVoice!
    
    // 语音识别
    let recognizeBus = 0
    var recognizer: SFSpeechRecognizer!
    var recAudioEngine: AVAudioEngine!
    var recAudioInputNode: AVAudioInputNode!
    //
    var recRequest: SFSpeechAudioBufferRecognitionRequest?
    var recTask: SFSpeechRecognitionTask?
    var timer: Timer?
    
    private lazy var viewModel: OOAIViewModel = {
        return OOAIViewModel()
    }()
    
    
    //MARK: - override
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "语音助手"
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(title: "关闭", style: .plain, target: self, action: #selector(closeWindow))
        SFSpeechRecognizer.requestAuthorization { (status) in
            if status != SFSpeechRecognizerAuthorizationStatus.authorized {
                DDLogError("错误，没有权限！！！！！！")
            }
        }
        viewModel.aiVoiceControllerDelegate = self
        initSpeak()
        // init recognizer
        recognizer = SFSpeechRecognizer(locale: Locale(identifier: "zh_CN"))
        recAudioEngine = AVAudioEngine()
        recAudioInputNode = recAudioEngine.inputNode
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        // 开始说话
        let people = O2AuthSDK.shared.myInfo()?.name ?? ""
        viewModel.speakMessage = "你好：\(people)，需要我为您做些什么?"
        viewModel.activityStatus = .speak
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        DDLogInfo("关闭语音助手.....viewWillDisappear.......")
        self.closeVC = true
        if animation {
            stopListen()
        }
        if synthersizer?.isSpeaking == true {
            synthersizer?.stopSpeaking(at: .immediate)
        }
    }
    
    @objc private func closeWindow() {
        self.navigationController?.popViewController(animated: true)
    }
    
    //MARK: - recoginzer
    private func startListen() {
        guard !animation else {
            return
        }
        
        //清除任务
        if recTask != nil {
            recTask?.cancel()
            recTask = nil
        }
        
        // 清除定时器
        if self.timer != nil {
            self.timer?.invalidate()
            self.timer = nil
        }
        
        // 录音session
        let audioSession = AVAudioSession.sharedInstance()
        do {
            try audioSession.setCategory(AVAudioSession.Category.playAndRecord, mode: .default, options: .defaultToSpeaker)
            try audioSession.setActive(true, options: .notifyOthersOnDeactivation) // 这句话很重要，一定要，不然识别不到你的声音
        } catch {
            DDLogError("audioSession set failed.")
        }
        
        // 开始设置任务
        recRequest = SFSpeechAudioBufferRecognitionRequest()
        recRequest!.shouldReportPartialResults = true // 每个片段都返回 还是识别完成后返回
        recTask = recognizer.recognitionTask(with: recRequest!, delegate: self)
        
        // 监听一个标识位并拼接流文件
        let format = self.recAudioInputNode.outputFormat(forBus: self.recognizeBus)
        self.recAudioInputNode.installTap(onBus: self.recognizeBus, bufferSize: 1024, format: format) { (pcmBuffer, time) in
            self.recRequest?.append(pcmBuffer)
        }
        
        // 准备并启动引擎
        self.recAudioEngine.prepare()
        do {
            try self.recAudioEngine.start()
        }catch  {
            DDLogError("录音 识别 异常")
        }
        self.showLabel.text = "等待命令中....."
        animation = true
        voiceView.startAnimation()
        // 开始计时
        self.beginRecognizeFirstTime = Int(Date().timeIntervalSince1970)
        // 开启定时器 判断语音识别
        self.timer = Timer.scheduledTimer(timeInterval: 0.1, target: self, selector: #selector(tickTimer), userInfo: nil, repeats: true)
        self.timer?.fire()
    }
    
    @objc private func tickTimer() {
         let cTime = Int(Date().timeIntervalSince1970)
        if self.lastRecognizeTime < 0 {
            if self.beginRecognizeFirstTime > 0 && (cTime - self.beginRecognizeFirstTime) > 60 {
                // 超过60秒结束
                self.stopListen()
            }
        }else {
            if (cTime - self.lastRecognizeTime) > 3 { //超过3秒没说话
                self.stopListen()
            }
        }
    }
    
    private func stopListen() {
        guard animation else {
            return
        }
        self.timer?.invalidate()
        self.timer = nil
        self.recAudioEngine.stop()
        self.recAudioInputNode.removeTap(onBus: self.recognizeBus)
        self.recRequest?.endAudio()
        self.recRequest = nil
        self.recTask?.finish()
        self.recTask = nil
        animation = false
        self.beginRecognizeFirstTime = -1
        voiceView.stopAnimation()
    }
    
    
    
    //MARK: - Speech Synthesizer
    
    //初始化语音合成
    private func initSpeak() {
        synthersizer = AVSpeechSynthesizer.init()
        synthersizer.delegate = self
        voice = AVSpeechSynthesisVoice(language: "zh_CN")
    }
    
    private func speak(txt: String) {
        DDLogInfo("speak:\(txt)")
        do {
            let audioSession = AVAudioSession.sharedInstance()
//            try audioSession.overrideOutputAudioPort(.speaker)
            try audioSession.setCategory(.ambient, mode: .default)
//            let route = audioSession.currentRoute
//            route.outputs.forEach { (port) in
//                DDLogInfo("name:\(port.portName)")
//                DDLogInfo("type:\(port.portType)")
//                DDLogInfo("channels:\(String(describing: port.channels?.count))")
//            }
        }catch {
            DDLogError("异常：\(error)")
        }
        let utterance = AVSpeechUtterance(string: txt)
        utterance.voice = voice
        utterance.volume = 1
        synthersizer.speak(utterance)
    }
    
    
    
    
}

// MARK: - extension

extension OOVoiceAIController: OOAIVoiceControllerDelegate {
    func changeSpeakMessage(message: String?) {
        self.showLabel.text = message
    }
    func changeActivityStatus(status: AIActivityStatus?) {
        switch status! {
        case .listen:
            DDLogInfo("开始听命令。。。。。。。。。。。。。。")
            DispatchQueue.main.async {
                self.startListen()
            }
            
            break
        case .speak:
            DDLogInfo("开始说话。。。。。。。。。。。。。。")
            if let message = viewModel.speakMessage {
                DispatchQueue.main.async {
                    self.speak(txt: message)
                }
            }else {
                DDLogError("没有文字。 无法说话")
            }
            break
        }
    }
    
    func finishController() {
        DDLogInfo("关闭语音助手............")
        self.closeVC = true
        self.closeWindow()
    }
}

// 语音合成delegate
extension OOVoiceAIController : AVSpeechSynthesizerDelegate {
    func speechSynthesizer(_ synthesizer: AVSpeechSynthesizer, didFinish utterance: AVSpeechUtterance) {
        DDLogInfo("speak finish ............")
        if !self.closeVC {
            viewModel.speakFinish()
        }
    }
    func speechSynthesizer(_ synthesizer: AVSpeechSynthesizer, didStart utterance: AVSpeechUtterance) {
        DDLogInfo("speak start .....")
    }
    func speechSynthesizer(_ synthesizer: AVSpeechSynthesizer, didCancel utterance: AVSpeechUtterance) {
        DDLogError("speak cancel.......")
    }
    func speechSynthesizer(_ synthesizer: AVSpeechSynthesizer, didPause utterance: AVSpeechUtterance) {
        DDLogError("speak pause.......")
    }
    func speechSynthesizer(_ synthesizer: AVSpeechSynthesizer, didContinue utterance: AVSpeechUtterance) {
        DDLogInfo("speak continue............")
    }
}

extension OOVoiceAIController: SFSpeechRecognitionTaskDelegate {
    func speechRecognitionDidDetectSpeech(_ task: SFSpeechRecognitionTask) {
        DDLogInfo("did detect speech。。。。。。。")
    }
    func speechRecognitionTaskWasCancelled(_ task: SFSpeechRecognitionTask) {
        DDLogInfo("cancel recognizer....")
    }
    func speechRecognitionTask(_ task: SFSpeechRecognitionTask, didHypothesizeTranscription transcription: SFTranscription) {
        DDLogInfo("hypothesize recognize .....")
        let c = transcription.formattedString
        DDLogInfo("选：\(c)")
        let time = Int(Date().timeIntervalSince1970)
        self.lastRecognizeTime = time
    }
    func speechRecognitionTaskFinishedReadingAudio(_ task: SFSpeechRecognitionTask) {
        DDLogInfo("finish recognize end reading audio   .....")
    }
    func speechRecognitionTask(_ task: SFSpeechRecognitionTask, didFinishRecognition recognitionResult: SFSpeechRecognitionResult) {
        DDLogInfo("finish recognize result...........")
        let best = recognitionResult.bestTranscription.formattedString
        DDLogInfo("最佳：\(best)")
        let removePunctuation = best.trimmingCharacters(in: CharacterSet.punctuationCharacters)
        DDLogInfo("最佳去掉标点：\(removePunctuation)")
        if !self.closeVC {
            self.viewModel.command = removePunctuation
            self.lastRecognizeTime = -1
        }
    }
    func speechRecognitionTask(_ task: SFSpeechRecognitionTask, didFinishSuccessfully successfully: Bool) {
        DDLogInfo("finish recognize task ....... \(successfully)")
        if !successfully && !self.closeVC {
             self.viewModel.command = nil
        }
    }
    
}
