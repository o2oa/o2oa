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

@class JMSGData;

/*!
 * 媒体内容类型的抽象父类
 *
 * 所有的媒体文件内容, 如 VoiceContent,ImageContent,
 * 都有媒体文件的处理逻辑, 比如上传与下载, 这些逻辑都放在这个类里统一处理.
 *
 * 这个类一般对外不可见.
 */
@interface JMSGMediaAbstractContent : JMSGAbstractContent <NSCopying>

JMSG_ASSUME_NONNULL_BEGIN

/*!
 * @abstract 媒体文件ID
 *
 * @discussion 这是 JMessage 内部用于表示资源文件的ID，使用该ID 可以定位到网络上的资源。
 *
 * 收到消息时，通过此ID 可以下载到资源；发出消息时，文件上传成功会生成此ID。
 *
 * 注意: 不支持外部设置媒体ID，也不支持把此字段设置为 URL 来下载到资源文件。
 */
@property(nonatomic, strong, readonly) NSString * JMSG_NULLABLE mediaID;

/*! @abstract 媒体格式*/
@property(nonatomic, strong, readonly) NSString * JMSG_NULLABLE format;

/*! @abstract 媒体文件大小 */
@property(nonatomic, strong, readonly) NSNumber *fSize;

/*!
 * @abstract 上传资源文件progress绑定(用来监听上传progress回调)
 * @discussion 如果需要监听这条消息的上传文件进度, 则需要赋值这个 block 为你你自己的实现
 */
@property(nonatomic, copy)JMSGMediaProgressHandler JMSG_NULLABLE uploadHandler;

/*!
 * @abstract 获取原文件的本地路径
 *
 * @discussion 此属性是通过懒加载的方式获取，必须在下载完成之后此属性值才有意义
 */
@property(nonatomic, strong, readonly) NSString * JMSG_NULLABLE originMediaLocalPath;

// 不支持使用的初始化方法
- (nullable instancetype)init NS_UNAVAILABLE;

JMSG_ASSUME_NONNULL_END

@end

