/*
 *  | |    | |  \ \  / /  | |    | |   / _______|
 *  | |____| |   \ \/ /   | |____| |  / /
 *  | |____| |    \  /    | |____| |  | |   _____
 *   | |    | |    /  \    | |    | |  | |  |____ |
 *  | |    | |   / /\ \   | |    | |  \ \______| |
 *  | |    | |  /_/  \_\  | |    | |   \_________|
 *
 * Copyright (c) 2011 ~ 2015 Shenzhen HXHG. All rights reserved.
 */

#import <Foundation/Foundation.h>
#import <JMessage/JMSGConstants.h>

@class JMSGUser;

/*!
 * 聊天室
 *
 * #### 主要特点
 *
 * 聊天室的消息没有推送通知和离线保存，也没有常驻成员的概念，只要进入聊天室即可接收消息，开始聊天，
 一旦退出聊天室，不再会接收到任何消息、通知和提醒。
 *
 * #### 发送消息
 *
 * 聊天室消息的发送与单聊、群聊是一样的，通用的发送接口
 *
 * #### 接收消息
 *
 * 聊天室消息的接收与单聊、群聊做了区分，聊天室消息的接收将通过 JMSGConversationDelegate 类里的 onReceiveChatRoomConversation:messages: 方法通知到上层
 *
 */
@interface JMSGChatRoom : NSObject
JMSG_ASSUME_NONNULL_BEGIN

/*!
 * @abstract 分页获取聊天室详情
 *
 * @param appKey  选填，为 nil 则获取当前应用下的聊天室
 * @param start   分页获取的下标，第一页从  index = 0 开始
 * @param count   一页的数量，每页最大值是 50
 * @param handler 结果回调. 正常返回时 resultObject 类型是 NSArray<JMSGChatRoom>
 *
 * @discussion 该接口总是向服务器端发起请求.
 */
+ (void)getChatRoomListWithAppKey:(NSString *JMSG_NULLABLE)appKey
                            start:(NSInteger)start
                            count:(NSInteger)count
                completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 获取当前用户已加入的聊天室列表
 *
 * @param handler 结果回调. 正常返回时 resultObject 类型是 NSArray<JMSGChatRoom>
 *
 * @discussion 该接口总是向服务器端发起请求.
 */
+ (void)getMyChatRoomListCompletionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 获取聊天室详情
 *
 * @param roomIds   待获取详情的聊天室 ID 数组
 * @param handler   结果回调. 正常返回时 resultObject 类型是 NSArray<JMSGChatRoom>
 *
 * @discussion 该接口总是向服务器端发起请求.
 */
+ (void)getChatRoomInfosWithRoomIds:(NSArray *JMSG_NONNULL)roomIds
                  completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 加入聊天室
 *
 * @param roomId    聊天室 id
 * @param handler   结果回调. error = nil 表示加入成功，resultObject 为 JMSGConversation 类型
 *
 * @discussion 成功进入聊天室之后，会将聊天室中最近若干条聊天记录同步下来并以 onReceiveChatRoomConversation: 事件的形式通知到上层，进入聊天室会自动获取最近50条消息。
 */
+ (void)enterChatRoomWithRoomId:(NSString *JMSG_NONNULL)roomId
              completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 退出聊天室
 *
 * @param roomId    聊天室 id
 * @param handler   结果回调. error = nil 表示加入成功.
 *
 * @discussion 退出聊天室后获取不到任何消息和通知.
 */
+ (void)leaveChatRoomWithRoomId:(NSString *JMSG_NONNULL)roomId
              completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

///----------------------------------------------------
/// @name Group basic fields 群组基本属性
///----------------------------------------------------


/*!
 * @abstract 聊天室 id
 */
@property(nonatomic, strong, readonly) NSString *JMSG_NONNULL roomID;
/*!
 * @abstract 名称
 */
@property(nonatomic, strong, readonly) NSString *JMSG_NONNULL name;
/*!
 * @abstract 聊天室所属应用 AppKey
 */
@property(nonatomic, strong, readonly) NSString *JMSG_NONNULL appkey;
/*!
 * @abstract 描述信息
 */
@property(nonatomic, strong, readonly) NSString *JMSG_NULLABLE desc;

/*!
 * @abstract 聊天室人数
 */
@property(nonatomic, assign, readonly) NSInteger totalMemberCount;

/*!
 * @abstract 聊天室最大人数限制
 */
@property(nonatomic, strong, readonly) NSString *JMSG_NULLABLE maxMemberCount;

/*!
 * @abstract 聊天室的创建时间
 */
@property(nonatomic, strong, readonly) NSNumber *ctime;

/*!
 * @abstract 聊天室创建者
 *
 * @param handler   结果回调. error = nil 表示获取成功, resultObject 为 JMSGUser 类型.
 */
- (void)getChatRoomOwnerInfo:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 聊天室的展示名
 *
 * @discussion 如果  chatroom.name 为空, 则此接口会返回 chatroom.roomID.
 */
- (NSString *)displayName;

JMSG_ASSUME_NONNULL_END
@end
