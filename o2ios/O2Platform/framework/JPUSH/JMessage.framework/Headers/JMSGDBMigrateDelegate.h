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
 * 数据库升级通知
 *
 * 当一个新版本的 SDK 第一次启动时, 如果发现新版本所使用的 DB 需要升级, 则会暂停启动过程, 进行升级操作.
 * 数据库升级操作可能需要花费较长的时间.
 *
 * 在数据库升级操作进行期间, SDK 未启动正常工作, 所有 API 禁止访问. 简单地说, SDK 在不可用状态.
 *
 * 为了让 App 能够处理 SDK 的这个状态, 数据库升级期间, SDK 会发出升级开始, 与升级完成的通知.
 *
 * 建议 App 应处理这个数据库升级的通知, 以让 App 交互看起来是可以接受的.
 *
 * 典型的作法是: 收到升级开始通知时, App 弹出全屏进度状态, 提示用户正在升级数据. 在这个状态下用户不可以操作 App.
 * 收到升级完成通知时, 结束升级提示, 并且继续执行 SDK 准备好后需要做的事情.
 */
@protocol JMSGDBMigrateDelegate <NSObject>

/*!
 * @abstract 数据库升级开始
 */
@optional
- (void)onDBMigrateStart;

/*!
 * @abstract 数据库升级完成
 *
 * @param error 如果升级失败, 则 error 不为 nil. 反之 error 为 nil 时升级成功.
 *
 * @discussion SDK会有自动重试, 竭力避免失败. 如果实在返回失败, 建议提示用户重新安装 App.
 */
@optional
- (void)onDBMigrateFinishedWithError:(NSError *)error;

@end
