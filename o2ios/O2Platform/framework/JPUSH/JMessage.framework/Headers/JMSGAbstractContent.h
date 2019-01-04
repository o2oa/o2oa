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
#import <JMessage/JMSGConversation.h>


/*!
 * 消息内容（抽象类）
 *
 * 所有消息内容的实体类，都直接或者间接继承这个类。很多 API 上使用这个抽象类作为类型。
 *
 * 有时候通过 API 得到的是这个抽象类型，需要基于消息的 contentType 属性来转型为相应的具体子类，
 * 做进一步的动作。
 *
 *    ```
 *    // 转移到子类举例
 *    JMSGAbstractContent *content = oneMessage.content;
 *    if (oneMessage.contentType == kJMSGContentTypeText) {
 *        JMSGTextContent *textContent = (JMSGTextContent *)content;
 *        String text = textContent.text;
 *    }
 *    ```
 */
@interface JMSGAbstractContent : NSObject <NSCopying> {}

JMSG_ASSUME_NONNULL_BEGIN

/*!
 * @abstract 附加参数
 *
 * @discussion 对某个类型的消息, 比如 VoiceContent, 可以附加参数以便用于业务逻辑
 */
@property(nonatomic, strong, readonly) NSDictionary * JMSG_NULLABLE extras;

// 无效的初始化方法. 应使用各具体子类内容类型的特别的方法
- (nullable instancetype)init NS_UNAVAILABLE;

/*!
 * @abstract 增加一个字符串值类型的字段
 *
 * @param value 新增键值对的值. String 类型.
 * @param key 新增键值对的键
 */
- (BOOL)addStringExtra:(NSString *)value forKey:(NSString *)key;

/*!
 * @abstract 增加一个数字值类型的字段
 *
 * @param value 新增键值对的值. Number 类型.
 * @param key 新增键值对的键
 */
- (BOOL)addNumberExtra:(NSNumber *)value forKey:(NSString *)key;

/*!
 * @abstract 调用此方法得到 JSON 格式描述的 Message Content
 */
- (NSString *)toJsonString;

/*!
 * @abstract 判断消息内容是否相等
 *
 * @param content 比较的内容对象
 *
 * @discussion 对于媒体类的内容, 即使同样的内容, 每次也视为新的资源, 会生成不同的资源ID,
 * 从而最终 content 不相等.
 *
 * 所有的子类都提供本方法.
 */
- (BOOL)isEqualToContent:(JMSGAbstractContent * JMSG_NULLABLE)content;

JMSG_ASSUME_NONNULL_END

@end
