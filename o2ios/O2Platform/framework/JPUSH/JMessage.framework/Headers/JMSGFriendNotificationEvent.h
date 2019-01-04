//
//  JMSGFriendEventContent.h
//  JMessage
//
//  Created by xudong.rao on 16/7/25.
//  Copyright © 2016年 HXHG. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <JMessage/JMSGAbstractContent.h>
#import <JMessage/JMSGUser.h>
#import <JMessage/JMSGNotificationEvent.h>


@interface JMSGFriendNotificationEvent : JMSGNotificationEvent

/// 事件的 id
@property(nonatomic, strong, readonly) NSString *JMSG_NULLABLE eventID;

/*!
 * @abstract 获取事件发生的理由
 *
 * @discussion 该字段由对方发起请求时所填，对方如果未填则将返回默认字符串
 */
- (NSString *JMSG_NULLABLE)getReason;

/*!
 * @abstract 事件发送者的username
 *
 * @discussion 该字段由对方发起请求时所填，对方如果未填则将返回默认字符串
 * 如果设置了noteName、nickname，返回优先级为noteName、nickname；否则返回username
 */
- (NSString *JMSG_NULLABLE)getFromUsername;

/*!
 * @abstract 获取事件发送者user
 */
- (JMSGUser *JMSG_NULLABLE)getFromUser;
@end

