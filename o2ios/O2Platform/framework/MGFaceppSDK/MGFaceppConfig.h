//
//  MGFaceppConfig.h
//  MGFacepp
//
//  Created by 张英堂 on 2016/12/27.
//  Copyright © 2016年 megvii. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "MGFaceppCommon.h"

#pragma mark - 设置检测参数

@interface MGFaceppConfig : NSObject

/**  人脸大小 默认 100 */
@property (nonatomic, assign) int minFaceSize;

/**  重新全局检测间隔 40 */
@property (nonatomic, assign) int interval;

/** 旋转角度 defalut 0, [0,90,180,270,360] */
@property (nonatomic, assign) int orientation;


/**  设置Detection类型 默认:MGFppDetectionModeNormal */
@property (nonatomic, assign) MGFppDetectionMode detectionMode;

/**  设置检测区域（为视频流原图的区域），默认全图检测 */
@property (nonatomic, assign) MGDetectROI detectROI;

/** 设置视频流格式，默认 PixelFormatTypeRGBA */
@property (nonatomic, assign) MGPixelFormatType pixelFormatType;


@property (nonatomic, assign) float faceConfidenceFilter;



@end
