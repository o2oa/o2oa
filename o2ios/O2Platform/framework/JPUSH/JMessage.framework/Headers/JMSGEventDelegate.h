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
#import <JMessage/JMSGNotificationEvent.h>
#import <JMessage/JMSGFriendNotificationEvent.h>

/*!
 * 监听通知事件
 */
@protocol JMSGEventDelegate <NSObject>


/*!
 * @abstract 监听好友相关事件
 *
 * @param event 好友事件
 *
 * @discussion 可监听：加好友、删除好友、好友更新等事件
 *
 * @since 3.5.0
 */
@optional
- (void)onReceiveFriendNotificationEvent:(JMSGFriendNotificationEvent *)event;

/*!
 * @abstract 监听消息撤回事件
 *
 * @param retractEvent 下发的通知事件，事件类型请查看 JMSGMessageRetractEvent 类
 *
 * @since 3.2.0
 */
@optional
- (void)onReceiveMessageRetractEvent:(JMSGMessageRetractEvent *)retractEvent;

/*!
 * @abstract 监听消息回执状态变更事件
 *
 * @param receiptEvent 下发的通知事件，事件类型请查看 JMSGMessageReceiptStatusChangeEvent 类
 *
 * @discussion 上层可以通过 receiptEvent 获取相应信息
 *
 * @since 3.3.0
 */
@optional
- (void)onReceiveMessageReceiptStatusChangeEvent:(JMSGMessageReceiptStatusChangeEvent *)receiptEvent;

/*!
 * @abstract 监听消息透传事件
 *
 * @param transparentEvent 下发的通知事件，事件类型请查看 JMSGMessageTransparentEvent 类
 *
 * @discussion 消息透传的类型：单聊、群聊、设备间透传消息
 *
 * @since 3.3.0
 */
@optional
- (void)onReceiveMessageTransparentEvent:(JMSGMessageTransparentEvent *)transparentEvent;

/*!
 * @abstract 聊天室管理员变更通知
 *
 * @param events 管理员事件列表
 *
 * @discussion 事件具体相关属性请查看 JMSGChatRoomAdminChangeEvent 类
 *
 * @since 3.8.0
 */
- (void)onReceiveChatRoomAdminChangeEvents:(NSArray<__kindof JMSGChatRoomAdminChangeEvent*>*)events;

/*!
 * @abstract 聊天室黑名单变更通知
 *
 * @param events 黑名单事件列表
 *
 * @discussion 事件具体相关属性请查看 JMSGChatRoomBlacklisChangetEvent 类
 *
 * @since 3.8.0
 */
- (void)onReceiveChatRoomBlacklistChangeEvents:(NSArray<__kindof JMSGChatRoomBlacklisChangetEvent*>*)events;

/*!
 * @abstract 聊天室禁言通知
 *
 * @param events 禁言事件列表
 *
 * @discussion 事件具体相关属性请查看 JMSGChatRoomSilenceEvent 类
 *
 * @since 3.8.1
 */
- (void)onReceiveChatRoomSilenceEvents:(NSArray<__kindof JMSGChatRoomSilenceEvent*>*)events;

///----------------------------------------------------
/// @name 以下是已经过期方法，请使用提示的新方法
///----------------------------------------------------

/*!
 * @abstract 监听通知事件
 *
 * @param event 下发的通知事件，上层通过 event.eventType 判断具体事件
 *
 * @discussion 此方法可监听如下事件:
 *
 * - 好友事件：加好友、删除好友、好友更新；
 * - 当前用户登录状态变更事件：当前登录用户被踢、非客户端修改密码强制登出、登录状态异常、被删除、信息变更通；
 *
 * #### 方法变更：
 *
 * 之前版本的好友事件、当前登录用户状态变更事件都是通过此方法监听，SDK 从 3.5.0 开始将此方法细分为两个方法.
 *
 *    ```
 *    // 当前用户登录状态变更事件，在 JMSGUserDelegate 类
 *    - (void)onReceiveUserLoginStatusChangeEvent:(JMSGUserLoginStatusChangeEvent *)event;
 *    // 好友相关事件
 *    - (void)onReceiveFriendNotificationEvent:(JMSGFriendNotificationEvent *)event;
 *    ```
 *
 * #### 注意：此方法已过期，请使用如上所述的两个方法
 */
@optional
- (void)onReceiveNotificationEvent:(JMSGNotificationEvent *)event __attribute__((deprecated("在 JMessage 3.5.0 过期了")));
@end

