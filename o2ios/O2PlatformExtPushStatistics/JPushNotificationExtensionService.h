/*
 *	| |    | |  \ \  / /  | |    | |   / _______|
 *	| |____| |   \ \/ /   | |____| |  / /
 *	| |____| |    \  /    | |____| |  | |   _____
 * 	| |    | |    /  \    | |    | |  | |  |____ |
 *  | |    | |   / /\ \   | |    | |  \ \______| |
 *  | |    | |  /_/  \_\  | |    | |   \_________|
 *
 * Copyright (c) 2017 Shenzhen HXHG. All rights reserved.
 */

#import <Foundation/Foundation.h>

#define JPUSH_EXTENSION_VERSION_NUMBER 1.1.1

@class UNNotificationRequest;

@interface JPushNotificationExtensionService : NSObject


/**
 设置appkey（需要与main target中的appkey相同）
 */
+ (void)jpushSetAppkey:(NSString *)appkey;

/**
 apns送达
 @param request apns请求
 @param completion 回调
 */
+ (void)jpushReceiveNotificationRequest:(UNNotificationRequest *)request with:(void (^)(void))completion;

/**
 关闭日志
 默认为开启
 建议发布时关闭以减少不必要的IO
 */
+ (void)setLogOff;


@end
