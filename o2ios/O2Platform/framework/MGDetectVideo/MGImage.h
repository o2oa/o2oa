//
//  MGImage.h
//  MGBankCard
//
//  Created by 张英堂 on 15/12/11.
//  Copyright © 2015年 megvii. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <CoreMedia/CoreMedia.h>
#import <AVFoundation/AVFoundation.h>

@interface MGImage : NSObject

/**
 *  裁剪图片
 *
 *  @param image  原始图片
 *  @param bounds 裁剪的区域
 *
 *  @return 裁剪后的图片
 */
+ (UIImage*)croppedImage:(UIImage *)image
                      rect:(CGRect)bounds;

/**
 *  修正图片方向
 *
 *  @param image 原始图片
 *
 *  @return 修正完成的图片
 */
+ (UIImage*)fixOrientationWithImage:(UIImage *)image;


//+ (UIImage*)fixOrientationWithImageOrientation:(UIImage *)image
//                                   orientation:(UIImageOrientation) orientation;

/**
 *  把sampleBuffer转化为图片
 *
 *  @param sampleBuffer sampleBuffer
 *  @param orientation  图片的方向
 *
 *  @return 转化完成的图片
 */
+ (UIImage*)imageFromSampleBuffer:(CMSampleBufferRef)sampleBuffer
                         orientation:(UIImageOrientation) orientation;


//+ (UIImage*)fixOrientationFromSampleBuffer:(CMSampleBufferRef)sampleBuffer
//                                  orientation:(UIImageOrientation)orientation;



/*视频角度转化为数值*/
+ (CGFloat)angleOffsetFromPortraitOrientationToOrientation:(AVCaptureVideoOrientation)orientation;



@end


