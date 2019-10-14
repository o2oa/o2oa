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

/*!
 * 会话相关变更通知
 */
@protocol JMSGConversationDelegate <NSObject>

/*!
 * @abstract 会话信息变更通知
 *
 * @param conversation 变更后的会话对象
 *
 * @discussion 当前有二个属性: 会话标题(title), 会话图标
 *
 * 收到此通知后, 建议处理: 如果 App 当前在会话列表页，刷新整个列表；如果在聊天界面，刷新聊天标题。
 */
@optional
- (void)onConversationChanged:(JMSGConversation *)conversation;

/*!
 * @abstract 同步离线消息、离线事件通知
 *
 * @param conversation    同步离线消息的会话
 * @param offlineMessages 离线消息、离线事件数组
 *
 * @discussion 注意：
 *
 * SDK 会将消息下发分为在线下发和离线下发两种情况,
 * 其中用户在离线状态(包括用户登出或者网络断开)期间所收到的消息我们称之为离线消息.
 *
 * 当用户上线收到这部分离线消息后,这里的处理与之前版本不同的是:
 *
 * 3.1.0 版本之前: SDK 会和在线时收到的消息一样,每收到一条消息都会上抛一个在线消息 JMSGMessage 来通知上层.
 *
 * 3.1.0 版本之后: SDK 会以会话为单位，不管该会话有多少离线消息，SDK同步完成后每个会话只上抛一次.
 *
 * 3.2.1 版本之后: SDK 会以会话为单位，不管该会话有多少离线事件，SDK同步完成后每个会话只上抛一次
 *
 * 注意：一个会话最多触发两次这个代理，即：离线消息和离线事件各一次,这样会大大减轻上层在收到消息刷新 UI 的压力.
 *
 * 上层通过此代理方法监听离线消息同步的会话,详见官方文档.
 *
 */
@optional
- (void)onSyncOfflineMessageConversation:(JMSGConversation *)conversation
                         offlineMessages:(NSArray JMSG_GENERIC(__kindof JMSGMessage *)*)offlineMessages;
/*!
 * @abstract 同步漫游消息通知
 *
 * @param conversation 同步漫游消息的会话
 *
 * @discussion 注意：
 *
 * 当 SDK 触发此函数时，说明该会话有同步下漫游消息，并且已经存储到本地数据库中，
 * 上层可通过 JMSGConversation 类中的获取message的方法刷新UI.
 *
 * @since 3.1.0
 */
@optional
- (void)onSyncRoamingMessageConversation:(JMSGConversation *)conversation;

/*!
 * @abstract 接收聊天室消息
 *
 * @param conversation 聊天室会话
 * @param messages      接收到的消息数组，元素是 JMSGMessage
 *
 * @discussion 注意：
 *
 * 接收聊天室的消息与单聊、群聊消息不同，聊天室消息都是通过这个代理方法来接收的。
 *
 * @since 3.4.0
 */
- (void)onReceiveChatRoomConversation:(JMSGConversation *)conversation
                             messages:(NSArray JMSG_GENERIC(__kindof JMSGMessage *)*)messages;


/*!
 * @abstract 当前剩余的全局未读数
 *
 * @param newCount 变更后的数量
 */
@optional
- (void)onUnreadChanged:(NSUInteger)newCount __attribute__((deprecated("deprecated")));
@end

