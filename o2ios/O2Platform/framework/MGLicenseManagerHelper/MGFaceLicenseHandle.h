//
//  MGLicenseHandle.h
//  MGSDKV2Test
//
//  Created by 张英堂 on 16/9/7.
//  Copyright © 2016年 megvii. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "MGLicenseManager.h"
#import "MGNetAccount.h"



@interface MGFaceLicenseHandle : NSObject


/**
 *  获取当前SDK是否授权--- 子类需要重写该方法，通过该类获取的 是否授权无法全部包括使用的SDK
 *
 *  @return 是否授权
 */
+ (BOOL)getLicense;

+ (NSDate *)getLicenseDate;

/**
 *  只有当授权时间少于 1天的时候，才会进行授权操作
 *
 */
+ (void)licenseForNetwokrFinish:(void(^)(bool License, NSDate *sdkDate))finish;


/**
 获取 face SDK 是否需要联网授权

 @return 是否为联网授权版本
 */
+ (BOOL)getNeedNetLicense;




@end
