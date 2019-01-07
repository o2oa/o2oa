//
//  JMSGFileContent.h
//  JMessage
//
//  Created by deng on 16/7/4.
//  Copyright © 2016年 HXHG. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <JMessage/JMSGMediaAbstractContent.h>

/*!
 * 文件内容类型
 */
@interface JMSGFileContent : JMSGMediaAbstractContent<NSCopying>

JMSG_ASSUME_NONNULL_BEGIN

/*!
 * @abstract 文件名
 */
@property(nonatomic, copy, readonly) NSString *fileName;

/*!
 * @abstract 文件格式
 *
 * 注意：格式后缀不需要带点，只需后缀名，如：pdf、doc 等
 */
@property(nonatomic, strong) NSString * JMSG_NULLABLE format;

// 不支持使用的初始化方法
- (nullable instancetype)init NS_UNAVAILABLE;

/**
 *  初始化文件内容
 *
 *  @param data     文件数据
 *  @param fileName 文件名
 *
 */
- (instancetype)initWithFileData:(NSData *)data
                        fileName:(NSString *)fileName;

/*!
 * @abstract 获取文件内容的数据
 */
- (void)fileData:(JMSGAsyncDataHandler)handler;

/*!
 * @abstract 获取文件内容的数据
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
 * @discussion
 * 如果本地数据文件已经存在, 则直接返回;
 * 如果本地还没有文件，会发起网络请求下载。下载完后再回调。
 */
- (void)fileDataWithProgress:(JMSGMediaProgressHandler JMSG_NULLABLE)progressHandler
           completionHandler:(JMSGAsyncDataHandler JMSG_NULLABLE)handler;

JMSG_ASSUME_NONNULL_END

@end
