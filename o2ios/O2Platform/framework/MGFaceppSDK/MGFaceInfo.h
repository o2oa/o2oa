//
//  MGFaceModel.h
//  LandMask
//
//  Created by 张英堂 on 16/7/11.
//  Copyright © 2016年 megvii. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "MGFaceppCommon.h"

typedef NS_ENUM(NSInteger, MGGender) {
    MGFemale = 0,
    MGMale = 1,
};

@interface MGFaceInfo : NSObject

/** tracking ID */
@property (nonatomic, assign) NSInteger trackID;

/** 在该张图片中人脸序号 */
@property (nonatomic, assign) int index;

/** 人脸的rect */
@property (nonatomic, assign) CGRect rect;

/** 人脸点坐标 （NSValue -> CGPoints）*/
@property (nonatomic, strong) NSArray <NSValue *>*points;

/** 该张人脸质量 */
@property (nonatomic, assign) CGFloat confidence;


#pragma mark 以下属性需要SDK版本支持 请使用 [MGAlgorithmInfo SDKAbility] 属性，获取SDK支持功能

#pragma mark 需要主动调用 MGFacepp 相关方法获取
//3D info
@property (nonatomic, assign) float pitch;
@property (nonatomic, assign) float yaw;
@property (nonatomic, assign) float roll;

/** 年龄 */
@property (nonatomic, assign) float age;
/** 性别 */
@property (nonatomic, assign) MGGender gender;
/** blurness */
@property (nonatomic, assign) float blurness;
/** minority */
@property (nonatomic, assign) float minority;

/** 眼状态 */
@property (nonatomic, assign) MGEyeStatus leftEyesStatus;
@property (nonatomic, assign) MGEyeStatus rightEyesStatus;

/** 嘴状态 */
@property (nonatomic, assign) MGMouthStatus mouseStatus;


#pragma mark -

/**
 人脸的 feature
 */
@property (nonatomic, strong, readonly) NSData *featureData;



@end
