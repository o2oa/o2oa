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
#import <UIKit/UIKit.h>
#import <JMessage/JMSGConstants.h>
#import <JMessage/JMSGConversation.h>

@class JMSGAbstractContent;
@class JMSGUser;
@protocol JMSGTargetProtocol;
@class JMSGOptionalContent;

/*!
 * 消息
 *
 * 本类 JMSGMessage 是 JMessage SDK 里的消息实体。
 * 收到的消息、发送的消息、获取历史消息，其中的消息类，都是这个 JMSGMessage。
 *
 * 以下分别描述消息相关主要使用场景。
 *
 * #### 获取历史消息
 *
 * 先基于聊天对象ID与会话类型，拿到会话对象，然后调用会话对象里的 [JMSGConversation allMessages:] 获取到某会话的全部历史消息列表。
 *
 * #### 展示一条消息
 *
 * 发送者、接收者等基本属性都有相应的属性。消息内容则在一个 content 对象里，访问时先通过 contentType 拿到内容类型，
 * 然后把 content 转型为相应的具体内容类型，再进一步可拿到具体的信息。
 *
 *    ```
 *    JMSGTextContent *textContent = (JMSGTextContent *)message.content;
 *    NSString *msgText = textContent.text;
 *    ```
 *
 * #### 接收消息
 *
 * 参考 JMessageDelegate 里的说明.
 *
 * #### 发送消息
 *
 * 参考 JMessageDelegate 里的说明.
 *
 */
@interface JMSGMessage : NSObject <NSCopying>

JMSG_ASSUME_NONNULL_BEGIN

///----------------------------------------------------
/// @name Class APIs 类方法 - 创建与发送消息
///----------------------------------------------------

/*!
 * @abstract 创建单聊消息（快捷接口）
 *
 * @param content 消息内容对象
 * @param username 单聊用户 username
 *
 * #### 注意：
 *
 * 1、单独调用此接口创建消息，SDK 不会本地保存消息，再调用发送接口时才会保存；
 *
 * 2、如果上层希望创建消息时就本地化保存，请使用 [JMSGConversation createMessageWithContent:]
 */
+ (JMSGMessage *)createSingleMessageWithContent:(JMSGAbstractContent *)content
                                       username:(NSString *)username;

/*!
 * @abstract 创建群聊消息
 *
 * @param content 消息内容对象
 * @param groupId 群聊ID
 *
 * #### 注意：
 *
 * 1、单独调用此接口创建消息，SDK 不会本地保存消息，再调用发送接口时才会保存；
 *
 * 2、如果上层希望创建消息时就本地化保存，请使用 [JMSGConversation createMessageWithContent:]
 */
+ (JMSGMessage *)createGroupMessageWithContent:(JMSGAbstractContent *)content
                                       groupId:(NSString *)groupId;

/*!
 * @abstract 创建聊天室消息
 *
 * @param content 消息内容对象
 * @param roomId  聊天室ID
 *
 * @discussion 不关心会话时的直接创建聊天消息的接口。一般建议使用 JMSGConversation -> createMessageWithContent:
 */
+ (JMSGMessage *)createChatRoomMessageWithContent:(JMSGAbstractContent *)content
                                       chatRoomId:(NSString *)roomId;

/*!
 * @abstract 创建@人的群聊消息
 *
 * @param content 消息内容对象
 * @param groupId 群聊ID
 * @param at_list @对象的数组
 *
 * #### 注意：
 *
 * 1、单独调用此接口创建消息，SDK 不会本地保存消息，再调用发送接口时才会保存；
 *
 * 2、如果上层希望创建消息时就本地化保存，请使用 [JMSGConversation createMessageWithContent:]
 */
+ (JMSGMessage *)createGroupMessageWithContent:(JMSGAbstractContent *)content
                                       groupId:(NSString *)groupId
                                       at_list:(NSArray<__kindof JMSGUser *> *)at_list;

/*!
 * @abstract 创建@所有人的群聊消息
 *
 * @param content 消息内容对象
 * @param groupId 群聊ID
 *
 * #### 注意：
 *
 * 1、单独调用此接口创建消息，SDK 不会本地保存消息，再调用发送接口时才会保存；
 *
 * 2、如果上层希望创建消息时就本地化保存，请使用 [JMSGConversation createMessageWithContent:]
 */
+ (JMSGMessage *)createGroupAtAllMessageWithContent:(JMSGAbstractContent *)content
                                            groupId:(NSString *)groupId;

/*!
 * @abstract 发送消息（已经创建好的）
 *
 * @param message 消息对象。
 *
 * @discussion 此接口与 createMessage:: 相关接口配合使用，创建好后使用此接口发送。
 */
+ (void)sendMessage:(JMSGMessage *)message;

/*!
 * @abstract 发送消息（附带可选功能，如：控制离线消息存储、自定义通知栏内容、消息已读回执等）
 *
 * @param message           通过消息创建类接口，创建好的消息对象
 * @param optionalContent   可选功能，具体请查看 JMSGOptionalContent 类
 *
 * @discussion 可选功能里可以设置离线消息存储、自定义通知栏内容、消息已读回执等，具体请查看 JMSGOptionalContent 类。
 *
 */
+ (void)sendMessage:(JMSGMessage *)message optionalContent:(JMSGOptionalContent *)optionalContent;

/*!
 * @abstract 发送单聊文本消息
 *
 * @param text 文本内容
 * @param username 单聊对象 username
 *
 * @discussion 快捷方法，不需要先创建消息而直接发送。
 */
+ (void)sendSingleTextMessage:(NSString *)text
                       toUser:(NSString *)username;

/*!
 * @abstract 发送跨应用单聊文本消息
 *
 * @param text 文本内容
 * @param username 单聊对象 username
 * @param userAppKey 单聊对象 appkey
 *
 * @discussion 快捷方法，不需要先创建消息而直接发送。
 */
+ (void)sendSingleTextMessage:(NSString *)text
                       toUser:(NSString *)username
                       appKey:(NSString *)userAppKey;

/*!
 * @abstract 发送单聊图片消息
 *
 * @param imageData 图片数据
 * @param username 单聊对象 username
 *
 * @discussion 快捷方法，不需要先创建消息而直接发送。
 */
+ (void)sendSingleImageMessage:(NSData *)imageData
                        toUser:(NSString *)username;

/*!
 * @abstract 发送跨应用单聊图片消息
 *
 * @param imageData 图片数据
 * @param username 单聊对象 username
 * @param userAppKey 单聊对象 appkey
 *
 * @discussion 快捷方法，不需要先创建消息而直接发送。
 */
+ (void)sendSingleImageMessage:(NSData *)imageData
                        toUser:(NSString *)username
                        appKey:(NSString *)userAppKey;

/*!
 * @abstract 发送单聊语音消息
 *
 * @param voiceData 语音数据
 * @param duration 语音时长
 * @param username 单聊对象 username
 *
 * @discussion 快捷方法，不需要先创建消息而直接发送。
 */
+ (void)sendSingleVoiceMessage:(NSData *)voiceData
                 voiceDuration:(NSNumber *)duration
                        toUser:(NSString *)username;

/*!
 * @abstract 发送跨应用单聊语音消息
 *
 * @param voiceData 语音数据
 * @param duration 语音时长
 * @param username 单聊对象 username
 * @param userAppKey 单聊对象 appkey
 *
 * @discussion 快捷方法，不需要先创建消息而直接发送。
 */
+ (void)sendSingleVoiceMessage:(NSData *)voiceData
                 voiceDuration:(NSNumber *)duration
                        toUser:(NSString *)username
                        appKey:(NSString *)userAppKey;

/*!
 * @abstract 发送单聊文件消息
 *
 * @param fileData 文件数据数据
 * @param fileName 文件名
 * @param username 单聊对象 username
 *
 * @discussion 快捷方法，不需要先创建消息而直接发送。
 */
+ (void)sendSingleFileMessage:(NSData *)fileData
                     fileName:(NSString *)fileName
                        toUser:(NSString *)username;

/*!
 * @abstract 发送跨应用单聊文件消息
 *
 * @param fileData 文件数据数据
 * @param fileName 文件名
 * @param username 单聊对象 username
 * @param userAppKey 单聊对象 appkey
 *
 * @discussion 快捷方法，不需要先创建消息而直接发送。
 */
+ (void)sendSingleFileMessage:(NSData *)fileData
                     fileName:(NSString *)fileName
                        toUser:(NSString *)username
                        appKey:(NSString *)userAppKey;

/*!
 * @abstract 发送单聊地理位置消息
 * @param latitude 纬度
 * @param longitude 经度
 * @param scale 缩放比例
 * @param address 详细地址
 * @param username 单聊对象
 * @discussion 快捷方法，不需要先创建消息而直接发送。
 */
+ (void)sendSingleLocationMessage:(NSNumber *)latitude
                  longitude:(NSNumber *)longitude
                      scale:(NSNumber *)scale
                    address:(NSString *)address
                     toUser:(NSString *)username;

/*!
 * @abstract 发送跨应用单聊地理位置消息
 * @param latitude 纬度
 * @param longitude 经度
 * @param scale 缩放比例
 * @param address 详细地址
 * @param username 单聊对象
 * @param userAppKey 单聊对象的appKey
 * @discussion 快捷方法，不需要先创建消息而直接发送。
 */
+ (void)sendSingleLocationMessage:(NSNumber *)latitude
                        longitude:(NSNumber *)longitude
                            scale:(NSNumber *)scale
                          address:(NSString *)address
                           toUser:(NSString *)username
                           appKey:(NSString *)userAppKey;

/*!
 * @abstract 发送群聊文本消息
 *
 * @param text 文本内容
 * @param groupId 群聊目标群组ID
 *
 * @discussion 快捷方法，不需要先创建消息而直接发送。
 */
+ (void)sendGroupTextMessage:(NSString *)text
                     toGroup:(NSString *)groupId;

/*!
 * @abstract 发送群聊图片消息
 *
 * @param imageData 图片数据
 * @param groupId 群聊目标群组ID
 *
 * @discussion 快捷方法，不需要先创建消息而直接发送。
 */
+ (void)sendGroupImageMessage:(NSData *)imageData
                      toGroup:(NSString *)groupId;

/*!
 * @abstract 发送群聊语音消息
 *
 * @param voiceData 语音数据
 * @param duration 语音时长
 * @param groupId 群聊目标群组ID
 *
 * @discussion 快捷方法，不需要先创建消息而直接发送。
 */
+ (void)sendGroupVoiceMessage:(NSData *)voiceData
                voiceDuration:(NSNumber *)duration
                      toGroup:(NSString *)groupId;

/*!
 * @abstract 发送群聊文件消息
 *
 * @param fileData 文件数据
 * @param fileName 文件名
 * @param groupId 群聊目标群组ID
 *
 * @discussion 快捷方法，不需要先创建消息而直接发送。
 */
+ (void)sendGroupFileMessage:(NSData *)fileData
                    fileName:(NSString *)fileName
                     toGroup:(NSString *)groupId;

/*!
 * @abstract 发送群聊地理位置消息
 * @param latitude 纬度
 * @param longitude 经度
 * @param scale 缩放比例
 * @param address 详细地址
 * @param groupId 群聊目标群组ID
 */
+ (void)sendGroupLocationMessage:(NSNumber *)latitude
                        longitude:(NSNumber *)longitude
                            scale:(NSNumber *)scale
                          address:(NSString *)address
                           toGroup:(NSString *)groupId;
/*!
 * @abstract 消息撤回
 *
 * @param message 需要撤回的消息
 * @param handler 结果回调
 *
 * - resultObject 撤回后的消息
 * - error        错误信息
 *
 * @discussion 注意：SDK可撤回3分钟内的消息
 */
+ (void)retractMessage:(JMSGMessage *)message completionHandler:(JMSGCompletionHandler)handler;

/*!
 * @abstract 消息转发
 *
 * @param message         需要转发的消息
 * @param target          目标 target，只能为 JMSGUser 或 JMSGGroup
 * @param optionalContent 可选功能，具体请查看 JMSGOptionalContent 类
 *
 * @discussion 注意：只能转发消息状态为 SendSucceed 和 ReceiveSucceed 的消息。
 */
+ (void)forwardMessage:(JMSGMessage *)message
                target:(id)target
       optionalContent:(JMSGOptionalContent *JMSG_NULLABLE)optionalContent;


///----------------------------------------------------
/// @name Message basic fields 消息基本属性
///----------------------------------------------------


/*!
 * 消息ID：这个 ID 是本地生成的ID，不是服务器端下发时的ID。
 */
@property(nonatomic, strong, readonly) NSString *msgId;

/*!
 * @abstract 服务器端下发的消息ID.
 * @discussion 一般用于与服务器端跟踪消息.
 */
@property(nonatomic, strong, readonly) NSString * JMSG_NULLABLE serverMessageId;

/*!
 * @abstract 消息发送目标
 *
 * @discussion 与 [fromUser] 属性相对应. 根据消息方向不同:
 *
 * - 收到的消息，target 就是我自己。
 * - 发送的消息，target 是我的聊天对象。
 *      单聊是对方用户;
 *      群聊是聊天群组, 也与当前会话的目标一致 [JMSGConversation target]
 */
@property(nonatomic, strong, readonly) id target;

/*!
 * @abstract 消息发送目标应用
 *
 * @discussion 这是为了支持跨应用聊天, 而新增的字段.
 *
 * 单聊时目标是 username. 当该用户为默认 appKey 时, 则不填此字段.
 * 群聊时目标是 groupId, 不填写此字段.
 *
 * @since 2.1.0
 */
@property(nonatomic, strong, readonly) NSString *targetAppKey;

/*!
 * @abstract 消息来源用户 Appkey
 *
 * @discussion 这是为了支持跨应用聊天, 而新增的字段.
 *
 * 不管群聊还是单聊, from_id 都是发送消息的 username. 当该用户是默认 appKey 时, 则不填写此字段.
 *
 * @since 2.1.0
 */
@property(nonatomic, strong, readonly) NSString *fromAppKey;

/*!
 * @abstract 消息来源用户
 *
 * @discussion 与 [target] 属性相对应. 根据消息方向不同:
 *
 * - 收到的消息, fromUser 是发出消息的对方.
 *      单聊是聊天对象, 也与当前会话目标用户一致 [JMSGConversation target],
 *      群聊是该条消息的发送用户.
 * - 发出的消息: fromUser 是我自己.
 */
@property(nonatomic, strong, readonly) JMSGUser *fromUser;

/*!
 * @abstract 消息来源类型
 * @discussion 默认的用户之间互发消息，其值是 "user"。如果是 App 管理员下发的消息，是 "admin"
 */
@property(nonatomic, strong, readonly) NSString *fromType;

/*!
 * @abstract 消息的内容类型
 */
@property(nonatomic, assign, readonly) JMSGContentType contentType;

/*!
 * @abstract 消息内容对象
 * @discussion 使用时应通过 contentType 先获取到具体的消息类型，然后转型到相应的具体类。
 */
@property(nonatomic, strong, readonly) JMSGAbstractContent * JMSG_NULLABLE content;

/*!
 * @abstract 消息发出的时间戳
 * @discussion 这是服务器端下发消息时的真实时间戳，单位为毫秒
 */
@property(nonatomic, strong, readonly) NSNumber *timestamp;

/*!
 * @abstract 消息中的fromName
 * @discussion 消息的发送方展示名称
 */
@property(nonatomic, strong, readonly) NSString *fromName;


///----------------------------------------------------
/// @name Message addOn fields 消息附加属性
///----------------------------------------------------

/*!
 * @abstract 聊天类型。当前支持的类型：单聊，群聊
 */
@property(nonatomic, assign, readonly) JMSGConversationType targetType;

/*!
 * @abstract 消息状态
 * @discussion 一条发出的消息，或者收到的消息，有多个状态会下。具体定义参考 JMSGMessageStatus 的定义。
 */
@property(nonatomic, assign, readonly) JMSGMessageStatus status;

/*!
 * @abstract 当前的消息是不是收到的。
 *
 * @discussion 是收到的，则是别人发给我的。UI 上一般展示在左侧。
 * 如果不是收到侧的，则是发送侧的，是我对外发送的。
 *
 * 主要是在聊天界面展示消息列表时，需要使用此方法，来确认展示消息的方式与位置。
 * 展示时需要发送方消息，不管是收到侧还是发送侧，都可以使用 fromUser 对象。
 */
@property(nonatomic, assign, readonly) BOOL isReceived;

/*!
 * @abstract 消息标志
 *
 * @discussion 这是一个用于表示消息状态的标识字段, App 可自由使用, SDK 不做变更.
 * 默认值为 0, App 有需要时可更新此状态.
 *
 * 使用场景:
 *
 * 1. 语音消息有一个未听标志. 默认 0 表示未读, 已读时 App 更新为 1 或者其他.
 * 2. 某些 App 需要对一条消息做送达, 已读标志, 可借用这个字段.
 */
@property(nonatomic, strong, readonly) NSNumber *flag;

/*!
 * @abstract 是否已读(只针对接收的消息)
 *
 * @discussion 该属性与实例方法 [-(void)setMessageHaveRead:] 是对应的。
 *
 * 注意：只有发送方调用 [+sendMessage:optionalContent:] 方法设置 message 需要已读回执，此属性才有意义。
 */
@property(nonatomic, assign, readonly) BOOL isHaveRead;

///----------------------------------------------------
/// @name Instance APIs 实例方法
///----------------------------------------------------

/*!
 * @abstract 默认的 init 方法不可用
 *
 * @discussion 如果已经得到 JMSGConversation 实例, 则可用以下方法来创建对象:
 *
 * - conversation -> createMessageWithContent:
 * - conversation -> createMessageAsyncWithImageContent::
 *
 * 或者不创建 JMSGMessage 实例也可以直接发送消息. 请参考 JMSGConversation 里相关方法.
 *
 * 如果你的 App 不依赖 JMSGConversation 实例, 也可以直接调用 JMSGMessage 里的类方法
 * 来创建 JMSGMessage 实例:
 *
 * - JMSGMessage -> createSingleMessageWithContent:
 * - JMSGMessage -> createGroupMessageWithContent:
 *
 * 或者直接也可以调用 JMSGMessage 类方法发消息而不必创建 JMSGMessage 对象.
 */
- (instancetype)init NS_UNAVAILABLE;

/*!
 * @abstract 是否是@自己的消息（只针对群消息，单聊消息无@功能）
 */
- (BOOL)isAtMe;

/*!
 * @abstract 是否是@所有人的消息（只针对群消息，单聊消息无@功能）
 */
- (BOOL)isAtAll;

/*!
 * @abstract 获取消息体中所有@对象（只针对群消息，单聊消息无@功能）
 *
 * @param handler 结果回调。回调参数：
 *
 * - resultObject 类型为 NSArray，数组里成员的类型为 JMSGUser
 * 注意：如果该消息为@所有人消息时，resultObject 返回nil，可以通过 isAtAll 接口来判断是否是@所有人的消息
 * - error 错误信息
 *
 * 如果 error 为 nil, 表示获取成功
 * 如果 error 不为 nil,表示获取失败
 *
 * @discussion 从服务器获取，返回消息的所有@对象。
 */
- (void)getAt_List:(JMSGCompletionHandler)handler;

/*!
 * @abstract 设置为已读
 *
 * @param handler 回调
 *
 * - resultObject 返回对应的 message，不过成功失败都会返回 message 对象
 * - error        不为 nil 表示操作失败
 *
 * @discussion 注意: 只针对消息接收方有效
 * 
 * 这是一个异步接口;
 *
 * 1、接收方：设置消息为已读状态后，isHaveRead 属性也会被设置为 YES，
 *
 * 2、发送方：会收到消息已读状态变更事件，SDK 会更新消息的未读人数。
 *
 * 注意：只有发送方调用 [+sendMessage:optionalContent:] 方法设置 message 需要已读回执，此方法才有效。
 */
- (void)setMessageHaveRead:(JMSGCompletionHandler)handler;

/*!
 * @abstract 消息未读人数
 *
 * @discussion 只针对消息发送方有效
 *
 * 注意：只有发送方调用 [+sendMessage:optionalContent:] 方法设置 message 需要已读回执，此方法才有意义。
 */
- (NSInteger)getMessageUnreadCount;

/*!
 * @abstract 已读未读用户列表
 *
 * @param handler 结果回调。回调参数:
 *
 * - unreadUsers  未读用户列表
 * - readsUsers   读用户列表
 * - error        不为nil表示出错
 *
 * @discussion 只针对消息发送方有效
 *
 * 注意：只有发送方调用 [+sendMessage:optionalContent:] 方法设置 message 需要已读回执，此方法才有意义。
 */
- (void)messageReadDetailHandler:(void(^)(NSArray *JMSG_NULLABLE readUsers, NSArray *JMSG_NULLABLE unreadUsers, NSError *JMSG_NULLABLE error))handler;

/*!
 * @abstract 取消正在发送的消息
 *
 * @discussion 在消息发送结果监听 [JMSGMessageDelegate onSendMessageResponse:error:] 里会返回对应的错误信息和错误码。
 *
 * @since 3.8.1
 */
- (void)cancelSendingMessage;

/*!
 * @abstract 设置消息的 fromName(即:通知栏的展示名称)
 *
 * @param fromName 本条消息在接收方通知栏的展示名称
 *
 * @discussion fromName填充在发出的消息体里，对方收到该消息通知时,在通知栏显示的消息发送人名称就是该字段的值.
 *
 */
- (void)setFromName:(NSString * JMSG_NULLABLE)fromName;

/*!
 * @abstract 更新 message 中的extra
 *
 * @param value   待更新的value,不能为null,类型只能为 NSNumber 和 NSString
 * @param key     待更新value对应的key,不能为null
 *
 * @discussion 如果 message 中没有该 key 对应的 extra 值，则会插入该新值
 */
- (BOOL)updateMessageExtraValue:(id)value forKey:(NSString *)key;

/*!
 * @abstract 更新消息标志
 *
 * @param flag 为 nil 时表示设置为 0.
 *
 * @discussion 参考 flag property 的说明.
 */
- (void)updateFlag:(NSNumber * JMSG_NULLABLE)flag;

/*!
 * @abstract 消息对象转换为 JSON 字符串的表示。
 *
 * @discussion 遵循 Message JSON 协议的定义。
 */
- (NSString *)toJsonString;

/*!
 * @abstract JSON 字符串 转换为 消息对象。
 *
 * @discussion 遵循 Message JSON 协议的定义。失败时返回 nil
 *
 * #### 注意：尽量不要自己随意拼接 json 字符串去转换，容易导致创建的 message 无法正常发送
 */
+ (JMSGMessage *JMSG_NULLABLE)fromJson:(NSString *JMSG_NONNULL)json;

/*!
 * @abstract 对象比较
 *
 * @param message 待比较的消息对象
 */
- (BOOL)isEqualToMessage:(JMSGMessage * JMSG_NULLABLE)message;


JMSG_ASSUME_NONNULL_END

@end



