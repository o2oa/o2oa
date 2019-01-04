//
//  JMSGLocationContent.h
//  JMessage
//
//  Created by 邓永豪 on 16/7/26.
//  Copyright © 2016年 HXHG. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <JMessage/JMSGAbstractContent.h>

/**
 *  地理位置内容类型
 */
@interface JMSGLocationContent : JMSGAbstractContent <NSCopying>

JMSG_ASSUME_NONNULL_BEGIN

/*!
 * @abstract 纬度
 */
@property(nonatomic, strong, readonly) NSNumber *latitude;

/*!
 * @abstract 经度
 */
@property(nonatomic, strong, readonly) NSNumber *longitude;

/*!
 * @abstract 缩放
 */
@property(nonatomic, strong, readonly) NSNumber *scale;

/*!
 * @abstract 详细地址信息
 */
@property(nonatomic, copy, readonly) NSString *address;

// 不支持使用的初始化方法
- (nullable instancetype)init NS_UNAVAILABLE;

/**
 *  初始化地理位置消息内容
 *
 *  @param latitude  纬度
 *  @param longitude 经度
 *  @param scale     缩放比例
 *  @param address   详细地址信息
 *
 *  @return 地理位置消息内容
 */
- (instancetype)initWithLatitude:(NSNumber *)latitude
                       longitude:(NSNumber *)longitude
                           scale:(NSNumber *)scale
                        address:(NSString *)address;

JMSG_ASSUME_NONNULL_END

@end
