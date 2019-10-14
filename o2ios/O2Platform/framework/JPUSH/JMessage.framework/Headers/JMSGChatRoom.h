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

@class JMSGUser,JMSGMemberSilenceInfo;

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
 * @abstract 聊天室的黑名单列表
 *
 * @param handler 结果回调. resultObject 是 NSArray 类型，元素是 JMSGUser
 *
 * @since 3.8.0
 */
- (void)chatRoomBlacklist:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 添加黑名单
 *
 * @param usernames 用户名列表
 * @param appKey   用户 appKey，usernames 中的所有用户必须在同一个 AppKey 下，不填则默认为本应用 appKey
 * @param handler 结果回调。error 为 nil 表示成功.
 *
 * @since 3.8.0
 */
- (void)addBlacklistWithUsernames:(NSArray <__kindof NSString *>*)usernames
                           appKey:(NSString *JMSG_NULLABLE)appKey
                          handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;
/*!
 * @abstract 删除黑名单
 *
 * @param usernames 用户名列表
 * @param appKey   用户 appKey，usernames 中的所有用户必须在同一个 AppKey 下，不填则默认为本应用 appKey
 * @param handler 结果回调。error 为 nil 表示成功.
 *
 * @since 3.8.0
 */
- (void)deleteBlacklistWithUsernames:(NSArray <__kindof NSString *>*)usernames
                              appKey:(NSString *JMSG_NULLABLE)appKey
                             handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 管理员列表
 *
 * @param handler 结果回调. resultObject 是 NSArray 类型，元素是 JMSGUser
 *
 * @discussion 注意：返回列表中不包含房主.
 *
 * @since 3.8.0
 */
- (void)chatRoomAdminList:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 添加管理员
 *
 * @param usernames 用户名列表
 * @param appkey    用户 AppKey，不填则默认为本应用 AppKey
 * @param handler   结果回调。error 为 nil 表示成功.
 *
 * @discussion 注意：非 VIP 应用最多设置 15 个管理员，不包括群主本身
 *
 * @since 3.8.0
 */
- (void)addAdminWithUsernames:(NSArray <__kindof NSString *>*)usernames
                       appKey:(NSString *JMSG_NULLABLE)appkey
                      handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 删除管理员
 *
 * @param usernames 用户名列表
 * @param appkey    用户 AppKey，不填则默认为本应用 AppKey
 * @param handler   结果回调。error 为 nil 表示成功.
 *
 * @since 3.8.0
 */
- (void)deleteAdminWithUsernames:(NSArray <__kindof NSString *>*)usernames
                          appKey:(NSString *JMSG_NULLABLE)appkey
                         handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;
/*!
 * @abstract 设置成员禁言（可设置禁言时间）
 *
 * @param silenceTime 禁言时间戳，单位：毫秒，必须不小于5分钟，不大于1年
 * @param usernames   用户的 username 数组，一次最多500人
 * @param appkey      用户的 appKey，若传入空则默认使用本应用appKey，同一次设置的 usernames 必须在同一个 AppKey 下
 * @param handler     结果回调，error = nil 时，表示成功
 *
 * @discussion 只有房主和管理员可设置；设置成功的话上层会收到相应下发事件。
 *
 * @since 3.8.1
 */
- (void)addChatRoomSilenceWithTime:(SInt64)silenceTime
                         usernames:(NSArray *JMSG_NONNULL)usernames
                            appKey:(NSString *JMSG_NULLABLE)appkey
                           handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 取消成员禁言
 *
 * @param usernames  用户的 username 数组，一次最多500人
 * @param appkey     用户的 appKey，若传入空则默认使用本应用appKey，同一次设置的 usernames 必须在同一个 AppKey 下
 * @param handler   结果回调，error = nil 时，表示成功
 *
 * @discussion 只有房主和管理员可设置；取消成功的话上层会收到相应下发事件。
 *
 * @since 3.8.1
 */
- (void)deleteChatRoomSilenceWithUsernames:(NSArray *JMSG_NONNULL)usernames
                                    appKey:(NSString *JMSG_NULLABLE)appkey
                                   handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 获取禁言状态
 *
 * @param username 用户名
 * @param appKey   用户所在应用 AppKey，不填这默认本应用
 * @param handler  结果回调，resultObject 是 JMSGMemberSilenceInfo 类型
 *                 若 error == nil && resultObject != nil,该成员已被禁言
 *                 若 error == nil && resultObject == nil,该成员未被禁言
 *                 若 error != nil ,请求失败，
 *
 * @discussion 详细信息可查看 JMSGMemberSilenceInfo 类
 *
 * @since 3.8.1
 */
- (void)getChatRoomMemberSilenceWithUsername:(NSString *JMSG_NONNULL)username
                                      appKey:(NSString *JMSG_NULLABLE)appKey
                                     handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 禁言列表
 *
 * @param start 开始位置
 * @param count 需要获取的个数，必须大于 0
 * @param handler 结果回调
 *
 * @since 3.8.1
 */
- (void)getChatRoomSilencesWithStart:(SInt64)start
                               count:(SInt64)count
                             handler:(void(^)(NSArray <__kindof JMSGMemberSilenceInfo *>*JMSG_NULLABLE list,SInt64 total,NSError *JMSG_NULLABLE error))handler;
/*!
 * @abstract 聊天室的展示名
 *
 * @discussion 如果  chatroom.name 为空, 则此接口会返回 chatroom.roomID.
 */
- (NSString *)displayName;

JMSG_ASSUME_NONNULL_END
@end
