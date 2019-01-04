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
#import <JMessage/JMSGConstants.h>


/*!
 * 图片内容
 *
 * 图片区分缩略图与大图.
 *
 * 大图指的创建对象时图片. 这个大图一般来说, 也是 App 对拍照或者相册里的 "原图" 经过压缩处理的.
 * 因为消息应用一般不会发送相册或者拍照的 "原图", 这太大了.
 *
 * 暂不支持可选 "原图" 发送功能. 有计划支持.
 *
 * 缩略图则是 SDK 根据一定的算法 (主要基于屏幕, 长宽比等), 裁减出来的一个相对小的图片,
 * 用于默认显示在聊天窗口.
 * 第一次访问缩略图后, SDK 会缓存把该数据缓存在该对象上, 所以访问多次访问会非常快,
 * 有利于聊天窗口滚动的性能表现.
 *
 * 收到消息时, SDK 会默认预下载缩略图, 这样查看消息就可以即时展示出来.
 * 而大图, 则一般是 App 预览图片调用接口时, SDK 再去发起下载.
 *
 * 针对每种资源(缩略图或者大图), 只提供一个异步接口获取数据. SDK 去做缓存,文件,下载的适配工作.
 */
@interface JMSGImageContent : JMSGMediaAbstractContent <NSCopying>

/*!
 * @abstract 图片链接
 */
@property(nonatomic, strong, readonly) NSString * JMSG_NULLABLE imageLink;

/*! 
 * @abstract 图片格式
 *
 * 注意：格式后缀不需要带点，只需后缀名，如：png、jpg 等
 */
@property(nonatomic, strong) NSString * JMSG_NULLABLE format;

/*!
 * @abstract 图片原始大小
 */
@property(nonatomic, assign, readonly) CGSize imageSize;

/*!
 * @abstract 获取缩略图的本地路径
 *
 * @discussion 此属性是通过懒加载的方式获取，必须在下载完成之后此属性值才有意义
 */
@property(nonatomic, strong, readonly) NSString * JMSG_NULLABLE thumbImageLocalPath;

// 不支持使用的初始化方法
- (nullable instancetype)init NS_UNAVAILABLE;

/*!
 * @abstract 初始化消息图片内容
 *
 * @param data 图片数据
 *
 * @discussion 这是预设的初始化方法. 创建一个图片内容对象, 必须要传入图片数据.
 *
 * 对于图片消息, 一般来说创建此图片内容的数据, 是对拍照原图经过裁减处理的, 否则发图片消息太大.
 * 这里传入的图片数据, SDK视为大图. 方法 largeImageDataWithProgress:completionHandler 下载到的,
 * 就是这个概念上的图片数据.
 */
- (nullable instancetype)initWithImageData:(NSData * JMSG_NONNULL)data;

/*!
 * @abstract 获取图片消息的缩略图数据
 *
 * @param handler 结果回调。回调参数:
 *
 * - data 图片数据;
 * - objectId 消息msgId;
 * - error 不为nil表示出错;
 *
 * 如果 error 为 ni, data 也为 nil, 表示没有数据.
 *
 * @discussion 展示缩略时调用此接口，获取缩略图数据。
 * 如果本地数据文件已经存在, 则直接返回;
 * 如果本地还没有图片，会发起网络请求下载。下载完后再回调。
 */
- (void)thumbImageData:(JMSGAsyncDataHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 获取图片消息的大图数据
 *
 * @param progressHandler 下载进度。会持续回调更新进度, 直接下载完成。如果为 nil 则表示不关心进度。
 * @param handler 结果回调。回调参数:
 *
 * - data 图片数据;
 * - objectId 消息msgId;
 * - error 不为nil表示出错;
 *
 * 如果 error 为 ni, data 也为 nil, 表示没有数据.
 *
 * @discussion 一般在预览图片大图时，要用此接口。
 * 如果本地数据文件已经存在, 则直接返回;
 * 如果本地还没有图片，会发起网络请求下载。下载完后再回调。
 */
- (void)largeImageDataWithProgress:(JMSGMediaProgressHandler JMSG_NULLABLE)progressHandler
                 completionHandler:(JMSGAsyncDataHandler JMSG_NULLABLE)handler;

@end
