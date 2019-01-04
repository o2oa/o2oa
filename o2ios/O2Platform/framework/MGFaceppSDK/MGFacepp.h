//
//  MGFacepp.h
//  LandMask
//
//  Created by 张英堂 on 16/9/5.
//  Copyright © 2016年 megvii. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreMedia/CoreMedia.h>

#import "MGFaceppConfig.h"
#import "MGImageData.h"
#import "MGFaceppCommon.h"
#import "MGAlgorithmInfo.h"
#import "MGFaceInfo.h"
#import "MGDetectRectInfo.h"

@interface MGFacepp : NSObject

- (MGFaceppConfig *)getFaceppConfig;

- (instancetype)init DEPRECATED_ATTRIBUTE;

/**
 *  初始化方法 必须使用该方法初始化，否则会导致初始化失败。
 *
 *  @param modelData        model data
 *  @param config           设置的callback
 *  @return 实例化
 */
- (instancetype)initWithModel:(NSData *)modelData
                faceppSetting:(void(^)(MGFaceppConfig *config))config;

/**
 初始化方法

 @param modelData        model data
 @param maxFaceCount     一张图像中识别的最大人脸数，设置为1即为单脸跟踪
 @param config           设置的callback
 @return handle
 */
- (instancetype)initWithModel:(NSData *)modelData maxFaceCount:(NSInteger)maxFaceCount faceppSetting:(void(^)(MGFaceppConfig *config))config;

/**
 *  @param config        更新设置参数
 */
- (BOOL)updateFaceppSetting:(void(^)(MGFaceppConfig *config))config;


/**
 *  获取检测器目前状态
 */
@property (nonatomic, assign, readonly) MGFaceppStatus status;


#pragma mark - 检测人脸信息
/**
 *  检测人脸信息
 *
 *  @param imagedata  检测的图片
 *  @return 检测结果（如果为 nil 时候，检测器异常，检测失败，请检测代码以及设置）
 */
- (NSArray <MGFaceInfo *>*)detectWithImageData:(MGImageData *)imagedata;

/**
 *  检测人脸框
 *
 *  @param imagedata  检测的图片
 *  @return 检测结果（如果为 nil 时候，检测器异常，检测失败，请检测代码以及设置）
 */
- (NSInteger)getFaceNumberWithImageData:(MGImageData *)imagedata;


/**
 *  人脸关键点平滑
 *
 *  @param faceInfo faceinfo model
 *  @param isSmooth 是否关键点平滑，防止人脸抖动
 *  @param nr       关键点个数
 */
- (BOOL)GetGetLandmark:(MGFaceInfo *)faceInfo isSmooth:(BOOL)isSmooth pointsNumber:(int)nr;

/**
 *  获取人脸框
 *  
 *
 *  @param index faceinfo model
 *  @param isSmooth 是否关键点平滑，防止人脸抖动
 */
- (MGDetectRectInfo *)GetRectAtIndex:(int)index isSmooth:(BOOL)isSmooth;

/**
 *  获取人脸 3D信息
 *  @param faceInfo faceInfo
 *  @return 是否获取成功
 */
- (BOOL)GetAttribute3D:(MGFaceInfo *)faceInfo;

/**
 *  获取 眼状态
 *  @param faceInfo faceInfo
 *  @return 是否获取成功
 */
- (BOOL)GetAttributeEyeStatus:(MGFaceInfo *)faceInfo;

/**
 *  获取 嘴状态
 *  @param faceInfo faceInfo
 *  @return 是否获取成功
 */
- (BOOL)GetAttributeMouseStatus:(MGFaceInfo *)faceInfo;

/**
 *  获取 年龄
 *  @param faceInfo faceInfo
 *  @return 是否获取成功
 */
- (BOOL)GetAttributeAgeGenderStatus:(MGFaceInfo *)faceInfo;

/**
 获取 Blurness 状态

 @param faceInfo faceInfo
 @return 是否获取成功
 */
- (BOOL)GetBlurnessStatus:(MGFaceInfo *)faceInfo;

/**
 获取 Minorit 状态

 @param faceInfo faceInfo
 @return 是否获取成功
 */
- (BOOL)GetMinorityStatus:(MGFaceInfo *)faceInfo;

/**
 获取人脸的 Feature 数据

 @param faceInfo faceinfo
 @return 是否获取成功
 */
- (BOOL)GetFeatureData:(MGFaceInfo *)faceInfo;

/**
 比较两个人脸相似度， 必须改 MGFaceInfo 获取过 Feature 数据才有效
 如果成功返回人脸相似度， 如果比对失败，返回 -1.0
 
 @param faceInfo MGFaceInfo 1
 @param faceInf2 MGFaceInfo 2
 @return 比对相似度
 */
- (float)faceCompareWithFaceInfo:(MGFaceInfo *)faceInfo faceInf2:(MGFaceInfo *)faceInf2;

/**
 比较两个人脸相似度
 如果成功返回人脸相似度， 如果比对失败，返回 -1.0

 @param featureData featureData 1
 @param featureData2 featureData 2
 @return 相似度
 */
- (float)faceCompareWithFeatureData:(NSData *)featureData featureData2:(NSData *)featureData2;


/** 开启检测新的一帧，在每次调用 detectWithImageData: 之前调用。 */
- (void)beginDetectionFrame;

/** 停止当前改帧的检测，在获取人脸详细信息后，需要主动调用该方法结束当前帧，以便进入下一帧 */
- (void)endDetectionFrame;


/**
 释放算法资源
 算法在计算时需要占用一些内存资源，必须在所有算法的句柄（handle）被释放后再调用
 
 @return 成功则返回 YES
 */
- (BOOL)shutDown;

#pragma mark - 类方法，获取 SDK 相关信息


/**
 获取版本号

 @return 版本号
 */
+ (NSString *)getSDKVersion;


/**
 获取 SDK jenkins 号

 @return SDK jenkins 号
 */
+ (NSString *)getJenkinsNumber;


/**
 清除track缓存

 @return 成功则返回 YES
 */
- (BOOL)resetTrack;


/**
 获取 SDK 相关信息

 @param modelData model data
 @return sdk 相关信息
 */
+ (MGAlgorithmInfo *)getSDKAlgorithmInfoWithModel:(NSData *)modelData;;




@end




