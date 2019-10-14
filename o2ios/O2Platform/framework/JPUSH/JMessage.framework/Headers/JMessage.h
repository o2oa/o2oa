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
#import <JMessage/JMSGUser.h>
#import <JMessage/JMSGGroup.h>
#import <JMessage/JMSGMessage.h>
#import <JMessage/JMSGConversation.h>
#import <JMessage/JMSGAbstractContent.h>
#import <JMessage/JMSGMediaAbstractContent.h>
#import <JMessage/JMSGCustomContent.h>
#import <JMessage/JMSGEventContent.h>
#import <JMessage/JMSGImageContent.h>
#import <JMessage/JMSGTextContent.h>
#import <JMessage/JMSGVoiceContent.h>
#import <JMessage/JMessageDelegate.h>
#import <JMessage/JMSGFileContent.h>
#import <JMessage/JMSGFriendManager.h>
#import <JMessage/JMSGNotificationEvent.h>
#import <JMessage/JMSGFriendNotificationEvent.h>
#import <JMessage/JMSGLocationContent.h>
#import <JMessage/JMSGPromptContent.h>
#import <JMessage/JMSGOptionalContent.h>
#import <JMessage/JMSGChatRoom.h>
#import <JMessage/JMSGVideoContent.h>
#import <JMessage/JMSGConstants.h>

@protocol JMSGMessageDelegate;
@protocol JMessageDelegate;
@class JMSGConversation;

extern NSString *const kJMSGNetworkIsConnectingNotification;          // 正在连接中
extern NSString *const kJMSGNetworkDidSetupNotification;              // 建立连接
extern NSString *const kJMSGNetworkDidCloseNotification;              // 关闭连接
extern NSString *const kJMSGNetworkDidRegisterNotification;           // 注册成功
extern NSString *const kJMSGNetworkFailedRegisterNotification;        // 注册失败
extern NSString *const kJMSGNetworkDidLoginNotification;              // 连接成功
extern NSString *const kJMSGNetworkDidReceiveMessageNotification;     // 收到消息
extern NSString *const kJMSGServiceErrorNotification;                 // 错误提示

/*!
 * JMessage核心头文件
 *
 * 这是唯一需要导入到你的项目里的头文件，它引用了内部需要用到的头文件。
 */
@interface JMessage : NSObject

/*! JMessage SDK 版本号。用于展示 SDK 的版本信息 */
#define JMESSAGE_VERSION @"3.9.1"

/*! JMessage SDK 构建ID. 每次构建都会增加 */
#define JMESSAGE_BUILD 241

/*! API Version - int for program logic. SDK API 有变更时会增加 */
extern NSInteger const JMESSAGE_API_VERSION;


/*!
 * @abstract 初始化 JMessage SDK
 *
 * @discussion 此方法被[JMessage setupJMessage:appKey:channel:apsForProduction:category:messageRoaming:]方法取代
 */
+ (void)setupJMessage:(NSDictionary *)launchOptions
               appKey:(NSString *)appKey
              channel:(NSString *)channel
     apsForProduction:(BOOL)isProduction
             category:(NSSet *)category __attribute__((deprecated("JMessage 3.1.0 版本已过期")));

/*!
 * @abstract 初始化 JMessage SDK
 *
 * @param launchOptions    AppDelegate启动函数的参数launchingOption(用于推送服务)
 * @param appKey           appKey(应用Key值,通过JPush官网可以获取)
 * @param channel          应用的渠道名称
 * @param isProduction     是否为生产模式
 * @param category         iOS8新增通知快捷按钮参数
 * @param isRoaming        是否启用消息漫游,默认关闭
 *
 * @discussion 此方法必须被调用, 以初始化 JMessage SDK
 *
 * 如果未调用此方法, 本 SDK 的所有功能将不可用.
 */
+ (void)setupJMessage:(NSDictionary *)launchOptions
               appKey:(NSString *)appKey
              channel:(NSString *)channel
     apsForProduction:(BOOL)isProduction
             category:(NSSet *)category
       messageRoaming:(BOOL)isRoaming;

/*!
 * @abstract 增加回调(delegate protocol)监听
 *
 * @param delegate 需要监听的 Delegate Protocol
 * @param conversation 允许为nil
 *
 * - 为 nil, 表示接收所有的通知, 不区分会话.
 * - 不为 nil，表示只接收指定的 conversation 相关的通知.
 *
 * @discussion 默认监听全局 JMessageDelegate 即可.
 *
 * 这个调用可以在任何地方, 任何时候调用, 可以在未进行 SDK
 * 启动 setupJMessage:appKey:channel:apsForProduction:category: 时就被调用.
 *
 * 并且, 如果你有必要接收数据库升级通知 JMSGDBMigrateDelegate,
 * 就应该在 SDK 启动前就调用此方法, 来注册通知接收.
 * 这样, SDK启动过程中发现需要进行数据库升级, 给 App 发送数据库升级通知时,
 * App 才可以收到并进行处理.
 */
+ (void)addDelegate:(id <JMessageDelegate>)delegate withConversation:(JMSGConversation *)conversation;

/*!
 * @abstract 删除Delegate监听
 *
 * @param delegate 监听的 Delegate Protocol
 * @param conversation 基于某个会话的监听. 允许为 nil.
 *
 * - 为 nil, 表示全局的监听, 即所有会话相关.
 * - 不为 nil, 表示特定的会话.
 */
+ (void)removeDelegate:(id <JMessageDelegate>)delegate withConversation:(JMSGConversation *)conversation;

/*!
 * @abstract 删除全部监听
 */
+ (void)removeAllDelegates;

/*!
 * @abstract 打开日志级别到 Debug
 *
 * @discussion JMessage iOS  SDK 默认开启的日志级别为: Info. 只显示必要的信息, 不打印调试日志.
 *
 * 调用本接口可打开日志级别为: Debug, 打印调试日志，初始化 SDK 前调用.
 */
+ (void)setDebugMode;

/*!
 * @abstract 关闭日志
 *
 * @discussion 关于日志级别的说明, 参考 [JMessage setDebugMode]
 *
 * 虽说是关闭日志, 但还是会打印 Warning, Error 日志. 这二种日志级别, 在程序运行正常时, 不应有打印输出.
 *
 * 建议在发布的版本里, 调用此接口, 关闭掉日志打印.
 */
+ (void)setLogOFF;

/*!
 * @abstract 开启崩溃上报
 *
 * @discussion 默认不上报
 */
+ (void)setCrashLogON;

/*!
 * @abstract 注册远程推送
 * @param types 通知类型
 * @param categories 类别组
 *
 */
+ (void)registerForRemoteNotificationTypes:(NSUInteger)types categories:(NSSet *)categories;

/*!
 * @abstract 注册 DeviceToken
 * @param deviceToken 从注册推送回调中拿到的 DeviceToken
 */
+ (void)registerDeviceToken:(NSData *)deviceToken;

/*!
 *  @abstract 验证此 appKey 是否为当前应用 appKey
 *
 *  @param appKey 应用 AppKey
 *
 *  @return 是否为当前应用 appKey
 */
+ (BOOL)isMainAppKey:(NSString *)appKey;

/*!
 * @abstract 设置角标(到服务器)
 *
 * @param value 新的值. 会覆盖服务器上保存的值(这个用户)
 *
 * @discussion 本接口不会改变应用本地的角标值.
 * 本地仍须调用 UIApplication:setApplicationIconBadgeNumber 函数来设置脚标.
 *
 * 该功能解决的问题是, 服务器端推送 APNs 时, 并不知道客户端原来已经存在的角标是多少, 指定一个固定的数字不太合理.
 *
 * APNS 服务器端脚标功能提供:
 *
 * - 通过本 API 把当前客户端(当前这个用户的) 的实际 badge 设置到服务器端保存起来;
 * - 调用服务器端 API 发 APNs 时(通常这个调用是批量针对大量用户),
 *   使用 "+1" 的语义, 来表达需要基于目标用户实际的 badge 值(保存的) +1 来下发通知时带上新的 badge 值;
 */
+ (BOOL)setBadge:(NSInteger)value;

/*!
 * @abstract 重置角标(为0)
 *
 * @discussion 相当于 [setBadge:0] 的效果.
 * 参考 [JMessage setBadge:] 说明来理解其作用.
 */
+ (void)resetBadge;

/*!
 * @abstract 发送透传消息给自己在线的其他设备
 *
 * @param message   发送的内容
 * @param platform  设备类型
 * @param handler   回调
 *
 * @discussion 注意：
 *
 *  1. 消息透传功能，消息不会进入到后台的离线存储中去，仅当对方用户当前在线时才会成功送达，SDK 不会将此类消息内容存储；
 *
 *  2. 透传命令到达是，接收方通过 [JMSGEventDelegate onReceiveMessageTransparentEvent:] 方法监听。
 *
 * @since 3.5.0
 */
+ (void)sendCrossDeviceTransMessage:(NSString *)message
                           platform:(JMSGPlatformType)platform
                            handler:(JMSGCompletionHandler)handler;

/*!
 * @abstract 判断是否设置全局免打扰
 *
 * @return YES/NO
 */
+ (BOOL)isSetGlobalNoDisturb;

/*!
 * @abstract 设置是否全局免打扰
 *
 * @param isNoDisturb 是否全局免打扰 YES:是 NO: 否
 * @param handler 结果回调。回调参数：error 不为 nil,表示设置失败
 *
 * @discussion 此函数为设置全局的消息免打扰，建议开发者在 SDK 完全启动之后，再调用此接口获取数据
 */
+ (void)setIsGlobalNoDisturb:(BOOL)isNoDisturb handler:(JMSGCompletionHandler)handler;

/*!
 * @abstract 用户免打扰列表
 *
 * @param handler 结果回调。回调参数：
 *
 * - resultObject 类型为 NSArray，数组里成员的类型为 JMSGUser、JMSGGroup
 * - error 错误信息
 *
 * 如果 error 为 nil, 表示设置成功
 * 如果 error 不为 nil,表示设置失败
 *
 * @discussion 从服务器获取，返回用户的免打扰列表。
 * 建议开发者在 SDK 完全启动之后，再调用此接口获取数据
 */
+ (void)noDisturbList:(JMSGCompletionHandler)handler;

/*!
 * @abstract 黑名单列表
 *
 * @param handler 结果回调。回调参数：
 *
 * - resultObject 类型为 NSArray，数组里成员的类型为 JMSGUser
 * - error 错误信息
 *
 * 如果 error 为 nil, 表示设置成功
 * 如果 error 不为 nil,表示设置失败
 *
 * @discussion 从服务器获取，返回用户的黑名单列表。
 * 建议开发者在 SDK 完全启动之后，再调用此接口获取数据
 */
+ (void)blackList:(JMSGCompletionHandler)handler;

/*!
 * @abstract 获取当前服务器端时间
 *
 * @discussion 可用于纠正本地时间。
 */
+ (NSTimeInterval)currentServerTime;

/*!
 * @abstract 发起数据库升级测试
 *
 * @discussion 这是一个专用于测试时使用到的接口.
 *
 * 关于数据库升级相关, 参考这个 [JMSGDBMigrateDelegate] 类里的说明.
 *
 * 调用此接口后, App 会收到一个升级开始通知, 30s 后再收到一个升级结束通知.
 *
 * 本接口内部并不会真实地发起数据库升级操作, 而仅用于发出开始与完成的通知, 以方便 App 来测试处理流程.
 */
+ (void)testDBMigrating;
@end


/*!
 * 用户登录设备信息
 */
@interface JMSGDeviceInfo: NSObject

/// 设备所属平台，Android、iOS、Windows、web
@property(nonatomic, assign, readonly) JMSGPlatformType platformType;
/// 是否登录，YES:已登录，NO:未登录
@property(nonatomic, assign, readonly) BOOL isLogin;
/// 是否在线，0:不在线，1:在线
@property(nonatomic, assign, readonly) UInt32 online;
/// 上次登录时间
@property(nonatomic, strong, readonly) NSNumber *mtime;
/// 默认为0，1表示该设备被当前登录设备踢出
@property(nonatomic, assign, readonly) NSInteger flag;

@end
