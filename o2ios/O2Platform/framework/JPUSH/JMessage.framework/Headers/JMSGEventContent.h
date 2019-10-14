/*
 *	| |    | |  \ \  / /  | |    | |   / _______|
 *	| |____| |   \ \/ /   | |____| |  / /
 *	| |____| |    \  /    | |____| |  | |   _____
 * 	| |    | |    /  \    | |    | |  | |  |____ |
 *  | |    | |   / /\ \   | |    | |  \ \______| |
 *  | |    | |  /_/  \_\  | |    | |   \_________|
 *
 * Copyright (c) 2011 ~ 2015 Shenzhen HXHG. All rights reserved.
 */

#import <Foundation/Foundation.h>
#import <JMessage/JMSGAbstractContent.h>

/*!
 * 事件类型的消息内容
 *
 * 服务器端下发的事件通知, 比如用户被踢下线,群组里加了人, SDK 作为一个特殊的消息类型处理.
 * SDK 以消息的形式通知到 App. 详情参见 JMessageDelegate.
 */
@interface JMSGEventContent : JMSGAbstractContent <NSCopying>

/*!
 * @abstract 事件类型
 * @discussion 参考事件类型的定义 JMSGEventNotificationType
 */
@property(nonatomic, assign, readonly) JMSGEventNotificationType eventType;

// 不支持使用的初始化方法
- (nullable instancetype)init NS_UNAVAILABLE;

/*!
 * @abstract 获取事件发起者的用户名
 * @return 正常返回事件发起者的用户名，如果是调用后台 API 产生的事件，则返回：管理员
 *
 * @discussion 如果设置了nickname，则返回nickname，否则返回username
 * 可以用于定制 event message，拼接成完整的事件描述信息。
 */
- (NSString *JMSG_NULLABLE)getEventFromUsername;

/*!
 * @abstract 获取事件发起者
 */
- (JMSGUser *JMSG_NULLABLE)getEventFromUser;

/*!
 * @abstract 获取事件作用对象用户名列表
 * @return 返回类型为 NSArray，数组成员为事件作用对象的用户名
 *
 * @discussion 如果设置了nickname，则返回nickname，否则返回username
 * 可以用于定制 event message，拼接成完整的事件描述信息。
 */
- (NSArray *JMSG_NULLABLE)getEventToUsernameList;

/*!
 * @abstract 获取事件作用对象列表。
 */
- (NSArray <__kindof JMSGUser *>*JMSG_NULLABLE)getEventToUserList;

/*!
 * @abstract 获取事件自定义字段
 */
- (NSString *JMSG_NULLABLE)getEventCustom;

/*!
 @abstract 展示此事件的文本描述

 @discussion SDK 根据事件类型，拼接成完整的事件描述信息。
 */
- (NSString * JMSG_NONNULL)showEventNotification;

@end
