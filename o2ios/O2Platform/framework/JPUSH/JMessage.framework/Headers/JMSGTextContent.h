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
 * 纯文本内容类型
 */
@interface JMSGTextContent : JMSGAbstractContent <NSCopying>

JMSG_ASSUME_NONNULL_BEGIN

/*!
 * @abstract 内容文本
 */
@property(nonatomic, readonly, copy) NSString *text;

// 不支持使用的初始化方法
- (instancetype)init NS_UNAVAILABLE;

/*!
 * @abstract 基于文本初始化内容对象
 *
 * @param text 纯文本内容
 *
 * @discussion 这是预设的创建文本类型内容的方法
 */
- (instancetype)initWithText:(NSString *)text;

JMSG_ASSUME_NONNULL_END

@end
