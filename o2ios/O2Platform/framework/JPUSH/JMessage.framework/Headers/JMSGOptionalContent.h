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

@class JMSGCustomNotification;

/*!
 * @abstract 发送消息的可选功能
 *
 */
@interface JMSGOptionalContent : NSObject

/*!
 * @abstract 不保存离线消息，NO，默认值，保存离线消息；YES，不保存离线消息
 */
@property(nonatomic, assign) BOOL noSaveOffline;

/*!
 * @abstract 不在状态栏显示消息，NO，默认值，状态栏显示消息；YES，状态栏不显示消息
 */
@property(nonatomic, assign) BOOL noSaveNotification;

/*!
 * @abstract 设置这条消息的发送是否需要对方发送已读回执，NO，默认值
 */
@property(nonatomic, assign) BOOL needReadReceipt;


/*!
 * @abstract 自定义消息通知栏的内容
 *
 * @discussion 这个属性可以具体参考 JMSGCustomNotification 类
 */
@property(nonatomic, strong) JMSGCustomNotification *customNotification;

@end




/// 自定义通知栏消息的内容
@interface JMSGCustomNotification : NSObject

/*!
 * @abstract 是否启用自定义通知栏，默认:NO
 */
@property(nonatomic, assign) BOOL enabled;

/*!
 * @abstract 自定义消息通知栏的标题
 */
@property(nonatomic, strong) NSString *title;

/*!
 * @abstract 自定义消息通知栏的内容
 *
 */
@property(nonatomic, strong) NSString *alert;

/*!
 * @abstract 被@目标的通知内容前缀
 *
 * @discussion 此字段仅对@消息设置有效
 */
@property(nonatomic, strong) NSString *atPrefix;

@end
