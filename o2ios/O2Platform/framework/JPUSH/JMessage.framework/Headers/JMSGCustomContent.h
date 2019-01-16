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
#import <JMessage/JMSGAbstractContent.h>

/*!
 * 定制内容类型
 *
 * 用于自定义消息的内容类型.
 *
 * 建议只在基本类型 (TextContent, VoiceContent, ImageContent) 不满足使用的情况才使用此类型.
 *
 * 所有的内容类型都带有 extras 可附加字段信息, 从而都具备一定的定制能力.
 * 比如如果需要基于图片做简单定制, 建议基于 ImageContent 再附加 extra 的方式.
 *
 */
@interface JMSGCustomContent : JMSGAbstractContent <NSCopying>

JMSG_ASSUME_NONNULL_BEGIN

@property(nonatomic, strong, readonly) NSDictionary * JMSG_NULLABLE customDictionary;

// 不支持使用的初始化方法
- (instancetype)init NS_UNAVAILABLE;

/*!
 * @abstract 预期使用的初始化方法
 *
 * @param customDict 初始化时指定的字典
 */
- (instancetype)initWithCustomDictionary:(NSDictionary * JMSG_NULLABLE)customDict;

/*!
 * @abstract 添加一个键值对.
 *
 * @param value 值. 必须满足 JSON Value 的要求, 基本规则是: NSNumber, NSString, NSArray, NSDictionary
 * @param key 键
 *
 * @return 如果无效的 value, 返回 false, 添加失败
 *
 * @discussion value的有效性校验, 参考 Apple 官方文档: https://developer.apple.com/library//ios/documentation/Foundation/Reference/NSJSONSerialization_Class/index.html#//apple_ref/occ/clm/NSJSONSerialization/isValidJSONObject:
 */
- (BOOL)addObjectValue:(NSObject *)value forKey:(NSString *)key;

/*!
 * @abstract 快捷添加 String 类型 value 的方法
 *
 * @param value 键值对里的值. String 类型.
 * @param key 键值对里的键
 *
 * @return 如果无效的 value, 返回 false, 添加失败
 */
- (BOOL)addStringValue:(NSString *)value forKey:(NSString *)key;

/*!
 * @abstract 快捷添加 Number 类型 value 的方法
 *
 * @param value 键值对里的值. Number 类型.
 * @param key 键值对里的键
 *
 * @return 如果无效的 value, 返回 false, 添加失败
 */
- (BOOL)addNumberValue:(NSNumber *)value forKey:(NSString *)key;

/*!
 * @abstract 设置该自定义消息内容的文本描述
 *
 * @param contentText 内容文本描述
 *
 * @discussion 用于展示在会话列表, 文本地简要描述这条消息.
 * 如果未设置, 则默认值为 "[自定义消息]"
 */
- (void)setContentText:(NSString *)contentText;

JMSG_ASSUME_NONNULL_END

@end
