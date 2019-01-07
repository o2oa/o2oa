//
//  MGDetectRect.h
//  MGFacepp
//
//  Created by Megvii on 2017/10/18.
//  Copyright © 2017年 megvii. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger, MGOrientation) {
    MGOrientationLeft,
    MGOrientationUp,
    MGOrientationRight,
    MGOrientationDown,
};

@interface MGDetectRectInfo : NSObject

@property (nonatomic, assign) float confidence;

@property (nonatomic, assign) float angle;

@property (nonatomic, assign) MGOrientation orient;

@property (nonatomic, assign) CGRect rect;

@end

