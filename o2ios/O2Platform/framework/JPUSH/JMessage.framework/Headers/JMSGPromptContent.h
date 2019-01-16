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
 * @abstract 提示性消息内容
 *
 * @discussion 此 MessageContent 类型仅由 SDK 主动创建，上层做展示用，不能当做发送的消息体。
 */
@interface JMSGPromptContent : JMSGAbstractContent<NSCopying>
JMSG_ASSUME_NONNULL_BEGIN

/*!
 * @abstract 获取提示信息
 *
 * @discussion 消息提示文字
 */
@property(nonatomic,strong, readonly) NSString *promptText;

/*!
 * @abstract 提示性消息的类型
 *
 * @discussion 比如：消息撤回提示、后台自定义消息提示等
 */
@property(nonatomic,assign, readonly) JMSGPromptContentType promptType;

// 不支持使用的初始化方法
- (instancetype)init NS_UNAVAILABLE;


JMSG_ASSUME_NONNULL_END
@end
