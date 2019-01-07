//
//  MGBaseDefine.h
//  MGBaseAPI
//
//  Created by 张英堂 on 15/12/21.
//  Copyright © 2015年 megvii. All rights reserved.
//

#ifndef MGBaseDefine_h
#define MGBaseDefine_h

#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>


/* 屏幕宽度 （区别于viewcontroller.view.fream）*/
#define MG_WIN_WIDTH  [UIScreen mainScreen].bounds.size.width

/* 屏幕高度 （区别于viewcontroller.view.fream）*/
#define MG_WIN_HEIGHT [UIScreen mainScreen].bounds.size.height

/* 手机系统版本 */
#define MG_IOS_SysVersion [[UIDevice currentDevice] systemVersion].floatValue

/* rgb颜色转换（16进制->10进制）*/
#define MGColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

/* rgba颜色 */
#define MGColorWithRGB(R, G, B, A) [UIColor colorWithRed:R/255.0f green:G/255.0f blue:B/255.0f alpha:A]


#ifdef DEBUG
#define MGLog(...) NSLog(__VA_ARGS__)
#else
#define MGLog(...)
#endif

typedef void(^VoidBlock)();
typedef void(^VoidErrorBlock)(NSError *error);
typedef void(^videoOutputBlock)(AVCaptureOutput *captureOutput,
                                CMSampleBufferRef sampleBuffer,
                                AVCaptureConnection *connection);




#endif /* MGBaseDefine_h */
