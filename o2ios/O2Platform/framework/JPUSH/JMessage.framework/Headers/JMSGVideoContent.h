/*
 *  | |    | |  \ \  / /  | |    | |   / _______|
 *  | |____| |   \ \/ /   | |____| |  / /
 *  | |____| |    \  /    | |____| |  | |   _____
 *  | |    | |    /  \    | |    | |  | |  |____ |
 *  | |    | |   / /\ \   | |    | |  \ \______| |
 *  | |    | |  /_/  \_\  | |    | |   \_________|
 *
 * Copyright (c) 2011 ~ 2015 Shenzhen HXHG. All rights reserved.
 */

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <JMessage/JMSGMediaAbstractContent.h>

/*!
 * 视频内容类型
 */
@interface JMSGVideoContent : JMSGMediaAbstractContent<NSCopying>

JMSG_ASSUME_NONNULL_BEGIN
// 不支持使用的初始化方法
- (instancetype)init NS_UNAVAILABLE;

/*!
 * @abstract 视频时长 (单位:秒)
 */
@property(nonatomic, copy, readonly) NSNumber *duration;

/*!
 * @abstract 视频封面图片大小
 */
@property(nonatomic, assign, readonly) CGSize videoThumbImageSize;

/*!
 * @abstract 获取视频封面图片的本地路径
 *
 * @discussion 此属性是通过懒加载的方式获取，必须在下载完成之后此属性值才有意义
 */
@property(nonatomic, strong, readonly) NSString *JMSG_NULLABLE videoThumbImageLocalPath;

/*!
 * @abstract 初始化视频消息内容
 *
 * @param data      该视频内容的数据
 * @param thumbData 缩略图，建议：缩略图上层要控制大小，避免上传过大图片
 * @param duration  该视频内容的持续时长，长度应大于 0
 *
 * @discussion 建议：缩略图上层要控制大小，避免上传过大图片.
 */
- (instancetype)initWithVideoData:(NSData *)data
                        thumbData:(NSData *JMSG_NULLABLE)thumbData
                         duration:(NSNumber *)duration;
/*!
 * @abstract 视频格式
 *
 * @discussion 创建 videoContent 时可设置，建议设置 format。后缀不需要带点，只需后缀名，如： mp4、mov 等
 */
@property(nonatomic, strong) NSString * JMSG_NULLABLE format;

/*!
 * @abstract 视频文件名
 *
 * @discussion 创建 videoContent 时可设置
 */
@property(nonatomic, strong) NSString * JMSG_NULLABLE fileName;

/*!
 * @abstract 获取视频消息的封面缩略图
 *
 * @param handler 结果回调。回调参数:
 *
 * - data 图片数据;
 * - objectId 消息msgId;
 * - error 不为nil表示出错;
 *
 * 如果 error 为 ni, data 也为 nil, 表示没有数据.
 *
 * @discussion 展示缩略时调用此接口，获取缩略图数据。如果本地数据文件已经存在, 则直接返回; 如果本地还没有图片，会发起网络请求下载。下载完后再回调。
 */
- (void)videoThumbImageData:(JMSGAsyncDataHandler JMSG_NULLABLE)handler;

/*!
 * @abstract 获取视频
 *
 * @param progressHandler 下载进度。会持续回调更新进度, 直接下载完成。如果为 nil 则表示不关心进度。
 * @param handler 结果回调。回调参数:
 *
 * - data 文件数据;
 * - objectId 消息msgId;
 * - error 不为nil表示出错;
 *
 * 如果 error 为 ni, data 也为 nil, 表示没有数据.
 *
 * @discussion 如果本地数据文件已经存在, 则直接返回;如果本地还没有文件，会发起网络请求下载。下载完后再回调。
 */
- (void)videoDataWithProgress:(JMSGMediaProgressHandler JMSG_NULLABLE)progressHandler
            completionHandler:(JMSGAsyncDataHandler JMSG_NULLABLE)handler;


JMSG_ASSUME_NONNULL_END

@end
