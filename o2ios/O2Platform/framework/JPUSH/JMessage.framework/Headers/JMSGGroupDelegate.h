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

@class JMSGGroup;


/*!
 * Group 相关变更通知
 */
@protocol JMSGGroupDelegate <NSObject>

/*!
 * @abstract 群组信息 (GroupInfo) 变更通知
 *
 * @param group 变更后的群组对象
 *
 * @discussion 如果想要获取通知, 需要先注册回调. 具体请参考 JMessageDelegate 里的说明.
 */
@optional
- (void)onGroupInfoChanged:(JMSGGroup *)group;

/*!
 * @abstract 监听申请入群通知
 *
 * @param event 申请入群事件
 *
 * @discussion 只有群主和管理员能收到此事件；申请入群事件相关参数请查看 JMSGApplyJoinGroupEvent 类，在群主审批此事件时需要传递事件的相关参数
 *
 * @since 3.4.0
 */
@optional
- (void)onReceiveApplyJoinGroupApprovalEvent:(JMSGApplyJoinGroupEvent *)event;

/*!
 * @abstract 监听管理员拒绝入群申请通知
 *
 * @param event 拒绝入群申请事件
 *
 * @discussion 只有申请方和被申请方会收到此事件；拒绝的相关描述和原因请查看 JMSGGroupAdminRejectApplicationEvent 类
 *
 * @since 3.4.0
 */
@optional
- (void)onReceiveGroupAdminRejectApplicationEvent:(JMSGGroupAdminRejectApplicationEvent *)event;

/*!
 * @abstract 监听管理员审批通知
 *
 * @param event 管理员审批事件
 *
 * @discussion 只有管理员才会收到该事件；当管理员同意或拒绝了某个入群申请事件时，其他管理员就会收到该事件，相关属性请查看 JMSGGroupAdminApprovalEvent 类
 *
 * @since 3.5.0
 */
@optional
- (void)onReceiveGroupAdminApprovalEvent:(JMSGGroupAdminApprovalEvent *)event;

/*!
 * @abstract 群成员群昵称变更通知
 *
 * @param events 群成员昵称变更事件列表
 *
 * @discussion 如果是离线事件，SDK 会将所有的修改记录加入数组上抛。事件具体相关属性请查看 JMSGGroupNicknameChangeEvent 类
 *
 * @since 3.7.0
 */
@optional
- (void)onReceiveGroupNicknameChangeEvents:(NSArray<__kindof JMSGGroupNicknameChangeEvent*>*)events;

/*!
 * @abstract 群公告变更通知
 *
 * @param events 群公告事件列表
 *
 * @discussion 事件具体相关属性请查看 JMSGGroupAnnouncementEvent 类
 *
 * @since 3.8.0
 */
@optional
- (void)onReceiveGroupAnnouncementEvents:(NSArray<__kindof JMSGGroupAnnouncementEvent*>*)events;

/*!
 * @abstract 群组黑名单变更通知
 *
 * @param events 群组黑名单事件列表
 *
 * @discussion 事件具体相关属性请查看 JMSGGroupBlacklistChangeEvent 类
 *
 * @since 3.8.0
 */
- (void)onReceiveGroupBlacklistChangeEvents:(NSArray<__kindof JMSGGroupBlacklistChangeEvent*>*)events;
@end
