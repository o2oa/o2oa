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
#import <JMessage/JMSGConstants.h>

@class JMSGUser,JMSGApplyJoinGroupEvent;


/*!
 * 群成员信息类
 *
 * #### 可通过 [JMSGGroup memberInfoList:]和 [JMSGGroup memberInfoWithUsername:appkey:] 两个接口获取群成员信息
 */
@interface JMSGGroupMemberInfo : NSObject

/// 成员用户信息
@property(nonatomic, strong, readonly) JMSGUser *JMSG_NULLABLE user;
/// 入群时间
@property(nonatomic, assign, readonly) UInt64 ctime;
/// 群昵称
@property(nonatomic, strong, readonly) NSString *JMSG_NULLABLE groupNickname;
/// 群组成员的身份
@property(nonatomic, assign, readonly) JMSGGroupMemberType memberType;

/*!
 * @abstract 获取群成员的展示名
 *
 * @discussion 展示优先级：群昵称 > 好友备注(user.noteName) > 用户昵称(user.nickname) > 用户名(user.username)
 *
 * #### 同接口 [JMSGGroup memberDisplayName:] 相同效果
 */
- (NSString *JMSG_NULLABLE)displayName;
@end

/*!
 * 成员禁言信息类
 *
 * @discussion 如果是群组成员禁言，则可通过 [JMSGGroup getGroupMemberSilenceList:]和 [JMSGGroup getGroupMemberSilence:appKey:handler:] 两个接口获取禁言信息；如果是聊天室成员禁言，则可通过 [JMSGChatRoom getChatRoomSilencesWithStart:count:handler:] 和 [JMSGChatRoom getChatRoomMemberSilenceWithUsername:appKey:handler:] 两个接口获取禁言信息
 */
@interface JMSGMemberSilenceInfo : NSObject
/// 成员用户信息
@property(nonatomic, strong, readonly) JMSGUser *JMSG_NULLABLE user;
/// 群成员禁言开始时间
@property(nonatomic, assign, readonly) UInt64 silenceStartTime;
/// 群成员禁言结束时间
@property(nonatomic, assign, readonly) UInt64 silenceEndTime;
@end

/*!
 * 群公告类
 */
@interface JMSGGroupAnnouncement : NSObject
/// 公告 id
@property(nonatomic, assign, readonly) UInt32 announcementId;
/// 群组 id
@property(nonatomic, strong, readonly) NSString *JMSG_NONNULL gid;
/// 公告内容
@property(nonatomic, strong, readonly) NSString *JMSG_NULLABLE text;
/// 发布者
@property(nonatomic, strong, readonly) JMSGUser *JMSG_NULLABLE publisher;
/// 发布时间
@property(nonatomic, assign, readonly) UInt64 publishTime;
/// 是否置顶
@property(nonatomic, assign, readonly) BOOL isTop;
/// 置顶时间
@property(nonatomic, assign, readonly) UInt64 topTime;

/*!
 * @abstract 公告对象转换为 JSON 字符串的表示。
 */
- (NSString *JMSG_NULLABLE)toJsonString;


/*!
 * @abstract JSON 字符串 转换为 公告对象。
 *
 * @discussion 失败时返回 nil
 */
+ (JMSGGroupAnnouncement *JMSG_NULLABLE)fromJson:(NSString *JMSG_NONNULL)json;

@end

/*!
 * 群信息类（此类仅用于修改群信息、创建群、群信息展示）
 *
 * #### 注意：
 *
 * 如果想要获取群的相关属性值、调用相关接口，需要通过 gid 获取到 JMSGGroup 对象再使用；
 *
 * 本类中可读可写属性表示可以用于群信息修改、创建群传值，只读属性说明是不允许客户端修改的，只做展示；
 */
@interface JMSGGroupInfo : NSObject

/** 群 id */
@property(nonatomic, strong, readonly) NSString *JMSG_NONNULL gid;
/** 群名称 */
@property(nonatomic, strong, readwrite) NSString *JMSG_NONNULL name;
/** 群描述 */
@property(nonatomic, strong, readwrite) NSString *JMSG_NONNULL desc;
/** 群头像数据，此属性只用户修改群信息，切勿从此类拿来此属性来展示 */
@property(nonatomic, strong, readwrite) NSData   *JMSG_NONNULL avatarData;
/** 群头像的媒体文件ID */
@property(nonatomic, strong, readonly) NSString *JMSG_NONNULL avatar;
/** 群组类型，私有、公开，注意：仅限于创建群组时设置，创建成功之后不允许修改群类型*/
@property(nonatomic, assign, readwrite) JMSGGroupType groupType;
/** 群组人数上限，注意：仅限于创建群组时可以设置，必须大于 2 */
@property(nonatomic, strong, readwrite) NSString *JMSG_NONNULL maxMemberCount;
/** 群组创建时间*/
@property(nonatomic, assign, readonly) SInt64  ctime;

@end

/*!
 * 群组
 *
 * 群组表示一组用户, 是群组聊天的聊天对象.
 *
 * 主要包含两类 API: 群组信息维护, 群组成员变更.
 */
@interface JMSGGroup : NSObject

JMSG_ASSUME_NONNULL_BEGIN


///----------------------------------------------------
/// @name Group Info Maintenance 群组信息维护
///----------------------------------------------------

/*!
 * @abstract 创建群组(只能创建私有群)
 *
 * @param groupName 群组名称
 * @param groupDesc 群组描述信息
 * @param usernameArray 初始成员列表。NSArray 里的类型是 NSString
 * @param handler 结果回调。正常返回 resultObject 的类型是 JMSGGroup。
 *
 * @discussion 向服务器端提交创建群组请求，返回生成后的群组对象.
 * 返回群组对象, 群组ID是App 需要关注的, 是后续各种群组维护的基础.
 */
+ (void)createGroupWithName:(NSString * JMSG_NULLABLE )groupName
                       desc:(NSString *JMSG_NULLABLE)groupDesc
                memberArray:(NSArray JMSG_GENERIC(__kindof NSString *) *JMSG_NULLABLE)usernameArray
          completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 创建群组（可创建私有群、公开群）
 *
 * @param groupInfo     群信息类，如：群名、群类型等，详细请查看 JMSGGroupInfo 类
 * @param usernameArray 初始成员列表。NSArray 里的类型是 NSString
 * @param handler       结果回调。正常返回 resultObject 的类型是 JMSGGroup。
 *
 * @discussion 向服务器端提交创建群组请求，返回生成后的群组对象.
 * 返回群组对象, 群组ID是App 需要关注的, 是后续各种群组维护的基础.
 */
+ (void)createGroupWithGroupInfo:(JMSGGroupInfo *)groupInfo
                     memberArray:(NSArray JMSG_GENERIC(__kindof NSString *) *JMSG_NULLABLE)usernameArray
               completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;
/*!
 * @abstract 更新群组信息
 *
 * @param groupId 待更新的群组ID
 * @param groupName 新名称
 * @param groupDesc 新描述
 * @param handler 结果回调. 正常返回时, resultObject 为 nil.
 *
 * @discussion 注意：name 和 desc 不允许传空字符串
 */
+ (void)updateGroupInfoWithGroupId:(NSString *)groupId
                              name:(NSString *JMSG_NULLABLE)groupName
                              desc:(NSString *JMSG_NULLABLE)groupDesc
                 completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 更新群信息（统一字段上传）
 *
 * @param gid         群组 id
 * @param groupInfo   群信息类，详细请查看 JMSGGroupInfo 类
 * @param handler     结果回调. 正常返回时, resultObject 为 nil.
 *
 * @discussion 注意：修改群名称和群描述时参数不允许传空字符串，群类型不允许修改
 */
+ (void)updateGroupInfoWithGid:(NSString *)gid
                     groupInfo:(JMSGGroupInfo *)groupInfo
             completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 更新群头像（支持传图片格式）
 *
 * @param groupId         待更新的群组ID
 * @param avatarData      头像数据
 * @param avatarFormat    头像格式，可以为空，不包括"."
 * @param handler         回调
 *
 * @discussion 头像格式参数直接填格式名称，不要带点。正确：@"png"，错误：@".png"
 */
+ (void)updateGroupAvatarWithGroupId:(NSString *JMSG_NONNULL)groupId
                          avatarData:(NSData *JMSG_NONNULL)avatarData
                        avatarFormat:(NSString *JMSG_NULLABLE)avatarFormat
                   completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 获取群组信息
 *
 * @param groupId 待获取详情的群组ID
 * @param handler 结果回调. 正常返回时 resultObject 类型是 JMSGGroup.
 *
 * @discussion 如果考虑性能损耗, 在群聊时获取群组信息, 可以获取 JMSGConversation -> target 属性.
 */
+ (void)groupInfoWithGroupId:(NSString *)groupId
           completionHandler:(JMSGCompletionHandler)handler;

/*!
 * @abstract 获取我的群组列表
 *
 * @param handler 结果回调。正常返回时 resultObject 的类型是 NSArray(NSNumber)，数组里的成员类型是JMSGGroup的gid
 *
 * @discussion 该接口总是向服务器端发起请求。
 */
+ (void)myGroupArray:(JMSGCompletionHandler)handler;

/*!
 * @abstract 获取所有设置群消息屏蔽的群组
 *
 * @param handler 结果回调。回调参数：
 *
 * - resultObject 类型为 NSArray，数组里成员的类型为 JMSGGroup
 * - error 错误信息
 *
 * 如果 error 为 nil, 表示设置成功
 * 如果 error 不为 nil,表示设置失败
 *
 * @discussion 从服务器获取，返回所有设置群消息屏蔽的群组。
 */
+ (void)shieldList:(JMSGCompletionHandler)handler;

/*!
 * @abstract 分页获取 appkey 下所有公开群信息
 *
 * @param appkey    群组所在的 AppKey，不填则默认为当前应用 AppKey
 * @param start     分页获取的下标，第一页从  index = 0 开始
 * @param count     每一页的数量，最大值为500
 * @param handler   结果回调，NSArray<JMSGGroupInfo>
 *
 * #### 注意：
 *
 * 返回数据中不是 JMSGGroup 类型，而是 JMSGGroupInfo 类型，只能用于展示信息，如果想要调用相关群组 API 接口则需要通过 gid 获取到 JMSGGroup 对象才可以调用
 */
+ (void)getPublicGroupInfoWithAppKey:(NSString *JMSG_NULLABLE)appkey
                               start:(NSInteger)start
                               count:(NSInteger)count
                   completionHandler:(JMSGCompletionHandler)handler;

/*!
 * @abstract 申请加入群组
 *
 * @param gid     群组 gid
 * @param reason   申请原因
 * @param handler 结果回调
 *
 * @discussion 只有公开群需要申请才能加入，私有群不需要申请。
 */
+ (void)applyJoinGroupWithGid:(NSString *JMSG_NONNULL)gid
                       reason:(NSString *JMSG_NULLABLE)reason
            completionHandler:(JMSGCompletionHandler)handler;

/*!
 * @abstract 管理员审批入群申请
 *
 * @patam eventId     入取申请事件的 id，详情请查看 JMSGApplyJoinGroupEvent 类
 * @param gid         群组 gid
 * @param joinUser    入群的用户
 * @param applyUser   发起申请的的用户，如果是主动申请入群则和 member 是相同的
 * @param isAgree     是否同意申请，YES : 同意， NO: 不同意
 * @param reason      拒绝申请的理由，选填
 * @param handler     结果回调
 *
 * @discussion 只有管理员才有权限审批入群申请，SDK 不会保存申请入群事件(JMSGApplyJoinGroupEvent)，上层可以自己封装再保存，或则归档直接保存，以便此接口取值调用。
 */
+ (void)processApplyJoinGroupEventID:(NSString *JMSG_NONNULL)eventId
                                 gid:(NSString *JMSG_NONNULL)gid
                            joinUser:(JMSGUser *JMSG_NONNULL)joinUser
                           applyUser:(JMSGUser *JMSG_NONNULL)applyUser
                             isAgree:(BOOL)isAgree
                              reason:(NSString *JMSG_NULLABLE)reason
                             handler:(JMSGCompletionHandler)handler;

/*!
 * @abstract 管理员审批入群申请（批量接口）
 *
 * @patam events      入取申请事件的 eventId 数组，详情请查看 JMSGApplyJoinGroupEvent 类
 * @param isAgree     是否同意申请，YES : 同意， NO: 不同意
 * @param reason      拒绝申请的理由，选填
 * @param isSendInviter 是否将结果通知给邀请方，默认是 NO
 * @param handler     结果回调
 *
 * @discussion 批量处理接口，event 下包含的所有被邀请者会被一起审批处理。只有管理员才有权限审批入群申请。
 */
+ (void)processApplyJoinGroupEvents:(NSArray <__kindof NSString *>*)events
                            isAgree:(BOOL)isAgree
                             reason:(NSString *JMSG_NULLABLE)reason
                        sendInviter:(BOOL)isSendInviter
                            handler:(JMSGCompletionHandler)handler;

/*!
 * @abstract 解散群组
 *
 * @patam gid     需要解散的群组 id
 * @param handler 结果回调,error = nil 表示操作成功
 *
 * @discussion 只有群主才有权限解散群。
 */
+ (void)dissolveGroupWithGid:(NSString *)gid handler:(JMSGCompletionHandler)handler;

///----------------------------------------------------
/// @name Group basic fields 群组基本属性
///----------------------------------------------------


/*!
 * @abstract 群组ID
 *
 * @discussion 该ID由服务器端生成，全局唯一。可以用于服务器端 API。
 */
@property(nonatomic, strong, readonly) NSString *gid;

/*!
 * @abstract 群组名称
 *
 * @discussion 可用于群组聊天的展示名称
 */
@property(nonatomic, copy, readonly) NSString * JMSG_NULLABLE name;

/*!
 * @abstract 群组描述信息
 */
@property(nonatomic, copy, readonly) NSString * JMSG_NULLABLE desc;

/*!
 * @abstract 群组等级
 *
 * @discussion 不同等级的群组，人数上限不同。当前默认等级 4，人数上限 200。客户端不可更改。
 */
@property(nonatomic, strong, readonly) NSNumber *level;

/*!
 * @abstract 群组头像（媒体文件ID）
 *
 * @discussion 此文件ID仅用于内部更新，不支持外部URL。
 */
@property(nonatomic, strong, readonly) NSString * JMSG_NULLABLE avatar;

/*!
 * @abstract 群组设置标志位
 *
 * @discussion 这是一个内部状态标志，对外展示仅用于调试目的。客户端不可更改。
 */
@property(nonatomic, strong, readonly) NSNumber *flag;

/*!
 * @abstract 群组类型
 *
 * @discussion 目前群组类型有：公开群、私有群。公开群是有权限设置，入群需要群主审核同意方可入群。
 */
@property(nonatomic, assign, readonly) JMSGGroupType groupType;

/*!
 * @abstract 群主（用户的 username）
 *
 * @discussion 有一套确认群主的策略。简单地说，群创建人是群主；如果群主退出，则是第二个加入的人，以此类似。客户端不可更改。
 */
@property(nonatomic, copy, readonly) NSString *owner;

/*!
 * @abstract 群主的appKey
 *
 * @discussion 当有跨应用群成员与群主同名(username相同)时，可结合用这个ownerAppKey来判断群主。
 */
@property(nonatomic, copy, readonly) NSString *ownerAppKey;

/*!
 * @abstract 群组人数上限，
 *
 * @discussion 表示当前群组人数上限，客户端不可更改。。
 */
@property(nonatomic, strong, readonly) NSString *maxMemberCount;

/*!
 * @abstract 该群是否已被设置为免打扰
 *
 * @discussion YES:是 , NO: 否
 */
@property(nonatomic, assign, readonly) BOOL isNoDisturb;

/*!
 * @abstract 该群是否已被设置为消息屏蔽
 *
 * @discussion YES:是 , NO: 否
 */
@property(nonatomic, assign, readonly) BOOL isShieldMessage;

///----------------------------------------------------
/// @name Group members maintenance 群组成员维护
///----------------------------------------------------
/*!
 * @abstract 获取群组成员列表（同步接口，建议使用异步接口）
 *
 * @return 成员列表. NSArray 里成员类型是 JMSGUser.
 *
 * @discussion 一般在群组详情界面调用此接口，展示群组的所有成员列表。
 * 本接口只是在本地请求成员列表，不会发起服务器端请求。
 */
- (NSArray JMSG_GENERIC(__kindof JMSGUser *)*)memberArray __attribute__((deprecated("Use - memberInfoList:")));

/*!
 * @abstract 获取群组成员列表（建议使用 [JMSGGroup memberInfoList:] 接口）
 *
 * @handler 成员列表. NSArray 里成员类型是 JMSGUser.
 *
 * @discussion 一般在群组详情界面调用此接口，展示群组的所有成员列表。
 * 本接口只是在本地请求成员列表，不会发起服务器端请求。
 */
- (void)memberArrayWithCompletionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 获取所有群成员信息列表
 *
 * @handler 成员列表. 类型为 NSArray，里面元素为 JMSGGroupMemberInfo.
 *
 * @discussion 返回数据中的 JMSGGroupMemberInfo 包含了成员 user 信息、入群时间、群昵称等
 */
- (void)memberInfoList:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 获取单个群成员信息
 *
 * @param  username 目标用户 username
 * @param  appkey   目标用户 appkey，不传则默认本应用 appkey
 * @return 群成员信息对象
 *
 * @discussion JMSGGroupMemberInfo 包含了成员 user 信息、入群时间、群昵称等
 */
- (JMSGGroupMemberInfo *JMSG_NULLABLE)memberInfoWithUsername:(NSString *JMSG_NONNULL)username
                                                      appkey:(NSString *JMSG_NULLABLE)appkey;

/*!
 * @abstract 修改群组类型
 *
 * @param type    群类型，公开群、私有群
 * @param handler 结果回调。error = nil 表示成功
 *
 * @discussion 对于已经创建的群组，可以通过此接口来修改群组的类型
 */
- (void)changeGroupType:(JMSGGroupType)type handler:(JMSGCompletionHandler)handler;

/*!
 * @abstract 设置群组消息免打扰（支持跨应用设置）
 *
 * @param isNoDisturb 是否免打扰 YES:是 NO: 否
 * @param handler 结果回调。回调参数：
 *
 * - resultObject 相应对象
 * - error 错误信息
 *
 * 如果 error 为 nil, 表示设置成功
 * 如果 error 不为 nil,表示设置失败
 *
 * @discussion 针对单个群组设置免打扰
 * 这个接口支持跨应用设置免打扰
 */
- (void)setIsNoDisturb:(BOOL)isNoDisturb handler:(JMSGCompletionHandler)handler;

/*!
 * @abstract 设置群组消息屏蔽
 *
 * @param isShield 是否群消息屏蔽 YES:是 NO: 否
 * @param handler 结果回调。回调参数： error 为 nil, 表示设置成功
 *
 * @discussion 针对单个群组设置群消息屏蔽
 */
- (void)setIsShield:(BOOL)isShield handler:(JMSGCompletionHandler)handler;

/*!
 * @abstract 设置成员群昵称
 *
 * @param nickname 群昵称
 * @param username 目标用户的 username
 * @param appKey   目标用户的 appKey,若传入空则默认使用本应用appKey
 */
- (void)setGroupNickname:(NSString *JMSG_NULLABLE)nickname
                username:(NSString *JMSG_NONNULL)username
                  appKey:(NSString *JMSG_NULLABLE)appKey
                 handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 获取成员的群昵称
 *
 * @param  username 群成员 username
 * @patam  appKey   群成员 appKey，不传则默认是本应用 appkey
 * @return 群昵称
 *
 * @discussion 还可以通过获取群成员信息 JMSGGroupMemberInfo 来获取群昵称
 */
- (NSString *JMSG_NULLABLE)groupNicknameWithUsername:(NSString *)username
                                              appKey:(NSString *JMSG_NULLABLE)appKey;

/*!
 * @abstract 群成员禁言设置(接口已过期)
 *
 * @param isSilence 是否禁言， YES:是 NO: 否
 * @param username  待设置的用户的 username
 * @param appKey    带待设置的用户的 appKey,若传入空则默认使用本应用appKey
 * @param handler   结果回调，error=nil,则表示成功
 *
 * @discussion 接口已过期，请使用 [JMSGGroup addGroupSilenceWithTime:usernames:appKey:handler] 接口。新老接口请不要混用。
 */
- (void)setGroupMemberSilence:(BOOL)isSilence
                     username:(NSString *JMSG_NONNULL)username
                       appKey:(NSString *JMSG_NULLABLE)appKey
                      handler:(JMSGCompletionHandler JMSG_NULLABLE)handler __attribute__((deprecated("Use - addGroupSilenceWithTime:")));
/*!
 * @abstract 设置群成员禁言（可设置禁言时间）
 *
 * @param silenceTime 禁言时间戳，单位：毫秒，必须不小于5分钟，不大于1年
 * @param usernames   用户的 username 数组，一次最多500人
 * @param appkey      用户的 appkey，若传入空则默认使用本应用appKey，同一次设置的 usernames 必须在同一个 AppKey 下
 * @param handler     结果回调，error = nil 时，表示成功
 *
 * @discussion 只有群主和管理员可设置；设置成功的话上层会收到相应下发事件。
 *
 * @since 3.8.1
 */
- (void)addGroupSilenceWithTime:(SInt64)silenceTime
                      usernames:(NSArray *JMSG_NONNULL)usernames
                         appKey:(NSString *JMSG_NULLABLE)appkey
                        handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 取消群成员禁言
 *
 * @param usernames  用户的 username 数组，一次最多500人
 * @param appkey     用户的 appkey，若传入空则默认使用本应用appKey，同一次设置的 usernames 必须在同一个 AppKey 下
 * @param handler   结果回调，error = nil 时，表示成功
 *
 * @discussion 只有群主和管理员可设置；取消成功的话上层会收到相应下发事件。
 *
 * @since 3.8.1
 */
- (void)deleteGroupSilenceWithUsernames:(NSArray *JMSG_NONNULL)usernames
                                appKey:(NSString *JMSG_NULLABLE)appkey
                               handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 判断用户在该群内是否被禁言（已过期，请使用 [JMSGGroup getGroupMemberSilenceWithUsername:] 方法）
 *
 * @param username  待判断用户的用户名
 * @param appKey    待判断用户的appKey，若传入空则默认使用本应用appKey
 */
- (BOOL)isSilenceMemberWithUsername:(NSString *JMSG_NONNULL)username
                             appKey:(NSString *JMSG_NULLABLE)appKey __attribute__((deprecated("已过期,请使用 - getGroupMemberSilenceWithUsername:appKey:")));
/*!
 * @abstract 获取禁言状态
 *
 * @param username 用户名
 * @param appKey   用户所在应用 AppKey，不填这默认本应用
 * @param handler  结果回调，resultObject 是 JMSGMemberSilenceInfo 类型；
 *                 若 error == nil && resultObject != nil,该成员已被禁言；
 *                 若 error == nil && resultObject == nil,该成员未被禁言；
 *                 若 error != nil ,请求失败。
 *
 * @discussion 返回的 JMSGMemberSilenceInfo 对象有 user 信息，通过 [JMSGGroup memberInfoWithUsername:appkey:] 可再次获取到 JMSGGroupMemberInfo 信息
 *
 * @since 3.8.1
 */
- (void)getGroupMemberSilenceWithUsername:(NSString *JMSG_NONNULL)username
                                   appKey:(NSString *JMSG_NULLABLE)appKey
                                  handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 禁言列表（已过期，请使用 [JMSGGroup getGroupSilenceList:] 方法）
 *
 * @return 禁言的成员列表. NSArray 里成员类型是 JMSGGroupMemberInfo
 *
 * @discussion 返回的是 JMSGUser 对象，无法直接查看禁言时间
 */
- (NSArray JMSG_GENERIC(__kindof JMSGUser *)*)groupSilenceMembers __attribute__((deprecated("已过期,请使用 - getGroupMemberSilenceList:")));

/*!
 * @abstract 禁言列表
 *
 * @param handler 结果回调，resultObject 是 NSArray 类型，元素是 JMSGMemberSilenceInfo
 *
 * @discussion 返回的 JMSGMemberSilenceInfo 对象有 user 信息，通过 [JMSGGroup memberInfoWithUsername:appkey:] 可再次获取到 JMSGGroupMemberInfo 信息
 *
 * @since 3.8.1
 */
- (void)getGroupMemberSilenceList:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 获取群公告列表
 *
 * @param handler 结果回调。resultObject 是 NSArray 类型，元素是 JMSGGroupAnnouncement
 *
 * @since 3.8.0
 */
- (void)groupAnnouncementList:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 发布群公告
 *
 * @param announcement 公告内容，大小必须在 1KB 以内
 * @param sendMessage 发布成功后是否需要发一条消息通知群成员，默认：YES
 * @param handler 结果回调。resultObject 为 JMSGGroupAnnouncement对象， error 为 nil 表示成功.
 *
 * @discussion
 * #### 注意：
 *
 * 如果 sendMessage = NO，则 SDK 不会自动发送消息，上层可以在回调或者收到事件后，自己发送消息；
 * 如果 sendMessage = YES，则在发布公告成功后 SDK 会自动在群里发布一条文本消息，文本内容就是公告内容，另外消息的 extras 里会附带公告的相关数据，上层可根据此数据将 message 对应到相应的公告， extras 里的 key-value 如下，
 *
 *    ```
 *    key(String)       = "jmessage_group_announcement"
 *    value(JsonString) = {
 *                        "id" : 公告 id,
 *                        "text" : 公告内容 text,
 *                        "publisher_uid" : 发布者 uid,
 *                        "ctime" : 公告发布时间,
 *                        "isTop" : 是否置顶,
 *                        "topTime" : 置顶时间,
 *                        "gid" : 群 gid
 *                      }
 *    ```
 * 群公告最多100条，发布公告后会有对应事件下发，上层通过 [JMSGGroupDelegate onReceiveGroupAnnouncementEvents:] 监听
 *
 * @since 3.8.0
 */
- (void)publishGroupAnnouncement:(NSString *JMSG_NONNULL)announcement
                     sendMessage:(BOOL)sendMessage
                         handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 删除群公告
 *
 * @param announcementID 公告id
 * @param handler 结果回调。error 为 nil 表示成功.
 *
 * @discussion 删除公告后会有对应事件下发，上层通过 [JMSGGroupDelegate onReceiveGroupAnnouncementEvents:] 监听
 * @since 3.8.0
 */
- (void)deleteGroupAnnouncement:(NSString *JMSG_NONNULL)announcementID
                        handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 置顶/取消置顶 群公告
 *
 * @param isTop 置顶参数，YES:置顶，NO:取消置顶
 * @param ID    公告 id
 * @param handler 结果回调。error 为 nil 表示成功.
 *
 * @discussion 置顶公告后会有对应事件下发，上层通过 [JMSGGroupDelegate onReceiveGroupAnnouncementEvents:] 监听
 * @since 3.8.0
 */
- (void)setGroupAnnouncementTop:(BOOL)isTop
                 announcementID:(NSString *JMSG_NONNULL)ID
                        handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 群黑名单列表
 *
 * @handler 结果回调. resultObject 是 NSArray 类型，元素是 JMSGUser
 *
 * @since 3.8.0
 */
- (void)groupBlacklistHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 添加群黑名单
 *
 * @param usernames 用户名列表
 * @param appKey   用户 appKey，usernames 中的所有用户必须在同一个 AppKey 下，不填则默认为本应用 appKey
 * @param handler 结果回调。error 为 nil 表示成功.
 *
 * @discussion 黑名单上限100个，超出将无法设置成功，被拉入黑名单用户会被主动踢出群组，且无法再次加入.
 * @since 3.8.0
 */
- (void)addGroupBlacklistWithUsernames:(NSArray <__kindof NSString *>*)usernames
                                appKey:(NSString *JMSG_NULLABLE)appKey
                               handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;
/*!
 * @abstract 删除群黑名单
 *
 * @param usernames 用户名列表
 * @param appKey   用户 appKey，usernames 中的所有用户必须在同一个 AppKey 下，不填则默认为本应用 appKey
 * @param handler 结果回调。error 为 nil 表示成功.
 *
 * @since 3.8.0
 */
- (void)deleteGroupBlacklistWithUsernames:(NSArray <__kindof NSString *>*)usernames
                                   appKey:(NSString *JMSG_NULLABLE)appKey
                                  handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 管理员列表
 *
 * @return 管理员列表. NSArray 里成员类型是 JMSGUser
 *
 * @discussion 注意：返回列表中不包含群主；仅在获取群成员成功后此接口才有效
 */
- (NSArray JMSG_GENERIC(__kindof JMSGUser *)*)groupAdminMembers;

/*!
 * @abstract 判断用户是否是管理员
 *
 * @param username  待判断用户的用户名
 * @param appKey    待判断用户的appKey，若传入空则默认使用本应用appKey
 */
- (BOOL)isAdminMemberWithUsername:(NSString *JMSG_NONNULL)username
                           appKey:(NSString *JMSG_NULLABLE)appKey;

/*!
 * @abstract 添加管理员
 *
 * @param username 用户名
 * @param appkey   用户 AppKey，不填则默认为本应用 AppKey
 * @param handler 结果回调。error 为 nil 表示成功.
 *
 * @discussion 注意：非 VIP 应用最多设置 15 个管理员，不包括群主本身
 */
- (void)addGroupAdminWithUsername:(NSString *JMSG_NONNULL)username
                           appKey:(NSString *JMSG_NULLABLE)appkey
                completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/** @abstract 批量添加管理员*/
- (void)addGroupAdminWithUsernames:(NSArray <__kindof NSString *>*)usernames
                            appKey:(NSString *JMSG_NULLABLE)appkey
                 completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 删除管理员
 *
 * @param username 用户名
 * @param appkey   用户 AppKey，不填则默认为本应用 AppKey
 * @param handler 结果回调。error 为 nil 表示成功.
 */
- (void)deleteGroupAdminWithUsername:(NSString *)username
                              appKey:(NSString *JMSG_NULLABLE)appkey
                   completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/** @abstract 批量删除管理员*/
- (void)deleteGroupAdminWithUsernames:(NSArray <__kindof NSString *>*)usernames
                               appKey:(NSString *JMSG_NULLABLE)appkey
                    completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 添加群组成员
 *
 * @param usernameArray 用户名数组。数组里的成员类型是 NSString
 * @param handler 结果回调。正常返回时 resultObject 为 nil.
 */
- (void)addMembersWithUsernameArray:(NSArray JMSG_GENERIC(__kindof NSString *) *)usernameArray
                  completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 添加群组跨应用成员
 *
 * @param usernameArray 用户名数组。数组里的成员类型是 NSString
 * @param userAppKey    用户的 AppKey，这批添加的成员必须在同一个 AppKey 下的用户
 *
 * @param handler 结果回调。正常返回时 resultObject 为 nil.
 */
- (void)addMembersWithUsernameArray:(NSArray JMSG_GENERIC(__kindof NSString *) *)usernameArray
                             appKey:(NSString *)userAppKey
                  completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 添加群组成员
 *
 * @param usernameArray 用户名数组。数组里的成员类型是 NSString
 * @param userAppKey    用户的 AppKey，这批添加的成员必须在同一个 AppKey 下的用户
 * @param reason        邀请原因，可选
 *
 * @param handler 结果回调。正常返回时 resultObject 为 nil.
 */
- (void)addMembersWithUsernameArray:(NSArray JMSG_GENERIC(__kindof NSString *) *)usernameArray
                             appKey:(NSString *JMSG_NULLABLE)userAppKey
                             reason:(NSString *JMSG_NULLABLE)reason
                  completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 删除群组成员
 *
 * @param usernameArray 用户名数据. 数组里的成员类型是 NSString
 * @param handler 结果回调。正常返回时 resultObject 为 nil.
 */
- (void)removeMembersWithUsernameArray:(NSArray JMSG_GENERIC(__kindof NSString *) *)usernameArray
                     completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 删除群组跨应用成员
 *
 * @param usernameArray 用户名数据. 数组里的成员类型是 NSString
 * @param handler 结果回调。正常返回时 resultObject 为 nil.
 */
- (void)removeMembersWithUsernameArray:(NSArray JMSG_GENERIC(__kindof NSString *) *)usernameArray
                                appKey:(NSString *)userAppKey
                     completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 移交群主
 *
 * @param username 新群主用户名
 * @param appkey   新群主用户 AppKey，不填则默认为本应用 AppKey
 * @param handler 结果回调。error 为 nil 表示成功.
 */
- (void)transferGroupOwnerWithUsername:(NSString *JMSG_NONNULL)username
                                appKey:(NSString *JMSG_NULLABLE)appkey
                     completionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 退出当前群组(当前用户)
 *
 * @param handler 结果回调。正常返回时 resultObject 为 nil。
 */
- (void)exit:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 获取头像缩略图文件数据
 *
 * @param handler 结果回调。回调参数:
 *
 * - data     头像数据;
 * - objectId 群组 gid;
 * - error    不为nil表示出错;
 *
 * 如果 error 为 nil, data 也为 nil, 表示没有头像数据.
 *
 * @discussion 需要展示缩略图时使用。
 * 如果本地已经有文件，则会返回本地，否则会从服务器上下载。
 */
- (void)thumbAvatarData:(JMSGAsyncDataHandler)handler;

/*!
 * @abstract 获取头像缩略文件的本地路径
 *
 * @return 返回本地路，返回值只有在下载完成之后才有意义
 */
- (NSString *JMSG_NULLABLE)thumbAvatarLocalPath;

/*!
 * @abstract 获取头像大图文件数据
 *
 * @param handler 结果回调。回调参数:
 *
 * - data     头像数据;
 * - objectId 群组 gid;
 * - error    不为nil表示出错;
 *
 * 如果 error 为 nil, data 也为 nil, 表示没有头像数据.
 *
 * @discussion 需要展示大图图时使用
 * 如果本地已经有文件，则会返回本地，否则会从服务器上下载。
 */
- (void)largeAvatarData:(JMSGAsyncDataHandler)handler;

/*!
 * @abstract 获取头像大图文件的本地路径
 *
 * @return 返回本地路，返回值只有在下载完成之后才有意义
 */
- (NSString *JMSG_NULLABLE)largeAvatarLocalPath;

/*!
 * @abstract 获取群成员的展示名
 *
 * @param memberUid 群成员的 uid（即：[JMSGUser uid]）
 *
 * @discussion 展示优先级：群昵称 > 好友备注(user.noteName) > 用户昵称(user.nickname) > 用户名(user.username)
 */
- (NSString *)memberDisplayName:(UInt64)memberUid;

/*!
 * @abstract 获取群组的展示名
 *
 * @discussion 如果 group.name 为空, 则此接口会拼接群组前 5 个成员的展示名返回.
 */
- (NSString *)displayName;

- (BOOL)isMyselfGroupMember;

- (BOOL)isEqualToGroup:(JMSGGroup * JMSG_NULLABLE)group;

JMSG_ASSUME_NONNULL_END

@end
