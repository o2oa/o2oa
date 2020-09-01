//
//  OOAIViewModel.swift
//  O2Platform
//
//  Created by FancyLou on 2018/9/10.
//  Copyright © 2018 zoneland. All rights reserved.
//

import UIKit
import Promises


/// 活动状态
///
/// - listen: 正在听命令
/// - speak: 正在说话
enum AIActivityStatus {
    case listen
    case speak
}

/// 当前状态
///
/// - normal: 普通状态
/// - working: 正在处理工作
enum AIStatus {
    case normal
    case working
}

/// 当前路由 路由就是接下来怎么处理
///
/// - listen: 开始听命令
/// - finish: 退出AI语音助手
/// - findtask: 查找待办工作
/// - dealTaskWithCommand: 根据命令处理工作
/// - dealTaskWithNeural: 根据人工智能算法处理工作
enum AICommandRoute {
    case listen
    case finish
    case findtask
    case dealTaskWithCommand
    case dealTaskWithNeural
}

protocol OOAIVoiceControllerDelegate {
    func changeActivityStatus(status: AIActivityStatus?)
    func changeSpeakMessage(message: String?)
    func finishController()
}

class OOAIViewModel: NSObject {
    override init() {
        super.init()
    }
    private let tulingAPI = OOMoyaProvider<Tuling123API>()
    private let taskAPI = OOMoyaProvider<OOTaskAPI>()
    private var taskList:[O2TodoTask] = []
    private var currentDealTaskIndex: Int = -1
    
    /// 前台ui获取当前任务，展现路由命令给UI
    func getCurrentDealTask() -> O2TodoTask? {
        if self.currentDealTaskIndex >= 0 && self.taskList.count > 0 {
            return self.taskList[self.currentDealTaskIndex]
        }
        return nil
    }
    
    /// 当前路由
    private var commandRoute: AICommandRoute = .listen
    
    /// 当前状态
    private var aiStatus: AIStatus = .normal
    
    /// 前台ui获取当前状态
    func getAIStatus() -> AIStatus {
        return aiStatus
    }
    
    /// ViewController里面实现处理前台页面UI相关的
    var aiVoiceControllerDelegate: OOAIVoiceControllerDelegate?
    
    //MARK: - MVVM arguments
    
    /// 活动状态 当前是在listen 还是在speak
    var activityStatus:AIActivityStatus? {
        didSet {
            self.aiVoiceControllerDelegate?.changeActivityStatus(status: activityStatus)
        }
    }
    
    
    /// 命令 listen后得到的命令
    var command: String? {
        didSet {
            if command != nil {
                listenFinish()
            }else {
                listenError()
            }
        }
    }
    
    /// 语音合成的文字，需要显示到UI上的
    var speakMessage: String? {
        didSet {
            self.aiVoiceControllerDelegate?.changeSpeakMessage(message: speakMessage)
        }
    }
    
    
    
}


extension OOAIViewModel {
    
    /// 前台说话结束后执行
    func speakFinish() {
        switch commandRoute {
        case .listen:
            self.activityStatus = .listen
            break
        case .finish:
            self.aiVoiceControllerDelegate?.finishController()
            break
        case .findtask:
            self.findTask()
            break
        case .dealTaskWithCommand:
            self.askHowToDealWithNextTask()
            break
        case .dealTaskWithNeural:
            self.dealWorkWithNeural()
            break
        }
    }
    
    
    private func listenFinish() {
        
        func isCurrentTaskRouteCommand() -> Bool {
            let task = self.taskList[self.currentDealTaskIndex]
            return (task.routeNameList?.contains(self.command!) == true)
        }
        
        if self.aiStatus == .normal {
            if isInStopCommand() {
                self.speakMessage = "感谢使用，下次再见！"
                self.commandRoute = .finish
                self.activityStatus = .speak
            }else if isInTaskCommand() {
                self.speakMessage = "正在查询您的工作"
                self.commandRoute = .findtask
                self.activityStatus = .speak
            }else {
                self.searchFromTuling()
            }
        }else {
            if isInTaskNeuralCommand() {
                self.speakMessage = "正在生成人工神经网络.提取您的处理数据进行分析."
                self.commandRoute = .dealTaskWithNeural
                self.activityStatus = .speak
            }else if isCurrentTaskRouteCommand() {
                self.dealWork(routeName: self.command!)
            }else if isInIgnoreCommand() {
                self.currentDealTaskIndex = self.currentDealTaskIndex + 1
                self.speakMessage = "好的！"
                self.commandRoute = .dealTaskWithCommand
                self.activityStatus = .speak
            }else if isInStopCommand() {
                self.speakMessage = "好的!还有什么需要我为您做的吗？"
                self.aiStatus = .normal
                self.commandRoute = .listen
                self.activityStatus = .speak
            }else {
                self.speakMessage = "对不起，我无法处理这个命令。您可以选择 退出工作 或 继续下一个工作"
                self.commandRoute = .listen
                self.activityStatus = .speak
            }
        }
    }
    
    private func listenError() {
        self.speakMessage = "对不起，我没有听清！"
        self.activityStatus = .speak
    }
    
    private func askHowToDealWithNextTask() {
        if self.currentDealTaskIndex >= self.taskList.count {
            self.aiStatus = .normal
            self.commandRoute = .listen
            self.speakMessage = "所有工作已经处理完成!还有什么需要我为您做的吗？"
            self.activityStatus = .speak
        }else {
            if self.currentDealTaskIndex < 0 {
                self.currentDealTaskIndex = 0
            }
            let task = self.taskList[self.currentDealTaskIndex]
            let person = task.person?.split(separator: "@").first ?? ""
            let routeList = task.routeNameList?.joined(separator: "或") ?? ""
            let processName = task.processName ?? ""
            let title = task.title ?? ""
            self.commandRoute = .listen
            self.speakMessage = "来自\(person)的\(processName)，标题：\(title)"+" , 您可以选择：\(routeList)"
            self.activityStatus = .speak
        }
    }
    
    private func dealWorkWithNeural() {
        let param:[String:String] = ["type":""]
        let task = self.taskList[self.currentDealTaskIndex]
        self.taskAPI.request(.taskSubmitNeural(task.id!, param), completion: { result in
            let response = OOResult<BaseModelClass<[O2TodoTaskNeural]>>(result)
            if response.isResultSuccess() {
                self.currentDealTaskIndex = self.currentDealTaskIndex + 1
                self.speakMessage = "工作处理完成！"
                self.commandRoute = .dealTaskWithCommand
                self.activityStatus = .speak
            }else {
                let routeList = task.routeNameList?.joined(separator: "或") ?? ""
                self.commandRoute = .listen
                self.speakMessage = "任务处理失败，当前任务无法自动判断，您可以选择：\(routeList)"
                 self.activityStatus = .speak
            }
        })
    }
    private func dealWork(routeName: String) {
        let param:[String:String] = ["routeName":routeName, "opinion":""]
        let task = self.taskList[self.currentDealTaskIndex]
        self.taskAPI.request(.taskSaveAndSubmit(task.id!, (param as Dictionary<String, AnyObject>)), completion: { result in
            let response = OOResult<BaseModelClass<O2WorkPostResult>>(result)
            if response.isResultSuccess() {
                self.currentDealTaskIndex = self.currentDealTaskIndex + 1
                self.speakMessage = "工作处理完成！"
                self.commandRoute = .dealTaskWithCommand
                self.activityStatus = .speak
            }else {
                let routeList = task.routeNameList?.joined(separator: "或") ?? ""
                self.commandRoute = .listen
                self.speakMessage = "任务处理失败，您可以选择：\(routeList)"
                self.activityStatus = .speak
            }
        })
    }
    
    private func findTask() {
        self.taskAPI.request(.taskList(OOTaskPageParameter()), completion: { result in
            let response = OOResult<BaseModelClass<[O2TodoTask]>>(result)
            if response.isResultSuccess() {
                if let data = response.model?.data {
                    self.taskList = data
                    self.speakMessage = "您有\(data.count)项工作需要处理."
                    self.aiStatus = .working
                    self.commandRoute = .dealTaskWithCommand  //路由到处理工作
                }else {
                    self.taskList = []
                    self.speakMessage = "没有查询到需要处理的工作！还有什么需要我为您做的吗？"
                }
                self.activityStatus = .speak
            }else {
                self.taskList = []
                self.speakMessage = "没有查询到需要处理的工作！还有什么需要我为您做的吗？"
                self.activityStatus = .speak
            }
        })
    }
    
    private func searchFromTuling() {
        let post = TulingPostModel()
        post.key = "1bbde256119f4d6eaf3c25e67dedbd38"
        post.info = self.command
        self.tulingAPI.request(.openapi(post)) { (result) in
            switch result {
            case .failure(let error):
                print(error)
                self.speakMessage = "更多功能还在升级学习中，非常抱歉！"
                self.commandRoute = .listen
                self.activityStatus = .speak
                break
            case .success(let data):
                let res = data.mapObject(TulingResponseModel.self)
                if let code = res?.code {
                    if code == "100000" {
                        self.speakMessage = res?.text
                    }else if code == "40001" || code == "40002" || code == "40004" || code == "40007" {
                        print("图灵识别错误")
                        self.speakMessage =  "更多功能还在升级学习中，非常抱歉！"
                    }else {
                        print("图灵识别错误, 未知的code。。。。。。。")
                        self.speakMessage =  "更多功能还在升级学习中，非常抱歉！"
                    }
                   
                }else {
                    print("图灵识别错误, 没有返回code。。。。。。。")
                    self.speakMessage =  "更多功能还在升级学习中，非常抱歉！"
                }
                self.commandRoute = .listen
                self.activityStatus = .speak
                break
            }
            
            
        }
    }
    
    private func isInStopCommand() -> Bool {
        guard let c = self.command else {
            return false
        }
        return AI_COMMAND_STOP.contains(c)
    }
    private func isInTaskCommand() -> Bool {
        guard let c = self.command else {
            return false
        }
        if AI_COMMAND_TASK.contains(c) {
            return true
        }else if AI_COMMAND_TASK_TYPO.contains(c) {
            return true
        }
        return false
    }
    private func isInTaskNeuralCommand() -> Bool {
        guard let c = self.command else {
            return false
        }
        return AI_COMMAND_TASK_NEURAL.contains(c)
    }
    private func isInIgnoreCommand() -> Bool {
        guard let c = self.command else {
            return false
        }
        return AI_COMMAND_IGNORE.contains(c)
    }
    

    
}

