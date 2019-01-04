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
#import <JMessage/JMSGConversation.h>

/*!
 * 事件
 *
 * #### 事件分类
 *
 * 事件主要区分两类，通知事件、消息事件，两种不同类型的事件是通过不同的监听方法来监听的。
 *
 * #### 通知事件
 *
 * 通知事件就是除群事件之外的，如：当前登录登录状态变更、好友相关、消息撤回、消息透传、入群申请、管理员审批等事件.
 * 上层通过 JMSGEventDelegate 类里的相对应的代理方法接收事件，具体细分请查看 JMSGEventDelegate 类.
 *
 * #### 消息事件
 *
 * 消息事件就是群事件，如：群加人、踢人、修改群信息、群成员禁言、管理员变更等（即：会展示在消息列表的事件），SDK 依旧作为一个特殊的消息类型下发，上层通过 [JMSGMessageDelegate onReceiveMessage:] 接收消息事件.
 */
@interface JMSGNotificationEvent : NSObject

/*!
 * @abstract 事件类型
 * @discussion 参考事件类型的定义 JMSGEventNotificationType
 */
@property(nonatomic, assign, readonly) JMSGEventNotificationType eventType;

/*!
 * @abstract 事件的描述信息
 * @discussion 下发事件的文字描述，可能为空
 */
@property(nonatomic, strong, readonly) NSString *eventDescription;

@end

/*!
 * @abstract 当前登录用户状态改变事件
 *
 * @discussion 当前登录用户被踢、非客户端修改密码强制登出、登录状态异常、被删除、被禁用、信息变更等通知
 *
 * @since 3.5.0
 */
@interface JMSGUserLoginStatusChangeEvent: JMSGNotificationEvent
@end


/*!
 * @abstract 消息撤回事件
 *
 * @discussion 上层通过 JMSGEventDelegate 类中的 [JMSGEventDelegate onReceiveMessageRetractEvent:] 代理方法监听此事件,详见官方文档.
 */
@interface JMSGMessageRetractEvent : JMSGNotificationEvent

/// 消息撤回所属会话
@property(nonatomic, strong, readonly) JMSGConversation *conversation;

/// 撤回之后的消息
@property(nonatomic, strong, readonly) JMSGMessage *retractMessage;

@end


/*!
 * @abstract 消息已读回执状态变更事件
 *
 * @discussion 上层通过 JMSGEventDelegate 类中的 [JMSGEventDelegate onReceiveMessageReceiptStatusChangeEvent:] 代理方法监听该事件
 */
@interface JMSGMessageReceiptStatusChangeEvent : JMSGNotificationEvent

/// 消息所属会话
@property(nonatomic, strong, readonly) JMSGConversation *conversation;
/// 已读回执变更的消息列表
@property(nonatomic, strong, readonly) NSArray <__kindof JMSGMessage *>*messages;

@end


/*!
 * @abstract 消息透传事件
 *
 * @discussion 上层通过 JMSGEventDelegate 类中的 [JMSGEventDelegate onReceiveMessageTransparentEvent:] 代理方法监听该事件
 */
@interface JMSGMessageTransparentEvent : JMSGNotificationEvent

/// 消息透传的类型,单聊、群聊、设备间透传消息
@property(nonatomic, assign, readonly) JMSGTransMessageType transMessageType;
/// 透传消息的发送者
@property(nonatomic, strong, readonly) JMSGUser *sendUser;
/// 透传消息的目标对象，JMSGUser、JMSGGroup
@property(nonatomic, strong, readonly) id target;
/// 透传消息内容
@property(nonatomic, strong, readonly) NSString *transparentText;

/*!
 * @abstract 透传消息所属会话
 *
 * @discussion 注意：如果接收到设备间的透传事件，此属性的值为 nil；如果本地并没有创建会话，此属性也为 nil
 */
@property(nonatomic, strong, readonly) JMSGConversation *conversation;

@end


/*!
 * @abstract 申请入群事件
 *
 * @discussion 上层通过 JMSGGroupDelegate 类中的 [JMSGGroupDelegate onReceiveApplyJoinGroupApprovalEvent:] 代理方法监听该事件
 */
@interface JMSGApplyJoinGroupEvent : JMSGNotificationEvent

/// 事件的 id
@property(nonatomic, strong, readonly) NSString *eventID;
/// 群 gid
@property(nonatomic, strong, readonly) NSString *groupID;
/// 是否是用户主动申请入群，YES：主动申请加入，NO：被邀请加入
@property(nonatomic, assign, readonly) BOOL isInitiativeApply;
/// 发起申请的 user
@property(nonatomic, strong, readonly) JMSGUser *sendApplyUser;
/// 被邀请入群的 user 数组
@property(nonatomic, strong, readonly) NSArray JMSG_GENERIC(__kindof JMSGUser *)*joinGroupUsers;
/// 原因
@property(nonatomic, strong, readonly) NSString *reason;

@end

/*!
 * @abstract 管理员拒绝入群申请事件
 *
 * @discussion 上层通过 JMSGGroupDelegate 类中的 [JMSGGroupDelegate onReceiveGroupAdminRejectApplicationEvent:] 代理方法监听该事件
 */
@interface JMSGGroupAdminRejectApplicationEvent : JMSGNotificationEvent

/// 群 gid
@property(nonatomic, strong, readonly) NSString *groupID;
/// 拒绝原因
@property(nonatomic, strong, readonly) NSString *rejectReason;
/// 操作的管理员
@property(nonatomic, strong, readonly) JMSGUser *groupManager;

@end

/*!
 * @abstract 管理员审批事件
 *
 * @discussion 管理员同意或者拒绝了某个入群申请，其他管理员会收到该通知，上层通过 JMSGGroupDelegate 类中的 [JMSGGroupDelegate onReceiveGroupAdminApprovalEvent:] 代理方法监听该事件
 */
@interface JMSGGroupAdminApprovalEvent : JMSGNotificationEvent

/// 管理员是否同意申请，YES：同意，NO：拒绝
@property(nonatomic, assign, readonly) BOOL isAgreeApply;
/// 申请入群事件的事件 id
@property(nonatomic, strong, readonly) NSString *applyEventID;
/// 群 gid
@property(nonatomic, strong, readonly) NSString *groupID;
/// 操作的管理员
@property(nonatomic, strong, readonly) JMSGUser *groupAdmin;
/// 申请或被邀请加入群的用户，即：实际入群的用户
@property(nonatomic, strong, readonly) NSArray JMSG_GENERIC(__kindof JMSGUser *)*users;

@end

