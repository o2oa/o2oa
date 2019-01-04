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
#import <JMessage/JMSGMediaAbstractContent.h>

/*!
 * 语音内容类型
 */
@interface JMSGVoiceContent : JMSGMediaAbstractContent <NSCopying>

JMSG_ASSUME_NONNULL_BEGIN

/*!
 * @abstract 语音时长 (单位:秒)
 */
@property(nonatomic, copy, readonly) NSNumber *duration;


// 不支持使用的初始化方法
- (instancetype)init NS_UNAVAILABLE;

/*!
 * @abstract 初始化语音内容
 *
 * @param data 该语音内容的数据. 不允许为 nil, 并且内容长度应大于 0, 否则失败
 * @param duration 该语音内容的持续时长. 单位是秒. 不允许为 nil, 并且应大于 0.
 *
 * @discussion 这是预设的初始化方法, 创建一条语音内容, 必然传入语音数据, 以及时长.
 */
- (instancetype)initWithVoiceData:(NSData *)data
                    voiceDuration:(NSNumber *)duration;

/*!
 * @abstract 获取语音内容的数据
 *
 * @param handler 结果回调。回调参数:
 *
 * - data 语音数据;
 * - objectId 消息msgId;
 * - error 不为nil表示出错;
 *
 * 如果 error 为 ni, data 也为 nil, 表示没有数据.
 *
 * @discussion 如果本地数据文件存在, 则直接返回.
 * 如果本地还没有语音数据，会发起网络请求下载。下载完后再回调。
 */
- (void)voiceData:(JMSGAsyncDataHandler)handler;


JMSG_ASSUME_NONNULL_END

@end
