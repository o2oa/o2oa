//
//  NotificationService.swift
//  O2PlatformExtPushStatistics
//
//  Created by 刘振兴 on 2018/4/14.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UserNotifications


@available(iOSApplicationExtension 10.0, *)
class NotificationService: UNNotificationServiceExtension {

    var contentHandler: ((UNNotificationContent) -> Void)?
    var bestAttemptContent: UNMutableNotificationContent?

    override func didReceive(_ request: UNNotificationRequest, withContentHandler contentHandler: @escaping (UNNotificationContent) -> Void) {
        self.contentHandler = contentHandler
        bestAttemptContent = (request.content.mutableCopy() as? UNMutableNotificationContent)
        
        if let bestAttemptContent = bestAttemptContent {
            // Modify the notification content here...
            bestAttemptContent.title = "\(bestAttemptContent.title) [modified]"
            let session = URLSession.shared
            let attachmentPath = self.bestAttemptContent?.userInfo["my-attachment"]
            if let attachPath = attachmentPath,(attachPath as AnyObject).hasSuffix("png") {
                let task = session.dataTask(with: URL.init(string: attachPath as! String)!) { (data, response, error) in
                    if let _ = data {
                        let localPath = "\(NSTemporaryDirectory())/myAttachment.png"
                        if  NSData(data: data!).write(toFile: localPath, atomically: true) {
                            let attachment = try! UNNotificationAttachment(identifier: "myAttachment", url: URL(fileURLWithPath: localPath), options: nil)
                            self.bestAttemptContent?.attachments = [attachment]
                        }
                    }
                    self.apnsDeliver(request: request)
                }
                task.resume()
            }else{
                self.apnsDeliver(request: request)
            }
            
            //contentHandler(bestAttemptContent)
        }
    }
    
    func apnsDeliver(request:UNNotificationRequest){
        JPushNotificationExtensionService.jpushSetAppkey("")
        JPushNotificationExtensionService.jpushReceive(request) {
            print("apns upload success")
            if let contentHandler = self.contentHandler, let bestAttemptContent =  self.bestAttemptContent {
                contentHandler(bestAttemptContent)
            }
        }
    }
    
    override func serviceExtensionTimeWillExpire() {
        // Called just before the extension will be terminated by the system.
        // Use this as an opportunity to deliver your "best attempt" at modified content, otherwise the original push payload will be used.
        if let contentHandler = contentHandler, let bestAttemptContent =  bestAttemptContent {
            contentHandler(bestAttemptContent)
        }
    }

}
