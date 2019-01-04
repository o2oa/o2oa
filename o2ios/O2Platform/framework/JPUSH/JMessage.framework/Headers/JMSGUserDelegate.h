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

@class JMSGGroup,JMSGUserLoginStatusChangeEvent;


/*!
 * User 相关变更通知
 */
@protocol JMSGUserDelegate <NSObject>

/*!
 * @abstract 监听当前用户登录状态变更事件
 *
 * @discussion 可监听：当前登录用户被踢、非客户端修改密码强制登出、登录状态异常、被删除、被禁用、信息变更等事件
 *
 * @since 3.5.0
 */
@optional
- (void)onReceiveUserLoginStatusChangeEvent:(JMSGUserLoginStatusChangeEvent *)event;


///----------------------------------------------------
/// @name 以下是已经过期方法，请使用提示的新方法
///----------------------------------------------------


/*!
 * @abstract 当前登录用户被踢下线通知(方法已过期，建议使用新方法)
 *
 * @discussion 一般可能是, 该用户在其他设备上登录, 把当前设备的登录踢出登录.
 *
 * SDK 收到服务器端下发事件后, 会内部退出登录.
 * App 也应该退出登录. 否则所有的 SDK API 调用将失败, 因为 SDK 已经退出登录了.
 *
 * 注意: 这是旧版本的监听方法，建议不要使用,已经过期,请使用 [JMSGUserDelegate onReceiveUserLoginStatusChangeEvent:] 新的监听方法.
 */
@optional
- (void)onLoginUserKicked __attribute__((deprecated("first deprecated in JMessage 2.2.0 - Use -onReceiveUserLoginStatusChangeEvent:")));
@end
