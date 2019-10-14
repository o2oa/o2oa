//
//  O2Logger.swift
//  
//
//  Created by 刘振兴 on 2017/6/2.
//
//

import Foundation
import CocoaLumberjack
//设置日志级别
public let ddLogLevel:DDLogLevel = DDLogLevel.debug;

class O2Logger {
    
    private static var fileLogger:DDFileLogger {
        var logFilePath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first
        logFilePath?.append("/logs")
        let fileManager = DDLogFileManagerDefault(logsDirectory: logFilePath)
        let ddFileLogger = DDFileLogger(logFileManager: fileManager)
        ddFileLogger.logFormatter = O2LoggerFormatter()
        return ddFileLogger
    }
    
    public static func startLogManager() {
        DDTTYLogger.sharedInstance.colorsEnabled = true
        DDTTYLogger.sharedInstance.logFormatter = O2LoggerFormatter()
        //加入到console
        DDLog.add(DDTTYLogger.sharedInstance)
        //加入到Apple Log System
        //DDLog.add(DDASLLogger.sharedInstance)
        //加入到自定义文件
        //let fileLogger:DDFileLogger = DDFileLogger()
        fileLogger.rollingFrequency = TimeInterval(60*60*24)
        fileLogger.logFileManager.maximumNumberOfLogFiles = 7
        DDLog.add(fileLogger)
        
        DDLogInfo("DDLog is configuration")
        
    }
    
    public static func getLogFiles() -> [O2LogFileInfo] {
        //fileLogger.logFileManager.sortedLogFileInfos
        var o2LogFiles:[O2LogFileInfo] = []
        let fileInfos = fileLogger.logFileManager.sortedLogFileInfos
        for fileInfo in fileInfos {
            DDLogDebug(fileInfo.filePath)
            let logFile = O2LogFileInfo(filePath: fileInfo.filePath, fileName: fileInfo.fileName, creationDate: fileInfo.creationDate, modificationDate: fileInfo.modificationDate, fileSize: fileInfo.fileSize, age: fileInfo.age, isArchived: fileInfo.isArchived)
            o2LogFiles.append(logFile)
        }
        
        return o2LogFiles
    }
    
    
    public static func debug(_ message:String){
        DDLogDebug(message)
    }
    
    public static func info(_ message:String){
        DDLogInfo(message)
    }
    
    public static func warn(_ message:String){
        DDLogWarn(message)
    }
    
    public static func error(_ message:String){
        DDLogError(message)
    }
}

class O2LoggerFormatter: NSObject,DDLogFormatter {
    
    func format(message logMessage: DDLogMessage) -> String? {
        //Level
        var level:String = "U"
        switch logMessage.flag {
        case DDLogFlag.error:
            level = "E"
            break
        case DDLogFlag.warning:
            level = "W"
            break
        case DDLogFlag.info:
            level = "I"
            break
        case DDLogFlag.debug:
            level = "D"
            break
        case DDLogFlag.verbose:
            level = "V"
            break
        default:
            level = "U"
        }
        //fileName
        let fileName = URL(fileURLWithPath: logMessage.file).lastPathComponent
        // function
        let fc = logMessage.function ?? ""
        //line
        let lineNumber = String(logMessage.line)
        //time
        let dateAndTime = logMessage.timestamp.toString("yyyy-MM-dd HH:mm:ss")
        //msg
        let msg = logMessage.message
        return "\(dateAndTime) \(fileName) \(fc) \(lineNumber) \(level) \(msg)"
    }
}

struct O2LogFileInfo {
    
    var filePath: String!
    var fileName: String!
    var creationDate: Date!
    var modificationDate: Date!
    var fileSize: UInt64!
    var age: TimeInterval!
    var isArchived: Bool!
    var friendFileName:String {
        let comps = fileName.split(" ")
        if comps.count > 2 {
            return "\(comps[1])_\(comps[2])"
        }else{
            return fileName
        }
    }
    
//    init(_ filePath:String,_ fileName:String,creationDate:Date,_ modificationDate:Date,_ fileSize:UInt64,_ age:TimeInterval,_ isArchived:Bool) {
//        self.filePath = filePath
//        self.fileName = fileName
//        self.creationDate = creationDate
//        self.modificationDate = modificationDate
//        self.fileSize = fileSize
//        self.age = age
//        self.isArchived = isArchived
//    }
}
