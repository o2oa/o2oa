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

#ifndef JMessage_JMSGConstants____FILEEXTENSION___
#define JMessage_JMSGConstants____FILEEXTENSION___

#import <Foundation/Foundation.h>


///----------------------------------------------------
/// @name type & define
///----------------------------------------------------

/*!
 * @abstract 异步回调 block
 *
 * @discussion 大多数异步 API 都会以过个 block 回调。
 *
 * - 如果调用出错，则 error 不为空，可根据 error.code 来获取错误码。该错误码 JMessage 相关文档里有详细的定义。
 * - 如果返回正常，则 error 为空。从 resultObject 去获取相应的返回。每个 API 的定义上都会有进一步的定义。
 *
 */
typedef void (^JMSGCompletionHandler)(id resultObject, NSError *error);

/*!
 * @abstract 空的 CompletionHandler.
 *
 * @discussion 用于不需要进行处理时.
 */
#define JMSG_NULL_COMPLETION_BLOCK ^(id resultObject, NSError *error){}

/*!
 * @abstract 数据返回的异步回调
 *
 * @discussion 专用于数据返回 API.
 *
 * - objectId 当前数据的ID标识.
 *   - 消息里返回 voice/image 的 media data 时, objectId 是 msgId;
 *   - 用户里返回 avatar 头像数据时, objectId 是 username.
 *
 */
typedef void (^JMSGAsyncDataHandler)(NSData *data, NSString *objectId, NSError *error);

/*!
 * @abstract 媒体上传进度 block
 *
 * @discussion 在发送消息时，可向 JMSGMessage 对象设置此 block，从而可以得到上传的进度
 */
typedef void (^JMSGMediaProgressHandler)(float percent, NSString *msgId);

/*!
 * @abstract Generic 泛型
 */
#if __has_feature(objc_generics) || __has_extension(objc_generics)
#  define JMSG_GENERIC(...) <__VA_ARGS__>
#else
#  define JMSG_GENERIC(...)
#endif

/*!
 * @abstract nullable 用于定义某属性或者变量是否可允许为空
 */
#if __has_feature(nullability)
#  define JMSG_NONNULL __nonnull
#  define JMSG_NULLABLE __nullable
#else
#  define JMSG_NONNULL
#  define JMSG_NULLABLE
#endif

#if __has_feature(assume_nonnull)
#  ifdef NS_ASSUME_NONNULL_BEGIN
#    define JMSG_ASSUME_NONNULL_BEGIN NS_ASSUME_NONNULL_BEGIN
#  else
#    define JMSG_ASSUME_NONNULL_BEGIN _Pragma("clang assume_nonnull begin")
#  endif
#  ifdef NS_ASSUME_NONNULL_END
#    define JMSG_ASSUME_NONNULL_END NS_ASSUME_NONNULL_END
#  else
#    define JMSG_ASSUME_NONNULL_END _Pragma("clang assume_nonnull end")
#  endif
#else
#  define JMSG_ASSUME_NONNULL_BEGIN
#  define JMSG_ASSUME_NONNULL_END
#endif


///----------------------------------------------------
/// @name enums
///----------------------------------------------------

/*!
 * 会话类型 - 单聊、群聊
 */
typedef NS_ENUM(NSInteger, JMSGConversationType) {
  /// 单聊
  kJMSGConversationTypeSingle = 1,
  /// 群聊
  kJMSGConversationTypeGroup = 2,
  /// 聊天室
  kJMSGConversationTypeChatRoom = 3,
};

/*!
 * 群组类型 - 私有、公开
 */
typedef NS_ENUM(NSInteger, JMSGGroupType) {
  /// 私有群
  kJMSGGroupTypePrivate = 1,
  /// 公开群
  kJMSGGroupTypePublic = 2,
};

/*!
 * 消息内容类型 - 文本、语音、图片等
 */
typedef NS_ENUM(NSInteger, JMSGContentType) {
  /// 不知道类型的消息
  kJMSGContentTypeUnknown = 0,
  /// 文本消息
  kJMSGContentTypeText = 1,
  /// 图片消息
  kJMSGContentTypeImage = 2,
  /// 语音消息
  kJMSGContentTypeVoice = 3,
  /// 自定义消息
  kJMSGContentTypeCustom = 4,
  /// 事件通知消息。服务器端下发的事件通知，本地展示为这个类型的消息展示出来
  kJMSGContentTypeEventNotification = 5,
  /// 文件消息
  kJMSGContentTypeFile = 6,
  /// 地理位置消息
  kJMSGContentTypeLocation = 7,
  /// 提示性消息
  kJMSGContentTypePrompt = 8,
  /// 视频消息
  kJMSGContentTypeVideo = 9,
};

/*!
 * 提示性消息的类型 - 消息撤回提示、后台自定义消息提示等
 */
typedef NS_ENUM(NSInteger, JMSGPromptContentType) {
  /// 消息撤回提示
  kJMSGPromptContentTypeRetractMessage = 0,
  /// 自定义提示
  kJMSGPromptContentTypeAPPRbag   = 1,
};

/*!
 * 消息状态
 */
typedef NS_ENUM(NSInteger, JMSGMessageStatus) {
  /// 发送息创建时的初始状态
  kJMSGMessageStatusSendDraft = 0,
  /// 消息正在发送过程中. UI 一般显示进度条
  kJMSGMessageStatusSending = 1,
  /// 媒体类消息文件上传失败
  kJMSGMessageStatusSendUploadFailed = 2,
  /// 媒体类消息文件上传成功
  kJMSGMessageStatusSendUploadSucceed = 3,
  /// 消息发送失败
  kJMSGMessageStatusSendFailed = 4,
  /// 消息发送成功
  kJMSGMessageStatusSendSucceed = 5,
  /// 接收中的消息(还在处理)
  kJMSGMessageStatusReceiving = 6,
  /// 接收消息时自动下载媒体失败
  kJMSGMessageStatusReceiveDownloadFailed = 7,
  /// 接收消息成功
  kJMSGMessageStatusReceiveSucceed = 8,
  /// 消息已被取消发送
  kJMSGMessageStatusCanceledSend = 9,
};


/*!
 * 上传文件的类型
 */
typedef NS_ENUM(NSInteger, JMSGFileType) {
  /// 未知的文件类型
  kJMSGFileTypeUnknown,
  /// 图片类型
  kJMSGFileTypeImage,
  /// 语音类型
  kJMSGFileTypeVoice,
  /// 文件类型
  kJMSGFileTypeFile,
  /// 视频类型
  kJMSGFileTypeVideo,
};

/*!
 * 平台类型
 */
typedef NS_ENUM(NSInteger, JMSGPlatformType) {
  /// 所有平台
  kJMSGPlatformTypeAll        = 0,
  /// Android 端
  kJMSGPlatformTypeAndroid    = 1,
  /// iOS 端
  kJMSGPlatformTypeiOS        = 2,
  /// Windows 端
  kJMSGPlatformTypeWindows    = 4,
  /// web 端
  kJMSGPlatformTypeWeb        = 16,
};

/*!
 * 群成员类型
 */
typedef NS_ENUM(NSInteger, JMSGGroupMemberType) {
  /// 普通成员
  kJMSGGroupMemberTypeOrdinary = 0,
  /// 群主
  kJMSGGroupMemberTypeOwner    = 1,
  /// 管理员
  kJMSGGroupMemberTypeAdmin    = 2,
};

/*!
 * 发送消息透传的的类型
 */
typedef NS_ENUM(NSInteger,JMSGTransMessageType) {
  /// 单聊透传消息
  kJMSGTransMessageTypeSingle        = 1,
  /// 群里透传消息
  kJMSGTransMessageTypeGroup        = 2,
  /// 设备间透传消息
  kJMSGTransMessageTypeCrossDevice  = 3,
};

/*!
 * 消息事件类型
 *
 * 本类 JMSGEventNotificationType 是 SDK 下发的通知事件类型,主要分为：消息事件、非消息事件
 *
 * #### 消息事件
 *
 * 如：群事件，SDK 会作为一个特殊的消息类型下发，上层通过 JMSGMessageDelegate 类接收消息的代理方法接收消息事件.
 *
 * #### 非消息事件
 *
 * 如：被踢、加好友等，SDK 会作为通知事件下发,上层通过 JMSGEventDelegate、JMSGGroupDelegate 这些对应类的代理方法可监听此类事件.
 */
typedef NS_ENUM(NSInteger, JMSGEventNotificationType) {
  // 消息事件
  /// 事件类型: 群组被创建
  kJMSGEventNotificationCreateGroup = 8,
  /// 事件类型: 退出群组
  kJMSGEventNotificationExitGroup = 9,
  /// 事件类型: 群组添加新成员
  kJMSGEventNotificationAddGroupMembers = 10,
  /// 事件类型: 群组成员被踢出
  kJMSGEventNotificationRemoveGroupMembers = 11,
  /// 事件类型: 群信息更新
  kJMSGEventNotificationUpdateGroupInfo = 12,
  /// 事件类型: 群禁言通知事件
  kJMSGEventNotificationGroupMemberSilence = 65,
  /// 事件类型: 管理员角色变更通知事件
  kJMSGEventNotificationGroupAdminChange = 80,
  /// 事件类型: 群主变更通知事件
  kJMSGEventNotificationGroupOwnerChange = 82,
  /// 事件类型: 群类型变更通知事件
  kJMSGEventNotificationGroupTypeChange = 83,
  /// 事件类型: 解散群组
  kJMSGEventNotificationDissolveGroup = 11001,
  /// 事件类型: 群组成员上限变更
  kJMSGEventNotificationGroupMaxMemberCountChange = 11002,
};

/*!
 * 通知事件类型
 *
 * #### 上层通过 JMSGEventDelegate 类里的方法来监听此类事件
 */
typedef NS_ENUM(NSInteger, JMSGCommonEventType){
  /// 事件类型: 消息撤回
  kJMSGEventNotificationMessageRetract = 55,
  /// 事件类型: 消息透传
  kJMSGEventNotificationMessageTransparent = 12001,
  /// 事件类型: 消息回执变更
  kJMSGEventNotificationMessageReceiptStatusChange = 12002,
};

/*!
 * 用户登录状态变更事件类型
 *
 * #### 上层通过 JMSGUserDelegate 类里的方法来监听此类事件
 */
typedef NS_ENUM(NSInteger, JMSGLoginStatusChangeEventType) {
  // 用户登录状态变更事件
  /// 事件类型: 登录被踢
  kJMSGEventNotificationLoginKicked = 1,
  /// 事件类型: 非客户端修改密码强制登出事件
  kJMSGEventNotificationServerAlterPassword = 2,
  /// 事件类型：用户登录状态异常事件（需要重新登录）
  kJMSGEventNotificationUserLoginStatusUnexpected = 70,
  /// 事件类型：当前登录用户信息变更通知事件(非客户端修改)
  kJMSGEventNotificationCurrentUserInfoChange = 40,
  /// 事件类型：当前登录用户被删除事件（本地用户信息会被清空）
  kJMSGEventNotificationCurrentUserDeleted = 10001,
  /// 事件类型：当前登录用户被禁用事件（本地用户信息会被清空）
  kJMSGEventNotificationCurrentUserDisabled = 10002,
};

/*!
 * 好友事件类型
 *
 * #### 上层通过 JMSGEventDelegate 类里的方法来监听此类事件
 */
typedef NS_ENUM(NSInteger, JMSGFriendEventType) {
  // 好友相关事件
  /// 事件类型: 收到好友邀请
  kJMSGEventNotificationReceiveFriendInvitation   = 51,
  /// 事件类型: 对方接受了你的好友邀请
  kJMSGEventNotificationAcceptedFriendInvitation  = 52,
  /// 事件类型: 对方拒绝了你的好友邀请
  kJMSGEventNotificationDeclinedFriendInvitation  = 53,
  /// 事件类型: 对方将你从好友中删除
  kJMSGEventNotificationDeletedFriend             = 6,
  /// 事件类型：非客户端修改好友关系收到好友更新事件
  kJMSGEventNotificationReceiveServerFriendUpdate = 7,
};

/*!
 * 群组事件类型
 *
 * #### 上层通过 JMSGGroupDelegate 类里的方法来监听此类事件
 */
typedef NS_ENUM(NSInteger, JMSGGroupEventType) {
  /// 事件类型：发布群公告
  kJMSGEventNotificationPublishGroupAnnouncement  = 86,
  /// 事件类型：删除群公告
  kJMSGEventNotificationDeleteGroupAnnouncement = 87,
  /// 事件类型：置顶/取消置顶 群公告
  kJMSGEventNotificationTopGroupAnnouncement = 88,
  /// 事件类型：添加群组黑名单
  kJMSGEventNotificationAddGroupBlacklist = 89,
  /// 事件类型：删除群组黑名单
  kJMSGEventNotificationDelGroupBlacklist = 90,
};

/*!
 * 聊天室事件类型
 *
 * #### 上层通过 JMSGEventDelegate 类里的方法来监听此类事件
 */
typedef NS_ENUM(NSInteger, JMSGChatRoomEventType) {
  /// 事件类型：添加管理员
  kJMSGEventNotificationChatRoomAddAdmin = 130,
  /// 事件类型：删除管理员
  kJMSGEventNotificationChatRoomDelAdmin = 131,
  /// 事件类型：添加黑名单
  kJMSGEventNotificationChatRoomAddBlacklist = 132,
  /// 事件类型：删除黑名单
  kJMSGEventNotificationChatRoomDelBlacklist = 133,
  /// 事件类型：添加禁言通知
  kJMSGEventNotificationChatRoomAddSilence = 135,
  /// 事件类型：解除禁言通知
  kJMSGEventNotificationChatRoomDelSilence = 136,
};

///----------------------------------------------------
/// @name errors
///----------------------------------------------------

/*!
 * @abstract JMessage SDK 的错误码汇总
 *
 * @discussion 错误码以 86 打头，都为 iOS SDK 内部的错误码
 */
typedef NS_ENUM(NSInteger, JMSGSDKErrorCode) {

  // --------------------- Network (860xxx)

  /// 下载失败
  kJMSGErrorSDKNetworkDownloadFailed = 860015,
  /// 其他网络原因
  kJMSGErrorSDKNetworkOtherError = 860016,
  /// 服务器获取用户Token失败
  kJMSGErrorSDKNetworkTokenFailed = 860017,
  /// 上传资源文件失败
  kJMSGErrorSDKNetworkUploadFailed = 860018,
  /// 上传资源文件Token验证失败
  kJMSGErrorSDKNetworkUploadTokenVerifyFailed = 860019,
  /// 获取服务器Token失败
  kJMSGErrorSDKNetworkUploadTokenGetFailed = 860020,
  /// 服务器返回数据错误（没有按约定返回）
  kJMSGErrorSDKNetworkResultUnexpected = 860021,
  /// 服务器端返回数据格式错误
  kJMSGErrorSDKNetworkDataFormatInvalid = 860030,

  // --------------------- DB & Global params (861xxx)

  /// 数据库删除失败 (预期应该成功)
  kJMSGErrorSDKDBDeleteFailed = 861000,
  /// 数据库更新失败 (预期应该成功)
  kJMSGErrorSDKDBUpdateFailed = 861001,
  /// 数据库查询失败 (预期应该成功)
  kJMSGErrorSDKDBSelectFailed = 861002,
  /// 数据库插入失败 (预期应该成功)
  kJMSGErrorSDKDBInsertFailed = 861003,
  /// 数据库迁移失败
  kJMSGErrorSDKDBMigrateFailed = 861004,
  
  /// Appkey 不合法
  kJMSGErrorSDKParamAppkeyInvalid = 861100,
  /// SDK 内部方法参数检查错误
  kJMSGErrorSDKParamInternalInvalid = 861101,

  // ------------------------ Third party (862xxx)

  /// 七牛相关
  kJMSGQiniuTokenInvalid = 862001,

  // ------------------------ User (863xxx)

  /// 用户名不合法
  kJMSGErrorSDKParamUsernameInvalid = 863001,
  /// 用户密码不合法
  kJMSGErrorSDKParamPasswordInvalid = 863002,
  /// 用户未登录
  kJMSGErrorSDKUserNotLogin = 863004,
  // 请求用户数量超出限制（目前单次最大请求500个）
  kJMSGErrorSDKUserNumberOverflow = 863005,
  // 用户已登录
  kJMSGErrorSDKUserInvalidState = 863006,
  // 用户正在退出的过程中
  kJMSGErrorSDKUserLogoutingState = 863007,
  // 添加好友失败
  kJMSGErrorSDKUserAddFriendFailState = 863008,
  // 删除好友失败
  kJMSGErrorSDKUserDeleteFriendFailState = 863009,
  
  // ------------------------ Media Resource (864xxx)

  /// 这不是一条媒体消息
  kJMSGErrorSDKNotMediaMessage = 864001,
  /// 下载媒体资源路径或者数据意外丢失
  kJMSGErrorSDKMediaResourceMissing = 864002,
  /// 媒体CRC码无效
  kJMSGErrorSDKMediaCrcCodeIllegal = 864003,
  /// 媒体CRC校验失败
  kJMSGErrorSDKMediaCrcVerifyFailed = 864004,
  /// 上传媒体文件时, 发现文件不存在
  kJMSGErrorSDKMediaUploadEmptyFile = 864005,
  /// 媒体HASH码无效
  kJMSGErrorSDKMediaHashCodeIllegal = 864006,
  /// 媒体HASH校验失败
  kJMSGErrorSDKMediaHashVerifyFailed = 864007,
  /// 这条消息不支持转发
  kJMSGErrorSDKMessageNotSupportForward = 864008,
  
  // ------------------------ Message (865xxx)

  /// 无效的消息内容
  kJMSGErrorSDKParamContentInvalid = 865001,
  /// 空消息
  kJMSGErrorSDKParamMessageNil = 865002,
  /// 消息不符合发送的基本条件检查
  kJMSGErrorSDKMessageNotPrepared = 865003,
  /// 你不是群组成员
  kJMSGErrorSDKMessageNotInGroup = 865004,
  /// 非法的 JSON 格式, 无法解析 Message JSON Protocol
  kJMSGErrorSDKMessageProtocolInvalidJsonFormat = 865005,
  /// 消息协议格式不正确, 可能缺少了字段
  kJMSGErrorSDKMessageProtocolLackFields = 865006,
  /// 消息协议格式不正确, 字段值非法
  kJMSGErrorSDKMessageProtocolInvalidFieldValue = 865007,
  /// 本地消息协议需要升级(收到新版本)
  kJMSGErrorSDKMessageProtocolUpgradeNeeded = 865008,
  /// 收到不支持消息内容类型(建议升级)
  kJMSGErrorSDKMessageProtocolContentTypeNotSupport = 865009,
  /// 消息状态不合法
  kJMSGErrorSDKMessageStatusNotLegal = 865010,
  /// 取消发送消息
  kJMSGErrorSDKMessageCancelSend = 865011,
  /// 取消下载消息多媒体文件
  kJMSGErrorSDKMessageCancelDownload = 865012,


  // ------------------------ Conversation (866xxx)

  /// 未知的会话类型
  kJMSGErrorSDKParamConversationTypeUnknown = 866001,
  /// 会话 username 无效
  kJMSGErrorSDKParamConversationUsernameInvalid = 866002,
  /// 会话 groupId 无效
  kJMSGErrorSDKParamConversationGroupIdInvalid = 866003,
  /// 会话没有找到
  kJMSGErrorSDKparamConversationNotFound = 866004,
  /// 会话头像没找到
  kJMSGErrorSDKConversationAvatarNotFound = 866005,

  // ------------------------ Group (867xxx)

  /// groupId 无效
  kJMSGErrorSDKParamGroupGroupIdInvalid = 867001,
  /// group 相关字段无效
  kJMSGErrorSDKParamGroupGroupInfoInvalid = 867002,

  // ------------------------ ChatRoom (868xxx)
  
  /// ChatRoom 不支持
  kJMSGErrorSDKChatRoomNotSupport = 868001,
  /// ChatRoom 不存在
  kJMSGErrorSDKChatRoomNotExist   = 868002,
  
  /// unknown
  kJMSGErrorSDKUnknownError = 869999,
};

/*!
 * @abstract SDK依赖的内部 HTTP 服务返回的错误码。
 *
 * @discussion 这些错误码也会直接通过 SDK API 返回给应用层。
 */
typedef NS_ENUM(NSUInteger, JMSGHttpErrorCode) {
  /// 服务器端内部错误
  kJMSGErrorHttpServerInternal = 898000,
  /// 注册用户已经存在 (403)
  kJMSGErrorHttpUserExist = 898001,
  /// 用户不存在 (403)
  kJMSGErrorHttpUserNotExist = 898002,
  /// 参数无效 (400)
  kJMSGErrorHttpPrameterInvalid = 898003,
  /// 密码错误 (403)
  kJMSGErrorHttpPasswordError = 898004,
  /// 内部UID 无效 (403)
  kJMSGErrorHttpUidInvalid = 898005,
  /// Gid不存在 (403)
  kJMSGErrorHttpGroupNotExist = 898006,
  /// Http 请求没有验证信息
  kJMSGErrorHttpMissingAuthenInfo = 898007,
  /// Http 请求验证失败
  kJMSGErrorHttpAuthenticationFailed = 898008,
  /// Appkey 不存在
  kJMSGErrorHttpAppkeyNotExist = 898009,
  /// Http 请求 token 过期
  kJMSGErrorHttpTokenExpired = 898010,
  /// 服务器端响应超时
  kJMSGErrorHttpServerResponseTimeout = 898030,
};

/*!
 * @abstract SDK依赖的内部 TCP 服务返回的错误码
 *
 * @discussion 这些错误码也会直接通过 SDK API 返回给应用层。
 */
typedef NS_ENUM(NSUInteger, JMSGTcpErrorCode) {
  /// appKey 未被注册
  kJMSGErrorTcpAppkeyNotRegistered = 800003,
  /// 服务器端内部错误
  kJMSGErrorTcpServerInternalError = 800009,
  /// 用户在登出状态
  kJMSGErrorTcpUserLogoutState = 800012,
  /// 用户在离线状态
  kJMSGErrorTcpUserOfflineState = 800013,
  /// 发起请求的用户设备不匹配
  kJMSGErrorTcpUserDeviceNotMatch = 800016,
  /// 用户未注册
  kJMSGErrorTcpUserNotRegistered = 801003,
  /// 用户密码错误
  kJMSGErrorTcpUserPasswordError = 801004,
  /// 用户被禁用
  kJMSGErrorTcpUserDisabled = 801006,
  /// 多通道同时登录错误，登录失败
  kJMSGErrorTcpLoginMultiChannelError = 801007,
  /// 目标用户不存在
  kJMSGErrorTcpTargetUserNotExist = 803003,
  /// 目标群组不存在
  kJMSGErrorTcpTargetGroupNotExist = 803004,
  /// 用户不在群组里
  kJMSGErrorTcpUserNotInGroup = 803005,
  /// 用户在黒名单里
  kJMSGErrorTcpUserInBlacklist = 803008,
  /// 内容不合法
  kJMSGErrorTcpContentIsIllegal = 803009,
  /// 发送消息失败，请求用户被禁言
  kJMSGErrorTcpUserBannedSendMessage = 803012,
  /// 群组成员列表为空
  kJMSGErrorTcpGroupMembersEmpty = 810002,
  /// 群组成员重复
  kJMSGErrorTcpGroupMembersDuplicated = 810007,
  /// 重复申请入群或重复邀请成员入群
  kJMSGErrorTcpGroupApplyRepeat  = 856003,
  /// 用户在聊天室中被禁言
  kJMSGErrorTcpUserBannedInChatRoom = 847002,
  /// 用户已经在聊天室黑名单中
  kJMSGErrorTcpUserHasInChatRoomBlacklist = 7132006,
  /// 目标用户不在聊天室黑名单里
  kJMSGErrorTcpUserNotInChatRoomBlacklist = 7133001,
  /// 用户已经在聊天室管理员列表中
  kJMSGErrorTcpUserHasInChatRoomAdminList = 7130002,
  /// 目标用户不在聊天室管理员列表里
  kJMSGErrorTcpUserNotInChatRoomAdminList = 7131002,
  /// 禁言时间不在允许范围内
  kJMSGErrorTcpMemberSilenceTimesOverLimit = 765003,
};


///----------------------------------------------------
/// @name Global keys 全局静态变量定义
///----------------------------------------------------

// General key

static NSString *const KEY_APP_KEY = @"appkey";


// User

static NSString *const KEY_USERNAME = @"username";
static NSString *const KEY_PASSWORD = @"password";

static NSString *const KEY_NEW_PASSWORD = @"new_password";
static NSString *const KEY_OLD_PASSWORD = @"old_password";

static NSString *const KEY_NICKNAME = @"nickname";  //昵称
static NSString *const KEY_AVATAR = @"avatar";      //头像
static NSString *const KEY_GENDER = @"gender";      //性别
static NSString *const KEY_BIRTHDAY = @"birthday";  //生日
static NSString *const KEY_REGION = @"region";      //区域
static NSString *const KEY_SIGNATURE = @"signature";//签名
static NSString *const KEY_ADDRESS = @"address";    //地址
static NSString *const KEY_STAR = @"star";

#endif

