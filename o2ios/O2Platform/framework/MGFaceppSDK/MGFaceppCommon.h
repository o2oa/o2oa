//
//  MGFaceppConfig.h
//  MGLandMark
//
//  Created by 张英堂 on 16/9/5.
//  Copyright © 2016年 megvii. All rights reserved.
//

#ifndef MGFaceppConfig_h
#define MGFaceppConfig_h

#import <UIKit/UIKit.h>

#define KMGFACEMODELNAME @"megviifacepp_0_5_2_model"
#define KMGFACEMODELTYPE @""


/** 设置检测视频帧的类型  */
typedef NS_ENUM(NSInteger, MGPixelFormatType) {
    PixelFormatTypeGRAY = 0,
    PixelFormatTypeBGR,
    PixelFormatTypeNV21,
    PixelFormatTypeRGBA,
    PixelFormatTypeRGB
};

/** face SDK 功能类型  */
typedef NS_ENUM(NSInteger, MGFaceAbility) {
    MGFaceAbilityTrack = 0,
    MGFaceAbilityDetect = 1,
    MGFaceAbilityPose3d = 2,
    MGFaceAbilityEyeStatus = 3,
    MGFaceAbilityMouseStatus = 4,
    MGFaceAbilityMinority = 5,
    MGFaceAbilityBlurness = 6,
    MGFaceAbilityAgeGender = 7,
    MGFaceAbilityExtractFeature = 8,
};


/** 人脸检测框 */
typedef struct {
    int left;
    int top;
    int right;
    int bottom;
}MGDetectROI;

CG_INLINE MGDetectROI MGDetectROIMake(int left, int top, int right,int bottom){
    MGDetectROI d;
    d.left = left;
    d.top = top;
    d.right = right;
    d.bottom = bottom;
    return d;
}

typedef NS_ENUM(NSUInteger ,MGFaceppStatus) {
    MGMarkPrepareWork = 1, //初始化已结束， 准备工作
    MGMarkWorking,          //正在检测中
    MGMarkWaiting,          //上一帧已经结束，等待下一帧输入
    MGMarkStopped           //检测器停止检测，等待释放
};

typedef NS_ENUM(NSUInteger ,MGFppDetectionMode) {
    MGFppDetectionModeDetect = 0,
    MGFppDetectionModeTracking = 1, // 此模式已经废弃，请使用 robust 模式
    MGFppDetectionModeTrackingFast = 3,
    MGFppDetectionModeTrackingRobust = 4,
    MGFppDetectionModeDetectRect = 5,
    MGFppDetectionModeTrackingRect = 6,
};


typedef NS_ENUM(NSUInteger ,MGEyeStatus) {
    MGEyeStatusNoGlassesOpen = 0,
    MGEyeStatusNoGlassesClose = 1,
    MGEyeStatusNormalGlassesOpen = 2,   //普通眼镜
    MGEyeStatusNormalGlassesClose = 3,
    MGEyeStatuoDarkGlasses = 4,         //太阳镜
    MGEyeStatusOtherOcclusion = 5,
    MGEyeStatusCount = 6
};

typedef NS_ENUM(NSUInteger ,MGMouthStatus) {
    MGMouthStatusOpen = 0,
    MGMouthStatusClose = 1,
    MGMouthStatusMaskOrRespopator = 2,   //
    MGMouthStatusOtherOcclusion = 3,
    MGMouthStatusCount = 4,         //
};





#endif /* MGFaceppConfig_h */
