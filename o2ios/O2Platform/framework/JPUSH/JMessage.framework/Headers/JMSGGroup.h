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
/** 群组人数上限*/
@property(nonatomic, strong, readonly) NSString *JMSG_NONNULL maxMemberCount;

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
 * @discussion 该接口总是向服务器端发起请求, 即使本地已经存在.
 * 如果考虑性能损耗, 在群聊时获取群组信息, 可以获取 JMSGConversation -> target 属性.
 */
+ (void)groupInfoWithGroupId:(NSString *)groupId
           completionHandler:(JMSGCompletionHandler)handler;

/*!
 * @abstract 获取我的群组列表
 *
 * @param handler 结果回调。正常返回时 resultObject 的类型是 NSArray，数组里的成员类型是JMSGGroup的gid
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
- (NSArray JMSG_GENERIC(__kindof JMSGUser *)*)memberArray;

/*!
 * @abstract 获取群组成员列表（异步接口）
 *
 * @handler 成员列表. NSArray 里成员类型是 JMSGUser.
 *
 * @discussion 一般在群组详情界面调用此接口，展示群组的所有成员列表。
 * 本接口只是在本地请求成员列表，不会发起服务器端请求。
 */
- (void)memberArrayWithCompletionHandler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

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
 * @abstract 群成员禁言设置
 *
 * @param isSilence 是否禁言， YES:是 NO: 否
 * @param username  带设置的用户的 username
 * @param username  带设置的用户的 appKey,若传入空则默认使用本应用appKey
 * @param handler   结果回调
 *
 * @discussion 注意: 目前 SDK 只支持群主设置群里某个用户禁言
 */
- (void)setGroupMemberSilence:(BOOL)isSilence
                     username:(NSString *JMSG_NONNULL)username
                       appKey:(NSString *JMSG_NULLABLE)appKey
                      handler:(JMSGCompletionHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 判断用户在该群内是否被禁言
 *
 * @param username  待判断用户的用户名
 * @param appKey    待判断用户的appKey，若传入空则默认使用本应用appKey
 */
- (BOOL)isSilenceMemberWithUsername:(NSString *JMSG_NONNULL)username
                             appKey:(NSString *JMSG_NULLABLE)appKey;

/*!
 * @abstract 禁言列表
 *
 * @return 禁言的成员列表. NSArray 里成员类型是 JMSGUser
 *
 * @discussion 仅在获取群成员成功后此接口才有效
 */
- (NSArray JMSG_GENERIC(__kindof JMSGUser *)*)groupSilenceMembers;

/*!
 * @abstract 判断用户是否是管理员
 *
 * @param username  待判断用户的用户名
 * @param appKey    待判断用户的appKey，若传入空则默认使用本应用appKey
 */
- (BOOL)isAdminMemberWithUsername:(NSString *JMSG_NONNULL)username
                           appKey:(NSString *JMSG_NULLABLE)appKey;

/*!
 * @abstract 管理员列表
 *
 * @return 管理员列表. NSArray 里成员类型是 JMSGUser
 *
 * @discussion 注意：返回列表中包含群主；仅在获取群成员成功后此接口才有效
 */
- (NSArray JMSG_GENERIC(__kindof JMSGUser *)*)groupAdminMembers;

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
 * @param handler 结果回调。正常返回时 resultObject 为 nil.
 */
- (void)addMembersWithUsernameArray:(NSArray JMSG_GENERIC(__kindof NSString *) *)usernameArray
                             appKey:(NSString *)userAppKey
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
 * @abstract 获取群组的展示名
 *
 * @discussion 如果 group.name 为空, 则此接口会拼接群组前 5 个成员的展示名返回.
 */
- (NSString *)displayName;

- (BOOL)isMyselfGroupMember;

- (BOOL)isEqualToGroup:(JMSGGroup * JMSG_NULLABLE)group;

JMSG_ASSUME_NONNULL_END

@end
