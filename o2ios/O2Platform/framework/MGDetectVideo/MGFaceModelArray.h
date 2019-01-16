//
//  MGFaceModelArray.h
//  LandMask
//
//  Created by 张英堂 on 16/8/17.
//  Copyright © 2016年 megvii. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <CoreMedia/CoreMedia.h>

#import "MGFaceInfo.h"


@interface MGFaceModelArray : NSObject

@property (nonatomic, strong) NSMutableArray <MGFaceInfo*>* faceArray;

@property (nonatomic, assign) NSUInteger count;
@property (nonatomic, assign) CGFloat timeUsed;
@property (nonatomic, assign) CGFloat AttributeTimeUsed;

@property (nonatomic, assign) CGRect detectRect;

@property (nonatomic, assign) NSInteger pointsCount;


@property (nonatomic, assign) BOOL getFaceInfo;
@property (nonatomic, assign) BOOL get3DInfo;


- (void)addModel:(MGFaceInfo *)model;

- (MGFaceInfo *)modelWithIndex:(NSUInteger)index;


- (NSString *)getDebugString;

@end
