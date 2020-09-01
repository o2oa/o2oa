//
//  OOVoiceAIController.swift
//  O2Platform
//
//  Created by FancyLou on 2020/8/27.
//  Copyright © 2020年 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack
import AVFoundation
import Speech
import O2OA_Auth_SDK

class OOVoiceAIController: UIViewController {
    
    @IBOutlet weak var voiceView: OOVoiceView!
    @IBOutlet weak var tipsLabel: UILabel!
    @IBOutlet weak var showLabel: UILabel!
    
    
    var closeVC = false
    var animation = false
 
    
    // 语音合成
    var synthersizer:AVSpeechSynthesizer!
    var voice: AVSpeechSynthesisVoice!
    
    //百度语音识别
    var bdmanager: BDSEventManager? = nil
    
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
        configASR()
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
    
    // MARK: - 语音识别
    ///初始化语音识别
   private func configASR() {
       self.bdmanager = BDSEventManager.createEventManager(withName: BDS_ASR_NAME)
       self.bdmanager?.setDelegate(self)
       //百度语音识别 appkey 和 secret
       self.bdmanager?.setParameter([BAIDU_ASR_APP_KEY, BAIDU_ASR_APP_SECRET], forKey: BDS_ASR_API_SECRET_KEYS)
       self.bdmanager?.setParameter(BAIDU_ASR_APP_ID, forKey: BDS_ASR_OFFLINE_APP_CODE)
       // 获取VAD模型的路径
       let path = Bundle.main.path(forResource: "bds_easr_basic_model", ofType: "dat")
       self.bdmanager?.setParameter(path, forKey: BDS_ASR_MODEL_VAD_DAT_FILE)
       self.bdmanager?.setParameter(true, forKey: BDS_ASR_ENABLE_MODEL_VAD)
       //关闭标点
       self.bdmanager?.setParameter(true, forKey: BDS_ASR_DISABLE_PUNCTUATION)
   }
    
    private func startListen() {
        guard !animation else {
            return
        }
        self.bdmanager?.sendCommand(BDS_ASR_CMD_START)
        self.showLabel.text = "等待命令中....."
        //根据当前状态 展现提示命令
        if self.viewModel.getAIStatus() == .normal {
            let taskCommand = AI_COMMAND_TASK.joined(separator: ",")
            let outCommand = AI_COMMAND_STOP.joined(separator: ",")
            self.tipsLabel.text = "可以使用如下命令：\(taskCommand) , \(outCommand)"
        }else if self.viewModel.getAIStatus() == .working {
            let ignoreCommand = AI_COMMAND_IGNORE.joined(separator: ",")
            let aiCommand = AI_COMMAND_TASK_NEURAL.joined(separator: ",")
            let outCommand = AI_COMMAND_STOP.joined(separator: ",")
            if let task = self.viewModel.getCurrentDealTask() {
                let routeList = task.routeNameList?.joined(separator: "或") ?? ""
                self.tipsLabel.text = "可以使用如下命令：\(routeList) , \(ignoreCommand), \(aiCommand)"
            }else {
                self.tipsLabel.text = "可以使用如下命令：\(outCommand)"
            }
        }else {
            self.tipsLabel.text = ""
        }
        animation = true
        voiceView.startAnimation()
    }
    
     
    private func stopListen() {
        guard animation else {
            return
        }
        self.tipsLabel.text = ""
        self.bdmanager?.sendCommand(BDS_ASR_CMD_STOP)
        animation = false
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
            try audioSession.setCategory(.ambient, mode: .default)
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

extension OOVoiceAIController: BDSClientASRDelegate {
       //EVoiceRecognitionClientWorkStatusStartWorkIng 识别工作开始，开始采集及处理数据 0
        //EVoiceRecognitionClientWorkStatusStart,                  // 检测到用户开始说话1
    //    EVoiceRecognitionClientWorkStatusEnd,                    // 本地声音采集结束，等待识别结果返回并结束录音2
    //    EVoiceRecognitionClientWorkStatusNewRecordData,          // 录音数据回调3
    //    EVoiceRecognitionClientWorkStatusFlushData,              // 连续上屏4
    //    EVoiceRecognitionClientWorkStatusFinish,                 // 语音识别功能完成，服务器返回正确结果5
    //    EVoiceRecognitionClientWorkStatusMeterLevel,             // 当前音量回调6
    //    EVoiceRecognitionClientWorkStatusCancel,                 // 用户取消7
    //    EVoiceRecognitionClientWorkStatusError,                  // 发生错误8
    //    /* 离线引擎状态 */
    //    EVoiceRecognitionClientWorkStatusLoaded,                 // 离线引擎加载完成9
    //    EVoiceRecognitionClientWorkStatusUnLoaded,               // 离线引擎卸载完成10
    //    /* CHUNK状态 */
    //    EVoiceRecognitionClientWorkStatusChunkThirdData,         // CHUNK: 识别结果中的第三方数据11
    //    EVoiceRecognitionClientWorkStatusChunkNlu,               // CHUNK: 识别结果中的语义结果12
    //    EVoiceRecognitionClientWorkStatusChunkEnd,               // CHUNK: 识别过程结束13
    //    /* LOG */
    //    EVoiceRecognitionClientWorkStatusFeedback,               // Feedback: 识别过程反馈的打点数据14
    //    /* Only for iOS */
    //    EVoiceRecognitionClientWorkStatusRecorderEnd,            // 录音机关闭，页面跳转需检测此时间，规避状态条 (iOS) 15
    //    /* LONG SPEECH END */
    //    EVoiceRecognitionClientWorkStatusLongSpeechEnd           // 长语音结束状态16
    func voiceRecognitionClientWorkStatus(_ workStatus: Int32, obj aObj: Any!) {
        switch workStatus {
        case 0: //EVoiceRecognitionClientWorkStatusStartWorkIng 识别工作开始，开始采集及处理数据
            DDLogInfo("开始识别。。。。。")
            break
        case 5: //EVoiceRecognitionClientWorkStatusFinish 语音识别功能完成，服务器返回正确结果
            if let resDic = aObj as? NSDictionary, let arr = resDic["results_recognition"] as? Array<String> {
                let first = arr[0]
                DDLogInfo("识别成功 返回结果 \(first)")
                DispatchQueue.main.async {
                    if !self.closeVC {
                        self.stopListen()
                        self.viewModel.command = first
                    }
                }
            }else {
                if !self.closeVC {
                    self.stopListen()
                     self.viewModel.command = nil
                }
            }
            break
        case 8://EVoiceRecognitionClientWorkStatusError,                  // 发生错误8
            let err = aObj as? Error
            DDLogError("err : \(String(describing: err?.localizedDescription))")
            DispatchQueue.main.async {
                if !self.closeVC {
                    self.stopListen()
                     self.viewModel.command = nil
                }
            }
        default:
            break
        }
    }
}
//
//extension OOVoiceAIController: SFSpeechRecognitionTaskDelegate {
//    func speechRecognitionDidDetectSpeech(_ task: SFSpeechRecognitionTask) {
//        DDLogInfo("did detect speech。。。。。。。")
//    }
//    func speechRecognitionTaskWasCancelled(_ task: SFSpeechRecognitionTask) {
//        DDLogInfo("cancel recognizer....")
//    }
//    func speechRecognitionTask(_ task: SFSpeechRecognitionTask, didHypothesizeTranscription transcription: SFTranscription) {
//        DDLogInfo("hypothesize recognize .....")
//        let c = transcription.formattedString
//        DDLogInfo("选：\(c)")
//        let time = Int(Date().timeIntervalSince1970)
//        self.lastRecognizeTime = time
//    }
//    func speechRecognitionTaskFinishedReadingAudio(_ task: SFSpeechRecognitionTask) {
//        DDLogInfo("finish recognize end reading audio   .....")
//    }
//    func speechRecognitionTask(_ task: SFSpeechRecognitionTask, didFinishRecognition recognitionResult: SFSpeechRecognitionResult) {
//        DDLogInfo("finish recognize result...........")
//        let best = recognitionResult.bestTranscription.formattedString
//        DDLogInfo("最佳：\(best)")
//        let removePunctuation = best.trimmingCharacters(in: CharacterSet.punctuationCharacters)
//        DDLogInfo("最佳去掉标点：\(removePunctuation)")
//        if !self.closeVC {
//            self.viewModel.command = removePunctuation
//            self.lastRecognizeTime = -1
//        }
//    }
//    func speechRecognitionTask(_ task: SFSpeechRecognitionTask, didFinishSuccessfully successfully: Bool) {
//        DDLogInfo("finish recognize task ....... \(successfully)")
//        if !successfully && !self.closeVC {
//             self.viewModel.command = nil
//        }
//    }
//
//}
