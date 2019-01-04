//
//  JMSGFriendManager.h
//  JMessage
//
//  Created by xudong.rao on 16/7/25.
//  Copyright © 2016年 HXHG. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <JMessage/JMSGConstants.h>

@interface JMSGFriendManager : NSObject

/*!
 * @abstract 获取好友列表
 *
 * @param handler 结果回调。回调参数：
 *
 * - resultObject 类型为 NSArray，数组里成员的类型为 JMSGUser
 * - error 错误信息
 *
 * 如果 error 为 nil, 表示设置成功
 * 如果 error 不为 nil,表示设置失败
 *
 * @discussion 从服务器获取，异步返回结果，返回用户的好友列表。
 * 建议开发者在 SDK 完全启动之后，再调用此接口获取数据
 */
+ (void)getFriendList:(JMSGCompletionHandler)handler;

/*!
 * @abstract 发送添加好友请求
 *
 * @param username 对方用户名
 * @param userAppKey 对方所在应用appkey,不传则默认是本应用
 * @param reason 添加好友时的备注，可不填
 *
 * @param handler 结果回调。回调参数
 *
 * - resultObject 相应的返回对象
 * - error 错误信息
 *
 * 如果 error 为 nil, 表示设置成功
 * 如果 error 不为 nil,表示设置失败
 *
 * @discussion 在对方未做回应的前提下，允许重复发送添加好友的请求。
 */
+ (void)sendInvitationRequestWithUsername:(NSString *)username
                                   appKey:(NSString *)userAppKey
                                   reason:(NSString *)reason
                        completionHandler:(JMSGCompletionHandler)handler;

/*!
 * @abstract 接受好友邀请
 *
 * @param username 对方用户名
 * @param userAppKey 对方所在应用appkey,不传则默认是本应用
 *
 * @param handler 结果回调。回调参数：
 *
 * - resultObject 相应的返回对象
 * - error 错误信息
 *
 * 如果 error 为 nil, 表示设置成功
 * 如果 error 不为 nil,表示设置失败
 *
 */
+ (void)acceptInvitationWithUsername:(NSString *)username
                              appKey:(NSString *)userAppKey
                   completionHandler:(JMSGCompletionHandler)handler;

/*!
 * @abstract 拒绝好友邀请
 *
 * @param username 对方用户名
 * @param userAppKey 对方所在应用appkey,不传则默认是本应用
 * @param reason 拒绝理由，可不传
 *
 * @param handler 结果回调。回调参数：
 *
 * - resultObject 相应的返回对象
 * - error 错误信息
 *
 * 如果 error 为 nil, 表示设置成功
 * 如果 error 不为 nil,表示设置失败
 *
 */
+ (void)rejectInvitationWithUsername:(NSString *)username
                              appKey:(NSString *)userAppKey
                              reason:(NSString *)reason
                   completionHandler:(JMSGCompletionHandler)handler;
/*!
 * @abstract 删除好友
 *
 * @param username 好友username
 * @param userAppKey 好友所在应用appkey,不传则默认是本应用
 *
 * @param handler 结果回调。回调参数：
 *
 * - resultObject 相应对象
 * - error 错误信息
 *
 * 如果 error 为 nil, 表示设置成功
 * 如果 error 不为 nil,表示设置失败
 *
 * @discussion
 */
+ (void)removeFriendWithUsername:(NSString *)username
                          appKey:(NSString *)userAppKey
               completionHandler:(JMSGCompletionHandler)handler;

@end
