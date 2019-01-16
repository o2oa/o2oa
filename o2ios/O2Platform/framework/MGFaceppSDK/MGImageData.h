//
//  MGImageData.h
//  MGFacepp
//
//  Created by 张英堂 on 2016/12/27.
//  Copyright © 2016年 megvii. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreMedia/CoreMedia.h>

@interface MGImageData : NSObject

/** 请不要使用该方法初始化 */
- (instancetype)init DEPRECATED_ATTRIBUTE;

/**
 初始化方法，二选一

 @param sampleBuffer 视频流帧
 @return 实例化对象
 */
- (instancetype)initWithSampleBuffer:(CMSampleBufferRef)sampleBuffer;

/**
 初始化方法，二选一


 @param image UIImage 对象
 @return 实例化对象
 */
- (instancetype)initWithImage:(UIImage *)image;


/**
 图片宽度
 */
@property (nonatomic, assign) CGFloat width;

/**
 图片高度
 */
@property (nonatomic, assign) CGFloat height;


/**
 是否为图片
 */
@property (nonatomic, assign, readonly) BOOL isUIImage;


/**
 获取内存地址

 @return 内存地址
 */
- (const char*)getData;


/**
 释放内存
 */
- (void)releaseImageData;

@end
